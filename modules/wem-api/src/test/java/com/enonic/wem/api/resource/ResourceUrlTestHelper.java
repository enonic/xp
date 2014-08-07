package com.enonic.wem.api.resource;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandlerFactory;

import com.google.common.base.Throwables;

public final class ResourceUrlTestHelper
{
    public static void mockModuleScheme( final File dir )
    {
        installHandlerFactory( new MockUrlStreamHandlerFactory( dir ) );
    }

    private static void clearHandlerFactory()
    {
        try
        {
            final Field field = URL.class.getDeclaredField( "factory" );
            field.setAccessible( true );
            field.set( null, null );
        }
        catch ( final Exception e )
        {
            throw Throwables.propagate( e );
        }
    }

    private static void installHandlerFactory( final URLStreamHandlerFactory factory )
    {
        clearHandlerFactory();
        URL.setURLStreamHandlerFactory( factory );
    }
}
