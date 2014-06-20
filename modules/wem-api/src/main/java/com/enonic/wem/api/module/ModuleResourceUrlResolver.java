package com.enonic.wem.api.module;

import java.net.URL;

import com.google.common.base.Throwables;

public class ModuleResourceUrlResolver
{
    private static ModuleResourceUrlResolver CURRENT;

    static
    {
        new ModuleResourceUrlResolver();
    }

    public ModuleResourceUrlResolver()
    {
        CURRENT = this;
    }

    protected URL doResolve( final ModuleResourceKey key )
        throws Exception
    {
        return new URL( "module:" + key.toString() );
    }

    public static URL resolve( final ModuleResourceKey key )
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
