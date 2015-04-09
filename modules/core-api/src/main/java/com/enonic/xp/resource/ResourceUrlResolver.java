package com.enonic.xp.resource;

import java.net.URL;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;

@Beta
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
