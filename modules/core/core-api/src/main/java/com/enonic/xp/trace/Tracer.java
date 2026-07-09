package com.enonic.xp.trace;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Tracer
{
    private static final ScopedValue<Trace> CURRENT = ScopedValue.newInstance();

    private static volatile TraceManager manager;

    private Tracer()
    {
    }

    public static boolean isEnabled()
    {
        return manager != null;
    }

    public static Trace current()
    {
        return CURRENT.isBound() ? CURRENT.get() : null;
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
        callWith( trace, () -> {
            runnable.run();
            return null;
        } );
    }

    /**
     * Executes the runnable in the given trace scope. Exceptions propagate unchanged: tracing never alters what a
     * caller catches.
     */
    public static <T> T trace( final Trace trace, final TraceRunnable<T> runnable )
    {
        return callWith( trace, runnable::run );
    }

    public static <T> T traceEx( final Trace trace, final Callable<T> callable )
        throws Exception
    {
        return callWith( trace, callable::call );
    }

    public static <T> T traceIO( final Trace trace, final TraceIO<T> callable )
        throws IOException
    {
        return callWith( trace, callable::call );
    }

    public static Trace newTrace( final String name )
    {
        final TraceManager current = manager;
        return current == null ? null : current.newTrace( name, current() );
    }

    public static void trace( final String name, final Runnable runnable )
    {
        trace( newTrace( name ), runnable );
    }

    public static <T> T trace( final String name, final TraceRunnable<T> runnable )
    {
        return trace( newTrace( name ), runnable );
    }

    public static <T> T trace( final String name, final Consumer<Trace> before, final Supplier<T> main, final BiConsumer<Trace, T> after )
    {
        final Trace trace = newTrace( name );

        if ( trace == null )
        {
            return main.get();
        }

        before.accept( trace );
        return callWith( trace, () -> {
            final T result = main.get();
            after.accept( trace, result );
            return result;
        } );
    }

    public static <T> T trace( final String name, final Consumer<Trace> before, final Supplier<T> main )
    {
        return trace( name, before, main, ( trace, t ) -> {
        } );
    }

    public static <T> T trace( final String name, final Consumer<Trace> before, final Runnable main )
    {
        return trace( name, before, () -> {
            main.run();
            return null;
        }, ( trace, t ) -> {
        } );
    }

    public static <T> T traceEx( final String name, final Callable<T> callable )
        throws Exception
    {
        return traceEx( newTrace( name ), callable );
    }

    public static void setManager( final TraceManager manager )
    {
        Tracer.manager = manager;
    }

    static <T, X extends Throwable> T callWith( final Trace trace, final ScopedValue.CallableOp<T, X> op )
        throws X
    {
        try
        {
            startTrace( trace );
            return ScopedValue.where( CURRENT, trace ).call( op );
        }
        finally
        {
            endTrace( trace );
        }
    }

    private static void startTrace( final Trace trace )
    {
        if ( trace == null )
        {
            return;
        }

        trace.start();
        final TraceManager current = manager;
        if ( current != null )
        {
            current.dispatch( TraceEvent.start( trace ) );
        }
    }

    private static void endTrace( final Trace trace )
    {
        if ( trace == null )
        {
            return;
        }

        trace.end();
        final TraceManager current = manager;
        if ( current != null )
        {
            current.dispatch( TraceEvent.end( trace ) );
        }
    }
}
