package com.enonic.xp.core.impl.resource;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.FileResource;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.server.RunMode;

@Component(immediate = true)
public class ResourceServiceImpl
    implements ResourceService
{
    protected RunMode runMode;

    private ApplicationService applicationService;

    public ResourceServiceImpl()
    {
        this.runMode = RunMode.get();
    }

    @Override
    public Resource getResource( final ResourceKey resourceKey )
    {
        final Application application = getActiveApplication( resourceKey.getApplicationKey() );
        if ( application == null )
        {
            return new UrlResource( resourceKey, null );
        }

        if ( this.runMode == RunMode.DEV )
        {
            return loadUsingDevMode( application, resourceKey );
        }

        return loadFromBundle( application.getBundle(), resourceKey );
    }

    private Resource loadFromBundle( final Bundle bundle, final ResourceKey key )
    {
        final String resourcePath = key.getPath();
        final URL url = bundle.getResource( resourcePath );
        return new UrlResource( key, url );
    }

    private Resource loadUsingDevMode( final Application app, final ResourceKey key )
    {
        final List<String> paths = app.getSourcePaths();
        final File file = loadFromPaths( paths, key );

        if ( file != null )
        {
            return new FileResource( key, file );
        }

        return loadFromBundle( app.getBundle(), key );
    }

    private File loadFromPaths( final List<String> paths, final ResourceKey key )
    {
        for ( final String path : paths )
        {
            final File file = loadFromPath( path, key );
            if ( file != null )
            {
                return file;
            }
        }

        return null;
    }

    private File loadFromPath( final String path, final ResourceKey key )
    {
        final File file = new File( new File( path ), key.getPath().substring( 1 ) );
        if ( file.isFile() && file.exists() )
        {
            return file;
        }

        return null;
    }

    @Override
    public ResourceKeys findResourceKeys( final ApplicationKey applicationKey, final String path, final String filePattern,
                                          boolean recurse )
    {
        final Application application = getActiveApplication( applicationKey );
        if ( application == null )
        {
            return ResourceKeys.empty();
        }

        final Bundle bundle = application.getBundle();
        final Enumeration<URL> entries = bundle.findEntries( path, filePattern, recurse );
        if ( entries == null )
        {
            return ResourceKeys.empty();
        }

        final List<ResourceKey> resourceKeyList = Collections.list( entries ).
            stream().
            map( resourceUrl -> ResourceKey.from( applicationKey, resourceUrl.getPath() ) ).
            collect( Collectors.toList() );

        return ResourceKeys.from( resourceKeyList );
    }

    @Override
    public ResourceKeys findFolders( final ApplicationKey applicationKey, final String path )
    {
        final Application application = getActiveApplication( applicationKey );
        if ( application == null )
        {
            return ResourceKeys.empty();
        }

        final Bundle bundle = application.getBundle();
        final Enumeration<String> entryPaths = bundle.getEntryPaths( path );
        if ( entryPaths == null )
        {
            return ResourceKeys.empty();
        }

        final List<ResourceKey> resourceKeyList = Collections.list( entryPaths ).
            stream().
            filter( entryPath -> entryPath.endsWith( "/" ) ).
            map( entryPath -> ResourceKey.from( applicationKey, entryPath ) ).
            collect( Collectors.toList() );

        return ResourceKeys.from( resourceKeyList );
    }

    private Application getActiveApplication( final ApplicationKey applicationKey )
    {
        try
        {
            final Application application = applicationService.getApplication( applicationKey );
            return application.getBundle().getState() == Bundle.ACTIVE ? application : null;
        }
        catch ( final ApplicationNotFoundException e )
        {
            return null;
        }
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}
