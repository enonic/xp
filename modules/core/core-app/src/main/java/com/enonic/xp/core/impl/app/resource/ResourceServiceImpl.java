package com.enonic.xp.core.impl.app.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.app.ApplicationAdaptor;
import com.enonic.xp.core.impl.app.ApplicationFactoryService;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.ResourcesProcessor;
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
        return findApplicationUrlResolver( key.getApplicationKey() ).map( urlResolver -> urlResolver.findResource( key.getPath() ) )
            .orElse( new UrlResource( key, null ) );
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
            final Resource resource = this.getResource( resourceKey );
            if ( v == null || !Objects.equals( v.stamp, stamp ) || !resource.exists() || resource.getTimestamp() != v.timestamp )
            {
                final V value = processor.process( resource );
                if ( value == null )
                {
                    return null;
                }

                return new ProcessingEntry( value, resource.getTimestamp(), stamp );
            }
            else
            {
                return v;
            }
        } );

        return entry != null ? (V) entry.value : null;
    }

    @Override
    public <K, V> V processResources( final ResourcesProcessor<K, V> processor )
    {
        final List<ResourceKey> resourceKeys = processor.toResourceKeys();
        final MultiProcessingEntry entry =
            this.multiCache.compute( new ProcessingKey( processor.getSegment(), processor.getKey() ), ( k, v ) -> {
                // stamps are read before the resources: a redeploy racing in-between costs a recompute, never staleness
                final List<BundleStamp> stamps =
                    resourceKeys.stream().map( ResourceKey::getApplicationKey ).distinct().map( this::bundleStamp ).collect(
                        Collectors.toList() );

                final List<Resource> resources = new ArrayList<>( resourceKeys.size() );
                final long[] timestamps = new long[resourceKeys.size()];
                for ( int i = 0; i < resourceKeys.size(); i++ )
                {
                    final Resource resource = this.getResource( resourceKeys.get( i ) );
                    resources.add( resource );
                    timestamps[i] = resource.getTimestamp();
                }

                if ( v == null || !v.stamps.equals( stamps ) || !Arrays.equals( v.timestamps, timestamps ) )
                {
                    final V value = processor.process( resources );
                    if ( value == null )
                    {
                        return null;
                    }

                    return new MultiProcessingEntry( value, timestamps, stamps );
                }
                else
                {
                    return v;
                }
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
