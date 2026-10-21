package com.enonic.xp.core.impl.app.resource;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.app.ApplicationAdaptor;
import com.enonic.xp.core.impl.app.ApplicationFactoryService;
import com.enonic.xp.core.impl.app.ApplicationGeneration;
import com.enonic.xp.core.impl.app.ApplicationHelper;
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
 * resource is looked at again. An unstable resolver (the classloader of an application in development) has the revision of its
 * resource, as the resolver reports it, checked on every use, as the files change under it without any signal.
 * <p>
 * A value is computed outside of any lock and put in the cache afterwards: a processor may itself read other resources through this
 * service (a content type inlining its form fragments), and two threads computing the same value at once just agree on it. The
 * caches are bounded in size and by last access only so that a key nobody reads any more does not hold its value forever; currency
 * is never a matter of age, it is checked on every hit. What a
 * processor reads that way is recorded with the value as its {@link Dependency dependencies}: for an unstable resolver they are
 * checked on every use as the resource of the value itself is, so an edit of a fragment alone reaches the content types inlining it.
 * A stable resolver needs no such record: a value only reads resources of its own application, all guarded by the same stamp.
 */
@Component(immediate = true)
public final class ResourceServiceImpl
    implements ResourceService
{
    private final Cache<ProcessingKey, ProcessingEntry> cache = newCache();

    private final Cache<ProcessingKey, MultiProcessingEntry> multiCache = newCache();

    private final Cache<FilesKey, FilesEntry> filesCache = newCache();

    // the dependencies of the unstable values being computed on this thread, innermost last; none set while none is computed, so a
    // hit outside of any such computation costs a single lookup
    private final ThreadLocal<Deque<List<Dependency>>> computing = new ThreadLocal<>();

    private final ApplicationFactoryService applicationFactoryService;

    private final ApplicationGeneration applicationGeneration;

    @Activate
    public ResourceServiceImpl( @Reference final ApplicationFactoryService applicationFactoryService,
                                @Reference final ApplicationGeneration applicationGeneration )
    {
        this.applicationFactoryService = applicationFactoryService;
        this.applicationGeneration = applicationGeneration;
    }

    // bounded so that a key nobody reads any more (an application uninstalled, a descriptor removed) is let go of eventually
    private static <K, V> Cache<K, V> newCache()
    {
        return Caffeine.newBuilder().maximumSize( 100_000 ).expireAfterAccess( Duration.ofHours( 1 ) ).build();
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
        final FilesEntry cached = filesCache.getIfPresent( filesKey );
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

    private Optional<ApplicationUrlResolver> findApplicationUrlResolver( final ApplicationKey key )
    {
        return applicationFactoryService.findResolver( effectiveKey( key ), resolverSource() );
    }

    private static ApplicationKey effectiveKey( final ApplicationKey key )
    {
        return ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( key ) ? ApplicationHelper.SYSTEM_APPLICATION_KEY : key;
    }

    // the incarnation of the bundle and the generation of the application, whatever it is served from: the service does not know
    private CacheStamp stamp( final ApplicationKey key )
    {
        final ApplicationKey effectiveKey = effectiveKey( key );
        return new CacheStamp( applicationFactoryService.findActiveBundle( effectiveKey ).map( BundleStamp::from ).orElse( null ),
                               applicationGeneration.of( effectiveKey ) );
    }

    @Override
    public <K, V> V processResource( final ResourceProcessor<K, V> processor )
    {
        final ProcessingKey cacheKey = new ProcessingKey( processor.getSegment(), processor.getKey() );

        final ProcessingEntry cached = this.cache.getIfPresent( cacheKey );
        if ( cached != null && isCurrent( cached ) )
        {
            final List<Dependency> outer = outerDependencies();
            if ( outer != null )
            {
                outer.add( cached.resource() );
                outer.addAll( cached.dependencies() );
            }
            return (V) cached.value();
        }

        // the stamp is read before the resource: a change racing in-between costs a recompute, never staleness
        final ResourceKey resourceKey = processor.toResourceKey();
        final CacheStamp stamp = stamp( resourceKey.getApplicationKey() );
        final Optional<ApplicationUrlResolver> urlResolver = findApplicationUrlResolver( resourceKey.getApplicationKey() );
        final Resource resource = findResource( urlResolver, resourceKey );
        final boolean stable = isStable( urlResolver );

        // what an unstable value reads while computed is recorded with it; a stable one has its stamp for a signal and records nothing
        final List<Dependency> dependencies = stable ? null : new ArrayList<>();
        final V value = compute( dependencies, () -> processor.process( resource ) );
        final Dependency self = Dependency.of( resource );
        final List<Dependency> outer = outerDependencies();
        if ( outer != null )
        {
            outer.add( self );
            if ( dependencies != null )
            {
                outer.addAll( dependencies );
            }
        }
        if ( value == null )
        {
            this.cache.invalidate( cacheKey );
            return null;
        }
        this.cache.put( cacheKey, new ProcessingEntry( value, self, dependencies == null ? List.of() : List.copyOf( dependencies ), stamp,
                                                       stable ) );
        return value;
    }

    private boolean isCurrent( final ProcessingEntry entry )
    {
        final ApplicationKey applicationKey = entry.resource().resourceKey().getApplicationKey();
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
        final Resource resource = findResource( findApplicationUrlResolver( applicationKey ), entry.resource().resourceKey() );
        return resource.exists() && entry.resource().isCurrent( resource ) && dependenciesCurrent( entry.dependencies() );
    }

    @Override
    public <K, V> V processResources( final MultiResourceProcessor<K, V> processor )
    {
        final ProcessingKey cacheKey = new ProcessingKey( processor.getSegment(), processor.getKey() );

        final MultiProcessingEntry cached = this.multiCache.getIfPresent( cacheKey );
        if ( cached != null && isCurrent( cached ) )
        {
            final List<Dependency> outer = outerDependencies();
            if ( outer != null )
            {
                outer.addAll( cached.resources() );
                outer.addAll( cached.dependencies() );
            }
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

        final List<Resource> resources = resourceKeys.stream()
            .map( resourceKey -> findResource( urlResolvers.get( resourceKey.getApplicationKey() ), resourceKey ) )
            .toList();

        final List<Dependency> dependencies = stable ? null : new ArrayList<>();
        final V value = compute( dependencies, () -> processor.process( resources ) );
        final List<Dependency> selves = resources.stream().map( Dependency::of ).toList();
        final List<Dependency> outer = outerDependencies();
        if ( outer != null )
        {
            outer.addAll( selves );
            if ( dependencies != null )
            {
                outer.addAll( dependencies );
            }
        }
        if ( value == null )
        {
            this.multiCache.invalidate( cacheKey );
            return null;
        }
        this.multiCache.put( cacheKey, new MultiProcessingEntry( value, selves, dependencies == null ? List.of() : List.copyOf( dependencies ),
                                                                 stamps, stable ) );
        return value;
    }

    private boolean isCurrent( final MultiProcessingEntry entry )
    {
        final List<ApplicationKey> applicationKeys =
            applicationKeys( entry.resources().stream().map( Dependency::resourceKey ).toList() );
        if ( !entry.stamps().equals( applicationKeys.stream().map( this::stamp ).toList() ) )
        {
            return false;
        }
        if ( entry.stable() )
        {
            return true;
        }
        final Map<ApplicationKey, Optional<ApplicationUrlResolver>> urlResolvers = new HashMap<>();
        for ( final Dependency resource : entry.resources() )
        {
            final ResourceKey resourceKey = resource.resourceKey();
            final Optional<ApplicationUrlResolver> urlResolver =
                urlResolvers.computeIfAbsent( resourceKey.getApplicationKey(), this::findApplicationUrlResolver );
            // missing resources are part of the value as well: one appearing is a change
            if ( !resource.isCurrent( findResource( urlResolver, resourceKey ) ) )
            {
                return false;
            }
        }
        return dependenciesCurrent( entry.dependencies() );
    }

    // computes a value collecting what it reads through this service into the list, when there is one: a stable value collects
    // nothing, what is read while it is computed goes to the nearest unstable value being computed, if any
    private <V> V compute( final @Nullable List<Dependency> dependencies, final Supplier<V> processing )
    {
        if ( dependencies == null )
        {
            return processing.get();
        }
        Deque<List<Dependency>> stack = computing.get();
        if ( stack == null )
        {
            stack = new ArrayDeque<>();
            computing.set( stack );
        }
        stack.addLast( dependencies );
        try
        {
            return processing.get();
        }
        finally
        {
            stack.removeLast();
            if ( stack.isEmpty() )
            {
                computing.remove();
            }
        }
    }

    // where a value read while another one is computed goes, as a dependency of it together with everything it depends on itself;
    // null outside of any computation
    private List<Dependency> outerDependencies()
    {
        final Deque<List<Dependency>> stack = computing.get();
        return stack == null ? null : stack.peekLast();
    }

    // the resources a value was computed from besides its own are as they were then
    private boolean dependenciesCurrent( final List<Dependency> dependencies )
    {
        if ( dependencies.isEmpty() )
        {
            return true;
        }
        final Map<ApplicationKey, Optional<ApplicationUrlResolver>> urlResolvers = new HashMap<>();
        for ( final Dependency dependency : dependencies )
        {
            final Optional<ApplicationUrlResolver> urlResolver =
                urlResolvers.computeIfAbsent( dependency.resourceKey().getApplicationKey(), this::findApplicationUrlResolver );
            if ( !dependency.isCurrent( findResource( urlResolver, dependency.resourceKey() ) ) )
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
