package com.enonic.xp.blobstore;

import java.util.concurrent.Callable;

import com.enonic.xp.util.Exceptions;

public class ClassLoaderHelper
{
    public static <T> T callWith( Callable<T> callable, final Class clazz )
    {
        final Thread currentThread = Thread.currentThread();

        final ClassLoader original = currentThread.getContextClassLoader();

        try
        {
            currentThread.setContextClassLoader( clazz.getClassLoader() );

            return callable.call();
        }
        catch ( RuntimeException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw Exceptions.unchecked( e );
        }
        finally
        {
            currentThread.setContextClassLoader( original );
        }
    }

}
