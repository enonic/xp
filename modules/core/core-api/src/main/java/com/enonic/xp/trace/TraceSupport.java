package com.enonic.xp.trace;

/**
 * Entry points invoked from bytecode woven for {@link Traced} annotated methods.
 * <p>
 * Not intended to be called directly from application code. Use {@link Traced} or {@link Tracer} instead.
 */
public final class TraceSupport
{
    /**
     * Shape of a woven method body returning a value. Implemented by the {@code invokedynamic} generated
     * lambda that captures the receiver and arguments of the intercepted method invocation.
     */
    @FunctionalInterface
    public interface TracedCall
    {
        Object invoke()
            throws Throwable;
    }

    /**
     * Shape of a woven {@code void} method body.
     */
    @FunctionalInterface
    public interface TracedVoidCall
    {
        void invoke()
            throws Throwable;
    }

    private TraceSupport()
    {
    }

    /**
     * Executes a woven non-void method body in a trace scope. Exceptions thrown by the body - checked or
     * unchecked - propagate unchanged.
     *
     * @param name trace name
     * @param call intercepted method body
     * @return result of the method body
     * @throws Throwable whatever the method body throws
     */
    public static Object trace( final String name, final TracedCall call )
        throws Throwable
    {
        final Trace trace = Tracer.newTrace( name );
        if ( trace == null )
        {
            return call.invoke();
        }

        return Tracer.callWith( trace, call::invoke );
    }

    /**
     * Executes a woven void method body in a trace scope. Exceptions thrown by the body - checked or
     * unchecked - propagate unchanged.
     *
     * @param name trace name
     * @param call intercepted method body
     * @throws Throwable whatever the method body throws
     */
    public static void trace( final String name, final TracedVoidCall call )
        throws Throwable
    {
        final Trace trace = Tracer.newTrace( name );
        if ( trace == null )
        {
            call.invoke();
            return;
        }

        Tracer.callWith( trace, () -> {
            call.invoke();
            return null;
        } );
    }
}
