package com.enonic.xp.resource;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandlerFactory;

import com.google.common.base.Throwables;

public final class ResourceUrlTestHelper
{
    public static ResourceUrlRegistry mockApplicationScheme()
    {
        final MockUrlStreamHandlerFactory handlerFactory = new MockUrlStreamHandlerFactory();
        installHandlerFactory( handlerFactory );
        return handlerFactory;
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
