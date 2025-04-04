package com.enonic.xp.core.internal;

import java.util.concurrent.ExecutionException;

public final class Exceptions
{
    private Exceptions()
    {
    }

    public static void throwIfUnchecked( final Throwable throwable )
    {
        if ( throwable instanceof RuntimeException )
        {
            throw (RuntimeException) throwable;
        }
        if ( throwable instanceof Error )
        {
            throw (Error) throwable;
        }
    }

    public static RuntimeException throwCause( final ExecutionException e )
    {
        if ( e.getCause() instanceof RuntimeException )
        {
            throw (RuntimeException) e.getCause();
        }
        throw new RuntimeException( e.getCause() );
    }
}
