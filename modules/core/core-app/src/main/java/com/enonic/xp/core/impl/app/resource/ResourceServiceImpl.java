package com.enonic.xp.core.impl.app.resource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.app.ApplicationAdaptor;
import com.enonic.xp.core.impl.app.ApplicationFactoryService;
import com.enonic.xp.core.impl.app.ApplicationHelper;
import com.enonic.xp.core.impl.app.ResourceSource;
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
 * {@link ApplicationUrlResolver#revision() revision} of the resolver, the version of the application node for persisted resources,
 * which every write of the persisted descriptor or schema moves. The stamp is read on every use, so a change written on another
 * node of the cluster is seen by the next read without any event. While the stamp holds, a hit costs the stamp alone: neither the
 * key translation nor the resource is looked at again. Only what is derived from a {@link ApplicationUrlResolver#isStable() stable}
 * resolver is cached. The files of an application in development, served by its classloader, change under it without any signal,
 * and a value holds more than its own resource (a content type with its form fragments inlined, the icon next to a descriptor, the
 * {@code .yaml} looked for before the {@code .yml}): a value derived from them is computed on every read, as their listing is.
 * <p>
 * The bundle and the resolver a value is computed under are of one lookup, see {@link ResourceSource}, and the stamp is read
 * before the resources: a change racing in-between costs a recompute, never staleness. A value is computed outside of any lock and
 * put in the cache afterwards: a processor may itself read other resources through this service (a content type inlining its form
 * fragments), and two threads computing the same value at once just agree on it. The caches are bounded in size and by last
 * access only so that a key nobody reads any more does not hold its value forever; currency is never a matter of age, it is
 * checked on every hit.
 */
@Component(immediate = true)
public final class ResourceServiceImpl
    implements ResourceService
{
    private final Cache<ProcessingKey, ProcessingEntry> cache = newCache();

    private final Cache<ProcessingKey, MultiProcessingEntry> multiCache = newCache();

    private final Cache<FilesKey, FilesEntry> filesCache = newCache();

    private final ApplicationFactoryService applicationFactoryService;

    @Activate
    public ResourceServiceImpl( @Reference final ApplicationFactoryService applicationFactoryService )
    {
        this.applicationFactoryService = applicationFactoryService;
    }

    // bounded so that a key nobody reads any more (an application uninstalled, a descriptor removed) is let go of eventually
    private static <K, V> Cache<K, V> newCache()
    {
        return Caffeine.newBuilder().maximumSize( 100_000 ).expireAfterAccess( Duration.ofHours( 1 ) ).build();
    }

    @Override
    public Resource getResource( final ResourceKey key )
    {
        return findResource( source( key.getApplicationKey() ), key );
    }

    private static Resource findResource( final ResourceSource source, final ResourceKey key )
    {
        final Resource resource = source.resolver() == null ? null : source.resolver().findResource( key.getPath() );
        return resource == null ? new UrlResource( key, null ) : resource;
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
        final FilesEntry cached = filesCache.getIfPresent( filesKey );
        final ResourceSource source = source( key );
        final CacheStamp stamp = stamp( source );
        if ( cached != null && cached.stamp().equals( stamp ) )
        {
            return cached.files();
        }

        final Set<String> files = source.resolver() == null ? Set.of() : Set.copyOf( source.resolver().findFiles() );
        if ( source.isStable() )
        {
            filesCache.put( filesKey, new FilesEntry( files, stamp ) );
        }
        else
        {
            filesCache.invalidate( filesKey );
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

    // the bundle and the resolver of the application as they are now, of one lookup
    private ResourceSource source( final ApplicationKey key )
    {
        return applicationFactoryService.findResourceSource( effectiveKey( key ), resolverSource() );
    }

    private static ApplicationKey effectiveKey( final ApplicationKey key )
    {
        return ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( key ) ? ApplicationHelper.SYSTEM_APPLICATION_KEY : key;
    }

    // the incarnation of the bundle and the revision of the resolver: what the resources are served from is the resolver's to know
    private static CacheStamp stamp( final ResourceSource source )
    {
        return new CacheStamp( source.bundle() == null ? null : BundleStamp.from( source.bundle() ),
                               source.resolver() == null ? null : source.resolver().revision() );
    }

    @Override
    public <K, V> V processResource( final ResourceProcessor<K, V> processor )
    {
        final ProcessingKey cacheKey = new ProcessingKey( processor.getSegment(), processor.getKey(), resolverSource() );

        final ProcessingEntry cached = this.cache.getIfPresent( cacheKey );
        if ( cached != null && isCurrent( cached ) )
        {
            return (V) cached.value();
        }

        final ResourceKey resourceKey = processor.toResourceKey();
        final ApplicationKey applicationKey = resourceKey.getApplicationKey();
        final ResourceSource source = source( applicationKey );
        final CacheStamp stamp = stamp( source );
        final V value = processor.process( findResource( source, resourceKey ) );
        if ( value == null || !source.isStable() )
        {
            this.cache.invalidate( cacheKey );
            return value;
        }
        this.cache.put( cacheKey, new ProcessingEntry( value, applicationKey, stamp ) );
        return value;
    }

    private boolean isCurrent( final ProcessingEntry entry )
    {
        return entry.stamp().equals( stamp( source( entry.applicationKey() ) ) );
    }

    @Override
    public <K, V> V processResources( final MultiResourceProcessor<K, V> processor )
    {
        final ProcessingKey cacheKey = new ProcessingKey( processor.getSegment(), processor.getKey(), resolverSource() );

        final MultiProcessingEntry cached = this.multiCache.getIfPresent( cacheKey );
        if ( cached != null && isCurrent( cached ) )
        {
            return (V) cached.value();
        }

        final List<ResourceKey> resourceKeys = processor.toResourceKeys();
        final List<ApplicationKey> applicationKeys = applicationKeys( resourceKeys );
        final Map<ApplicationKey, ResourceSource> sources = new HashMap<>();
        final List<CacheStamp> stamps = new ArrayList<>();
        boolean stable = true;
        for ( final ApplicationKey applicationKey : applicationKeys )
        {
            final ResourceSource source = source( applicationKey );
            sources.put( applicationKey, source );
            stamps.add( stamp( source ) );
            stable = stable && source.isStable();
        }

        final List<Resource> resources = resourceKeys.stream()
            .map( resourceKey -> findResource( sources.get( resourceKey.getApplicationKey() ), resourceKey ) )
            .toList();

        final V value = processor.process( resources );
        if ( value == null || !stable )
        {
            this.multiCache.invalidate( cacheKey );
            return value;
        }
        this.multiCache.put( cacheKey, new MultiProcessingEntry( value, applicationKeys, List.copyOf( stamps ) ) );
        return value;
    }

    private boolean isCurrent( final MultiProcessingEntry entry )
    {
        final List<CacheStamp> stamps = new ArrayList<>();
        for ( final ApplicationKey applicationKey : entry.applicationKeys() )
        {
            stamps.add( stamp( source( applicationKey ) ) );
        }
        return entry.stamps().equals( stamps );
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
