package com.enonic.xp.core.impl.app.resource;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationAdaptor;
import com.enonic.xp.core.impl.app.ApplicationFactoryService;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.util.HashCode;

@Component(immediate = true)
public final class ResourceServiceImpl
    implements ResourceService, ApplicationInvalidator
{
    private static final Logger LOG = LoggerFactory.getLogger( ResourceServiceImpl.class );

    private static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    private final ProcessingCache cache;

    private final ApplicationFactoryService applicationFactoryService;

    @Activate
    public ResourceServiceImpl( @Reference final ApplicationFactoryService applicationFactoryService )
    {
        this.cache = new ProcessingCache( this::getResource, RunMode.get() );
        this.applicationFactoryService = applicationFactoryService;
    }

    @Override
    public Resource getResource( final ResourceKey key )
    {
        return findApplicationUrlResolver( key.getApplicationKey() ).map( urlResolver -> urlResolver.findUrl( key.getPath() ) )
            .map( url -> new UrlResource( key, url ) )
            .orElse( new UrlResource( key, null ) );
    }

    @Override
    public ResourceKeys findFiles( final ApplicationKey key, final String pattern )
    {
        final Pattern compiled = Pattern.compile( pattern );

        return ResourceKeys.from( findApplicationUrlResolver( key ).map( ApplicationUrlResolver::findFiles )
                                      .orElse( Set.of() )
                                      .stream()
                                      .map( name -> ResourceKey.from( key, name ) )
                                      .filter( rk -> compiled.matcher( rk.getPath() ).find() )
                                      .iterator() );
    }

    private Optional<ApplicationUrlResolver> findApplicationUrlResolver( final ApplicationKey key )
    {
        final ApplicationKey applicationKey = isSystemApp( key ) ? SYSTEM_APPLICATION_KEY : key;
        return applicationFactoryService.findActiveApplication( applicationKey ).map( ApplicationAdaptor::getUrlResolver );
    }

    private boolean isSystemApp( final ApplicationKey key )
    {
        return ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( key );
    }

    @Override
    public <K, V> V processResource( final ResourceProcessor<K, V> processor )
    {
        return this.cache.process( processor );
    }

    @Override
    public Optional<HashCode> resourceHash( final ResourceKey key )
    {
        if ( !key.equals( ResourceKey.assets( key.getApplicationKey() ) ) )
        {
            throw new IllegalArgumentException( "Unsupported resource key " + key );
        }
        return findApplicationUrlResolver( key.getApplicationKey() ).map( urlResolver -> urlResolver.filesHash( key.getPath() ) )
            .map( HashCode::fromLong );
    }

    @Override
    @Deprecated
    public void invalidate( final ApplicationKey key )
    {
        invalidate( key, ApplicationInvalidationLevel.FULL );
    }

    @Override
    public void invalidate( final ApplicationKey key, final ApplicationInvalidationLevel level )
    {
        LOG.debug( "Cleanup Resource cache for {}", key );
        this.cache.invalidate( key );
    }
}
