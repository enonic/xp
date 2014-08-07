package com.enonic.wem.api.resource;

import java.net.URL;

import com.google.common.base.Throwables;

public class ResourceUrlResolver
{
    private static ResourceUrlResolver CURRENT;

    static
    {
        new ResourceUrlResolver();
    }

    public ResourceUrlResolver()
    {
        CURRENT = this;
    }

    protected URL doResolve( final ResourceKey key )
        throws Exception
    {
        return new URL( "module:" + key.toString() );
    }

    public static URL resolve( final ResourceKey key )
    {
        try
        {
            return CURRENT.doResolve( key );
        }
        catch ( final Exception e )
        {
            throw Throwables.propagate( e );
        }
    }
}
