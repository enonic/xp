package com.enonic.xp.core.impl.app.resource;

import java.net.URL;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.server.RunMode;

@Component(immediate = true)
public final class ResourceServiceImpl
    implements ResourceService, ApplicationInvalidator
{
    private static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    private final ProcessingCache cache;

    private ApplicationService applicationService;

    public ResourceServiceImpl()
    {
        this.cache = new ProcessingCache( this::getResource, RunMode.get() );
    }

    @Override
    public Resource getResource( final ResourceKey key )
    {
        final Application app = findApplication( key.getApplicationKey() );
        if ( app == null )
        {
            return new UrlResource( key, null );
        }

        final URL url = app.resolveFile( key.getPath() );
        return new UrlResource( key, url );
    }

    private String normalize( final String str )
    {
        if ( str.startsWith( "/" ) )
        {
            return normalize( str.substring( 1 ) );
        }

        if ( str.endsWith( "/" ) )
        {
            return str.substring( 0, str.length() - 1 );
        }

        return str;
    }

    private Stream<String> doFindFiles( final ApplicationKey key )
    {
        final Application app = findApplication( key );
        if ( app == null )
        {
            return Stream.empty();
        }

        return app.getFiles().stream();
    }

    @Override
    public ResourceKeys findFiles( final ApplicationKey key, final String pattern )
    {
        final Pattern compiled = Pattern.compile( normalize( pattern ) );
        final Stream<String> files = doFindFiles( key ).
            filter( compiled.asPredicate() );

        return toKeys( key, files );
    }

    private ResourceKeys toKeys( final ApplicationKey appKey, final Stream<String> stream )
    {
        return ResourceKeys.from( stream.map( name -> ResourceKey.from( appKey, name ) ).iterator() );
    }

    private Application findApplication( final ApplicationKey key )
    {
        final ApplicationKey applicationKey = isSystemApp( key ) ? SYSTEM_APPLICATION_KEY : key;
        final Application application = this.applicationService.getInstalledApplication( applicationKey );
        return ( application != null ) && application.isStarted() ? application : null;
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

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.cache.invalidate( key );
    }
}
