package com.enonic.xp.core.impl.app.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.app.ApplicationAdaptor;
import com.enonic.xp.core.impl.app.ApplicationFactoryService;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.resource.MultiResourceProcessor;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.vfs.VirtualFile;

@Component(immediate = true)
public final class ResourceServiceImpl
    implements ResourceService
{
    private static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    private final ConcurrentMap<ProcessingKey, ProcessingEntry> cache;

    private final ConcurrentMap<ProcessingKey, MultiProcessingEntry> multiCache;

    private final ApplicationFactoryService applicationFactoryService;

    @Activate
    public ResourceServiceImpl( @Reference final ApplicationFactoryService applicationFactoryService )
    {
        this.cache = new ConcurrentHashMap<>();
        this.multiCache = new ConcurrentHashMap<>();
        this.applicationFactoryService = applicationFactoryService;
    }

    @Override
    public Resource getResource( final ResourceKey key )
    {
        return findResource( findApplicationUrlResolver( key.getApplicationKey() ), key );
    }

    private static Resource findResource( final Optional<ApplicationUrlResolver> urlResolver, final ResourceKey key )
    {
        return urlResolver.map( resolver -> resolver.findResource( key.getPath() ) ).orElse( new UrlResource( key, null ) );
    }

    private static boolean isStable( final Optional<ApplicationUrlResolver> urlResolver )
    {
        return urlResolver.filter( resolver -> resolver instanceof BundleApplicationUrlResolver ).isPresent();
    }

    @Override
    public ResourceKeys findFiles( final ApplicationKey key, final String pattern )
    {
        final Pattern compiled = Pattern.compile( pattern );

        return findApplicationUrlResolver( key ).map( ApplicationUrlResolver::findFiles )
                                      .orElse( Set.of() )
                                      .stream()
                                      .map( name -> ResourceKey.from( key, name ) )
                                      .filter( rk -> compiled.matcher( rk.getPath() ).find() )
            .collect( ResourceKeys.collector() );
    }

    private Optional<ApplicationUrlResolver> findApplicationUrlResolver( final ApplicationKey key )
    {
        final String resolverSource = (String) ContextAccessor.current().getAttribute( ResourceConstants.RESOURCE_SOURCE_ATTRIBUTE );
        return applicationFactoryService.findResolver( effectiveKey( key ), resolverSource );
    }

    private static ApplicationKey effectiveKey( final ApplicationKey key )
    {
        return ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( key ) ? SYSTEM_APPLICATION_KEY : key;
    }

    private BundleStamp bundleStamp( final ApplicationKey key )
    {
        return applicationFactoryService.findActiveBundle( effectiveKey( key ) ).map( BundleStamp::from ).orElse( null );
    }

    @Override
    public <K, V> V processResource( final ResourceProcessor<K, V> processor )
    {
        final ResourceKey resourceKey = processor.toResourceKey();
        final ProcessingEntry entry = this.cache.compute( new ProcessingKey( processor.getSegment(), processor.getKey() ), ( k, v ) -> {
            // stamp is read before the resource: a redeploy racing in-between costs a recompute, never staleness
            final BundleStamp stamp = bundleStamp( resourceKey.getApplicationKey() );
            final Optional<ApplicationUrlResolver> urlResolver = findApplicationUrlResolver( resourceKey.getApplicationKey() );
            final boolean stable = isStable( urlResolver );

            final boolean sameStamp = v != null && Objects.equals( v.stamp, stamp );
            if ( sameStamp && stable && v.stable )
            {
                // resources come straight from the immutable bundle: a new bundle is the only signal that
                // files may be new - their timestamps prove nothing (constant with reproducible builds)
                return v;
            }

            final Resource resource = findResource( urlResolver, resourceKey );
            if ( sameStamp && resource.exists() && resource.getTimestamp() == v.timestamp )
            {
                return v;
            }

            final V value = processor.process( resource );
            if ( value == null )
            {
                return null;
            }
            return new ProcessingEntry( value, resource.getTimestamp(), stamp, stable );
        } );

        return entry != null ? (V) entry.value : null;
    }

    @Override
    public <K, V> V processResources( final MultiResourceProcessor<K, V> processor )
    {
        final List<ResourceKey> resourceKeys = processor.toResourceKeys();
        final MultiProcessingEntry entry =
            this.multiCache.compute( new ProcessingKey( processor.getSegment(), processor.getKey() ), ( k, v ) -> {
                // stamps are read before the resources: a redeploy racing in-between costs a recompute, never staleness
                final List<ApplicationKey> applicationKeys =
                    resourceKeys.stream().map( ResourceKey::getApplicationKey ).distinct().toList();

                final List<BundleStamp> stamps = new ArrayList<>( applicationKeys.size() );
                final Map<ApplicationKey, Optional<ApplicationUrlResolver>> urlResolvers = new HashMap<>();
                boolean stable = true;
                for ( final ApplicationKey applicationKey : applicationKeys )
                {
                    stamps.add( bundleStamp( applicationKey ) );
                    final Optional<ApplicationUrlResolver> urlResolver = findApplicationUrlResolver( applicationKey );
                    urlResolvers.put( applicationKey, urlResolver );
                    stable = stable && isStable( urlResolver );
                }

                final boolean sameStamps = v != null && v.stamps.equals( stamps );
                if ( sameStamps && stable && v.stable )
                {
                    // resources come straight from the immutable bundles: a new bundle is the only signal that
                    // files may be new - their timestamps prove nothing (constant with reproducible builds)
                    return v;
                }

                final List<Resource> resources = new ArrayList<>( resourceKeys.size() );
                final long[] timestamps = new long[resourceKeys.size()];
                for ( int i = 0; i < resourceKeys.size(); i++ )
                {
                    final ResourceKey resourceKey = resourceKeys.get( i );
                    final Resource resource = findResource( urlResolvers.get( resourceKey.getApplicationKey() ), resourceKey );
                    resources.add( resource );
                    timestamps[i] = resource.getTimestamp();
                }

                if ( sameStamps && Arrays.equals( v.timestamps, timestamps ) )
                {
                    return v;
                }

                final V value = processor.process( resources );
                if ( value == null )
                {
                    return null;
                }
                return new MultiProcessingEntry( value, timestamps, stamps, stable );
            } );

        return entry != null ? (V) entry.value : null;
    }

    @Override
    public VirtualFile getVirtualFile( final ResourceKey resourceKey )
    {
        return this.applicationFactoryService.findActiveApplication( resourceKey.getApplicationKey() )
            .map( ApplicationAdaptor::getBundle )
            .map( b -> (VirtualFile) new BundleResource( b, resourceKey.getPath() ) )
            .orElseGet( () -> new NullResource( resourceKey.getPath() ) );
    }
}
