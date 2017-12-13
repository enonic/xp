package com.enonic.xp.trace;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public final class Tracer
{
    private final static Tracer INSTANCE = new Tracer();

    private final ThreadLocal<Trace> current;

    private TraceManager manager;

    private Tracer()
    {
        this.current = new ThreadLocal<>();
        this.manager = null;
    }

    public static boolean isEnabled()
    {
        return INSTANCE.manager != null;
    }

    public static Trace current()
    {
        return INSTANCE.current.get();
    }

    public static void withCurrent( final Consumer<Trace> consumer )
    {
        final Trace trace = current();
        if ( trace != null )
        {
            consumer.accept( trace );
        }
    }

    public static void trace( final Trace trace, final Runnable runnable )
    {
        trace( trace, () -> {
            runnable.run();
            return null;
        } );
    }

    public static <T> T trace( final Trace trace, final TraceRunnable<T> runnable )
    {
        try
        {
            return traceEx( trace, runnable::run );
        }
        catch ( final RuntimeException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public static <T> T traceEx( final Trace trace, final Callable<T> callable )
        throws Exception
    {
        final Trace current = current();

        try
        {
            setCurrent( trace );
            startTrace( trace );
            return callable.call();
        }
        finally
        {
            endTrace( trace );
            setCurrent( current );
        }
    }

    public static Trace newTrace( final String name )
    {
        if ( !isEnabled() )
        {
            return null;
        }

        return INSTANCE.manager.newTrace( name, current() );
    }

    public static void trace( final String name, final Runnable runnable )
    {
        trace( newTrace( name ), runnable );
    }

    public static <T> T trace( final String name, final TraceRunnable<T> runnable )
    {
        return trace( newTrace( name ), runnable );
    }

    public static <T> T traceEx( final String name, final Callable<T> callable )
        throws Exception
    {
        return traceEx( newTrace( name ), callable );
    }

    public static void setManager( final TraceManager manager )
    {
        INSTANCE.manager = manager;
    }

    private static void setCurrent( final Trace trace )
    {
        INSTANCE.current.set( trace );
    }

    private static void startTrace( final Trace trace )
    {
        if ( trace == null )
        {
            return;
        }

        trace.start();
        if ( INSTANCE.manager != null )
        {
            INSTANCE.manager.dispatch( TraceEvent.start( trace ) );
        }
    }

    private static void endTrace( final Trace trace )
    {
        if ( trace == null )
        {
            return;
        }

        trace.end();
        if ( INSTANCE.manager != null )
        {
            INSTANCE.manager.dispatch( TraceEvent.end( trace ) );
        }
    }
}
