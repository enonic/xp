package com.enonic.xp.core.impl.resource;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.UrlResource;

final class BundleResourceLoader
    implements ResourceLoader
{
    @Override
    public Resource getResource( final Application app, final ResourceKey key )
    {
        final String resourcePath = key.getPath();
        final Bundle bundle = app.getBundle();
        final URL url = bundle.getResource( resourcePath );
        return new UrlResource( key, url );
    }

    @Override
    public ResourceKeys findFolders( final Application app, final String path )
    {
        final Bundle bundle = app.getBundle();
        final Enumeration<String> entryPaths = bundle.getEntryPaths( path );
        if ( entryPaths == null )
        {
            return ResourceKeys.empty();
        }

        final List<ResourceKey> resourceKeyList = Collections.list( entryPaths ).
            stream().
            filter( entryPath -> entryPath.endsWith( "/" ) ).
            map( entryPath -> ResourceKey.from( app.getKey(), entryPath ) ).
            collect( Collectors.toList() );

        return ResourceKeys.from( resourceKeyList );
    }
}
