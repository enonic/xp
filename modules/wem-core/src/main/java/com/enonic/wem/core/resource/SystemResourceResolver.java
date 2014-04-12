package com.enonic.wem.core.resource;

import java.net.URL;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;

final class SystemResourceResolver
    implements ResourceResolver
{
    private final static String PREFIX = "system";

    private ClassLoader classLoader;

    @Override
    public Resource resolve( final ResourceKey key )
    {
        final String path = getPath( key );
        final URL url = this.classLoader.getResource( path );

        if ( url == null )
        {
            return null;
        }

        final Resource.Builder builder = Resource.newResource();
        builder.byteSource( Resources.asByteSource( url ) );
        builder.timestamp( getTimestamp( url ) );
        builder.key( key );
        return builder.build();
    }

    private long getTimestamp( final URL url )
    {
        try
        {
            return url.openConnection().getLastModified();
        }
        catch ( final Exception e )
        {
            throw Throwables.propagate( e );
        }
    }

    @Override
    public ResourceKeys getChildren( final ResourceKey parentKey )
    {
        throw new UnsupportedOperationException();
    }

    private String getPath( final ResourceKey key )
    {
        return PREFIX + key.getPath();
    }

    public SystemResourceResolver classLoader( final ClassLoader classLoader )
    {
        this.classLoader = classLoader;
        return this;
    }
}
