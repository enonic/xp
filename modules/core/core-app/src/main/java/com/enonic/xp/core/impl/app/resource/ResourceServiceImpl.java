package com.enonic.xp.core.impl.app.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.enonic.xp.core.impl.app.ApplicationGeneration;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.resource.MultiResourceProcessor;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.vfs.VirtualFile;

/**
 * Serves the resources of applications and caches what is derived from them.
 * <p>
 * A cached value is stamped with the {@link CacheStamp} of its application: the incarnation of the bundle and the
 * {@link ApplicationGeneration generation}, which every change of the persisted descriptor or schema moves. While the stamp holds and
 * the resolver is {@link ApplicationUrlResolver#isStable() stable}, a hit costs nothing: neither the key translation nor the
 * resource is looked at again. An unstable resolver (the classloader of an application in development) has the timestamp of its
 * resource checked on every use, as the files change under it without any signal.
 * <p>
 * The caches are plain maps and a value is computed outside of any lock: a processor may itself read other resources through this
 * service (a content type inlining its form fragments), and two threads computing the same value at once just agree on it.
 */
@Component(immediate = true)
public final class ResourceServiceImpl
    implements ResourceService
{
    private static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    private final ConcurrentMap<ProcessingKey, ProcessingEntry> cache = new ConcurrentHashMap<>();

    private final ConcurrentMap<ProcessingKey, MultiProcessingEntry> multiCache = new ConcurrentHashMap<>();

    private final ConcurrentMap<FilesKey, FilesEntry> filesCache = new ConcurrentHashMap<>();

    private final ApplicationFactoryService applicationFactoryService;

    private final ApplicationGeneration applicationGeneration;

    @Activate
    public ResourceServiceImpl( @Reference final ApplicationFactoryService applicationFactoryService,
                                @Reference final ApplicationGeneration applicationGeneration )
    {
        this.applicationFactoryService = applicationFactoryService;
        this.applicationGeneration = applicationGeneration;
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
        return urlResolver.filter( ApplicationUrlResolver::isStable ).isPresent();
    }

    @Override
    public ResourceKeys findFiles( final ApplicationKey key, final String pattern )
    {
        final Pattern compiled = Pattern.compile( pattern );

        return files( key ).stream()
            .map( name -> ResourceKey.from( key, name ) )
            .filter( rk -> compiled.matcher( rk.getPath() ).find() )
            .collect( ResourceKeys.collector() );
    }

    // the files of the application, listed once per stamp for a stable resolver
    private Set<String> files( final ApplicationKey key )
    {
        final FilesKey filesKey = new FilesKey( effectiveKey( key ), resolverSource() );
        final FilesEntry cached = filesCache.get( filesKey );
        // the stamp is read before the files: a change racing in-between costs a listing, never staleness
        final CacheStamp stamp = stamp( key );
        if ( cached != null && cached.stamp().equals( stamp ) )
        {
            return cached.files();
        }

        final Optional<ApplicationUrlResolver> urlResolver = findApplicationUrlResolver( key );
        final Set<String> files = urlResolver.map( ApplicationUrlResolver::findFiles ).map( Set::copyOf ).orElse( Set.of() );
        if ( isStable( urlResolver ) )
        {
            filesCache.put( filesKey, new FilesEntry( files, stamp ) );
        }
        else
        {
            filesCache.remove( filesKey );
        }
        return files;
    }

    private record FilesKey(ApplicationKey applicationKey, String source)
    {
    }

    private static String resolverSource()
    {
        return (String) ContextAccessor.current().getAttribute( ResourceConstants.RESOURCE_SOURCE_ATTRIBUTE );
    }

    private Optional<ApplicationUrlResolver> findApplicationUrlResolver( final ApplicationKey key )
    {
        return applicationFactoryService.findResolver( effectiveKey( key ), resolverSource() );
    }

    private static ApplicationKey effectiveKey( final ApplicationKey key )
    {
        return ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( key ) ? SYSTEM_APPLICATION_KEY : key;
    }

    private CacheStamp stamp( final ApplicationKey key )
    {
        final ApplicationKey effectiveKey = effectiveKey( key );
        final BundleStamp bundleStamp = applicationFactoryService.findActiveBundle( effectiveKey ).map( BundleStamp::from ).orElse( null );
        return new CacheStamp( bundleStamp, applicationGeneration.of( effectiveKey ) );
    }

    @Override
    public <K, V> V processResource( final ResourceProcessor<K, V> processor )
    {
        final ProcessingKey cacheKey = new ProcessingKey( processor.getSegment(), processor.getKey() );

        final ProcessingEntry cached = this.cache.get( cacheKey );
        if ( cached != null && isCurrent( cached ) )
        {
            return (V) cached.value();
        }

        // the stamp is read before the resource: a change racing in-between costs a recompute, never staleness
        final ResourceKey resourceKey = processor.toResourceKey();
        final CacheStamp stamp = stamp( resourceKey.getApplicationKey() );
        final Optional<ApplicationUrlResolver> urlResolver = findApplicationUrlResolver( resourceKey.getApplicationKey() );
        final Resource resource = findResource( urlResolver, resourceKey );

        final V value = processor.process( resource );
        if ( value == null )
        {
            this.cache.remove( cacheKey );
            return null;
        }
        this.cache.put( cacheKey, new ProcessingEntry( value, resourceKey, resource.getTimestamp(), stamp, isStable( urlResolver ) ) );
        return value;
    }

    private boolean isCurrent( final ProcessingEntry entry )
    {
        final ApplicationKey applicationKey = entry.resourceKey().getApplicationKey();
        if ( !entry.stamp().equals( stamp( applicationKey ) ) )
        {
            return false;
        }
        if ( entry.stable() )
        {
            // resources come straight from the immutable bundle or from nodes every write of which moves the generation:
            // the stamp is the only signal that they may be new - timestamps prove nothing (constant with reproducible builds)
            return true;
        }
        final Resource resource = findResource( findApplicationUrlResolver( applicationKey ), entry.resourceKey() );
        return resource.exists() && resource.getTimestamp() == entry.timestamp();
    }

    @Override
    public <K, V> V processResources( final MultiResourceProcessor<K, V> processor )
    {
        final ProcessingKey cacheKey = new ProcessingKey( processor.getSegment(), processor.getKey() );

        final MultiProcessingEntry cached = this.multiCache.get( cacheKey );
        if ( cached != null && isCurrent( cached ) )
        {
            return (V) cached.value();
        }

        // stamps are read before the resources: a change racing in-between costs a recompute, never staleness
        final List<ResourceKey> resourceKeys = processor.toResourceKeys();
        final List<ApplicationKey> applicationKeys = applicationKeys( resourceKeys );
        final List<CacheStamp> stamps = applicationKeys.stream().map( this::stamp ).toList();

        final Map<ApplicationKey, Optional<ApplicationUrlResolver>> urlResolvers = new HashMap<>();
        boolean stable = true;
        for ( final ApplicationKey applicationKey : applicationKeys )
        {
            final Optional<ApplicationUrlResolver> urlResolver = findApplicationUrlResolver( applicationKey );
            urlResolvers.put( applicationKey, urlResolver );
            stable = stable && isStable( urlResolver );
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

        final V value = processor.process( resources );
        if ( value == null )
        {
            this.multiCache.remove( cacheKey );
            return null;
        }
        this.multiCache.put( cacheKey, new MultiProcessingEntry( value, resourceKeys, timestamps, stamps, stable ) );
        return value;
    }

    private boolean isCurrent( final MultiProcessingEntry entry )
    {
        final List<ApplicationKey> applicationKeys = applicationKeys( entry.resourceKeys() );
        if ( !entry.stamps().equals( applicationKeys.stream().map( this::stamp ).toList() ) )
        {
            return false;
        }
        if ( entry.stable() )
        {
            return true;
        }
        final Map<ApplicationKey, Optional<ApplicationUrlResolver>> urlResolvers = new HashMap<>();
        for ( final ApplicationKey applicationKey : applicationKeys )
        {
            urlResolvers.put( applicationKey, findApplicationUrlResolver( applicationKey ) );
        }
        final List<ResourceKey> resourceKeys = entry.resourceKeys();
        for ( int i = 0; i < resourceKeys.size(); i++ )
        {
            final ResourceKey resourceKey = resourceKeys.get( i );
            final Resource resource = findResource( urlResolvers.get( resourceKey.getApplicationKey() ), resourceKey );
            if ( resource.getTimestamp() != entry.timestamps()[i] )
            {
                return false;
            }
        }
        return true;
    }

    private static List<ApplicationKey> applicationKeys( final List<ResourceKey> resourceKeys )
    {
        return resourceKeys.stream().map( ResourceKey::getApplicationKey ).distinct().toList();
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
