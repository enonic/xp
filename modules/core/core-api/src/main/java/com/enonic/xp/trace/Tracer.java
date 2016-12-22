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

    public static <T> T trace( final Trace trace, final TraceRunnable<T> runnable )
    {
        final Trace current = current();

        try
        {
            setCurrent( trace );
            startTrace( trace );
            return runnable.run();
        }
        finally
        {
            endTrace( trace );
            setCurrent( current );
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

    public static Trace newTrace( final String type )
    {
        if ( !isEnabled() )
        {
            return null;
        }

        return INSTANCE.manager.newTrace( type, current() );
    }

    public static <T> T trace( final String type, final TraceRunnable<T> runnable )
    {
        return trace( newTrace( type ), runnable );
    }

    public static <T> T traceEx( final String type, final Callable<T> callable )
        throws Exception
    {
        return traceEx( newTrace( type ), callable );
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
        INSTANCE.manager.dispatch( TraceEvent.start( trace ) );
    }

    private static void endTrace( final Trace trace )
    {
        if ( trace == null )
        {
            return;
        }

        trace.end();
        INSTANCE.manager.dispatch( TraceEvent.end( trace ) );
    }
}
