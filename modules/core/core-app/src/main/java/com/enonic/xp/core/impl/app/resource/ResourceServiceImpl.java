package com.enonic.xp.core.impl.app.resource;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.app.ApplicationAdaptor;
import com.enonic.xp.core.impl.app.ApplicationFactory;
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
 * service (a content type inlining its form fragments), and two threads computing the same value at once just agree on it. What a
 * processor reads that way is recorded with the value as its {@link Dependency dependencies}: for an unstable resolver they are
 * checked on every use as the resource of the value itself is, so an edit of a fragment alone reaches the content types inlining it.
 * A stable resolver needs no such record: a value only reads resources of its own application, all guarded by the same stamp.
 */
@Component(immediate = true)
public final class ResourceServiceImpl
    implements ResourceService
{
    private static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    private final ConcurrentMap<ProcessingKey, ProcessingEntry> cache = new ConcurrentHashMap<>();

    private final ConcurrentMap<ProcessingKey, MultiProcessingEntry> multiCache = new ConcurrentHashMap<>();

    private final ConcurrentMap<FilesKey, FilesEntry> filesCache = new ConcurrentHashMap<>();

    // the dependencies of the values being computed on this thread, innermost last; none set while nothing is computed, so a hit
    // outside of any computation costs a single lookup
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
        final Bundle bundle = applicationFactoryService.findActiveBundle( effectiveKey ).orElse( null );
        // the application node is checked only for an application served from it: a local application is served from its bundle alone
        final long generation = bundle == null || ApplicationFactory.isPersisted( bundle )
            ? applicationGeneration.of( effectiveKey )
            : applicationGeneration.current( effectiveKey );
        return new CacheStamp( bundle == null ? null : BundleStamp.from( bundle ), generation );
    }

    @Override
    public <K, V> V processResource( final ResourceProcessor<K, V> processor )
    {
        final ProcessingKey cacheKey = new ProcessingKey( processor.getSegment(), processor.getKey() );

        final ProcessingEntry cached = this.cache.get( cacheKey );
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

        final List<Dependency> dependencies = new ArrayList<>();
        final V value = compute( dependencies, () -> processor.process( resource ) );
        final Dependency self = Dependency.of( resource );
        final List<Dependency> outer = outerDependencies();
        if ( outer != null )
        {
            outer.add( self );
            outer.addAll( dependencies );
        }
        if ( value == null )
        {
            this.cache.remove( cacheKey );
            return null;
        }
        final boolean stable = isStable( urlResolver );
        this.cache.put( cacheKey, new ProcessingEntry( value, self, stable ? List.of() : List.copyOf( dependencies ), stamp, stable ) );
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

        final MultiProcessingEntry cached = this.multiCache.get( cacheKey );
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

        final List<Dependency> dependencies = new ArrayList<>();
        final V value = compute( dependencies, () -> processor.process( resources ) );
        final List<Dependency> selves = resources.stream().map( Dependency::of ).toList();
        final List<Dependency> outer = outerDependencies();
        if ( outer != null )
        {
            outer.addAll( selves );
            outer.addAll( dependencies );
        }
        if ( value == null )
        {
            this.multiCache.remove( cacheKey );
            return null;
        }
        this.multiCache.put( cacheKey,
                             new MultiProcessingEntry( value, selves, stable ? List.of() : List.copyOf( dependencies ), stamps, stable ) );
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
            // missing resources are part of the value as well: one appearing changes its timestamp
            if ( findResource( urlResolver, resourceKey ).getTimestamp() != resource.timestamp() )
            {
                return false;
            }
        }
        return dependenciesCurrent( entry.dependencies() );
    }

    // computes a value collecting what it reads through this service
    private <V> V compute( final List<Dependency> dependencies, final Supplier<V> processing )
    {
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
