package com.enonic.xp.core.impl.app.resource;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
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
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.vfs.VirtualFile;

@Component(immediate = true)
public final class ResourceServiceImpl
    implements ResourceService, ApplicationInvalidator
{
    private static final Logger LOG = LoggerFactory.getLogger( ResourceServiceImpl.class );

    private static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    private final ConcurrentMap<ProcessingKey, ProcessingEntry> cache;

    private final ApplicationFactoryService applicationFactoryService;

    @Activate
    public ResourceServiceImpl( @Reference final ApplicationFactoryService applicationFactoryService )
    {
        this.cache = new ConcurrentHashMap<>();
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
        return applicationFactoryService.findResolver( isSystemApp( key ) ? SYSTEM_APPLICATION_KEY : key, resolverSource );
    }

    private boolean isSystemApp( final ApplicationKey key )
    {
        return ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( key );
    }

    @Override
    public <K, V> V processResource( final ResourceProcessor<K, V> processor )
    {
        final ProcessingEntry entry = this.cache.compute( new ProcessingKey( processor.getSegment(), processor.getKey() ), ( k, v ) -> {
            final Resource resource = this.getResource( processor.toResourceKey() );
            if ( v == null || !resource.exists() || resource.getTimestamp() > v.timestamp )
            {
                final V value = processor.process( resource );
                if ( value == null )
                {
                    return null;
                }

                return new ProcessingEntry( processor.toResourceKey(), value, resource.getTimestamp() );
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

    @Override
    public void invalidate( final ApplicationKey key, final ApplicationInvalidationLevel level )
    {
        LOG.debug( "Cleanup Resource cache for {}", key );
        this.cache.entrySet().removeIf( entry -> entry.getValue().key.getApplicationKey().equals( key ) );
    }
}
