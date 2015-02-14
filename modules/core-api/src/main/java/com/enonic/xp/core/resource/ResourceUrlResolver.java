package com.enonic.xp.core.resource;

import java.net.URL;

import com.google.common.base.Throwables;

public final class ResourceUrlResolver
{
    public static URL resolve( final ResourceKey key )
    {
        try
        {
            return new URL( "module:" + key.toString() );
        }
        catch ( final Exception e )
        {
            throw Throwables.propagate( e );
        }
    }
}
