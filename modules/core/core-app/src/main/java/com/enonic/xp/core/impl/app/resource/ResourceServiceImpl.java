package com.enonic.xp.core.impl.app.resource;

import java.net.URL;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationNotFoundException;
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

    private String normalizePath( final String path )
    {
        if ( path.startsWith( "/" ) )
        {
            return normalizePath( path.substring( 1 ) );
        }

        if ( path.endsWith( "/" ) )
        {
            return path.substring( 0, path.length() - 1 );
        }

        return path;
    }

    @Override
    public ResourceKeys findFiles( final ApplicationKey key, final String path, final String ext, final boolean recursive )
    {
        final String normalized = normalizePath( path );

        if ( recursive )
        {
            return findFiles2( key, normalized + "/.+\\." + ext );
        }

        return findFiles2( key, normalized + "/[^/]+\\." + ext );
    }

    @Override
    public ResourceKeys findFolders( final ApplicationKey key, final String path )
    {
        final String normalized = normalizePath( path );

        final Stream<String> folders = doFindFolders( key ).
            map( name -> toFolderName( name, normalized ) ).
            filter( name -> name != null ).
            distinct();

        return toKeys( key, folders );
    }

    private String toFolderName( final String name, final String prefix )
    {
        final String prefixWithSlash = prefix + "/";
        if ( !name.startsWith( prefixWithSlash ) )
        {
            return null;
        }

        final String rest = name.substring( prefixWithSlash.length() );
        final int index = rest.indexOf( '/' );

        if ( index > 0 )
        {
            return prefixWithSlash + rest.substring( 0, index );
        }

        return prefixWithSlash + rest;
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

    private Stream<String> doFindFolders( final ApplicationKey key )
    {
        return doFindFiles( key ).map( this::toFolderName ).filter( name -> name != null ).distinct();
    }

    private String toFolderName( final String path )
    {
        final int index = path.lastIndexOf( '/' );
        if ( index > 0 )
        {
            return path.substring( 0, index );
        }

        return null;
    }

    public ResourceKeys findFiles2( final ApplicationKey key, final String pattern )
    {
        final Pattern compiled = Pattern.compile( pattern );
        final Stream<String> files = doFindFiles( key ).
            filter( compiled.asPredicate() );

        return toKeys( key, files );
    }

    public ResourceKeys findFolders2( final ApplicationKey key, final String pattern )
    {
        final Pattern compiled = Pattern.compile( pattern );
        final Stream<String> files = doFindFolders( key ).
            filter( compiled.asPredicate() );

        return toKeys( key, files );
    }

    private ResourceKeys toKeys( final ApplicationKey appKey, final Stream<String> stream )
    {
        return ResourceKeys.from( stream.map( name -> ResourceKey.from( appKey, name ) ).iterator() );
    }

    private Application findApplication( final ApplicationKey key )
    {
        try
        {
            final Application application = this.applicationService.getApplication( key );
            return ( application != null ) && application.isStarted() ? application : null;
        }
        catch ( final ApplicationNotFoundException e )
        {
            return null;
        }
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
