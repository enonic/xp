package com.enonic.xp.context;

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.DynamicTestInvocationContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

/**
 * Test support for setting the ambient {@link Context} outside of {@link Context#runWith}/{@link Context#callWith}.
 * <p>
 * {@link ContextAccessor} is backed by a {@link ScopedValue}, which can only be bound around a bounded scope,
 * so a context can no longer be installed imperatively from a {@code @BeforeEach} method. This class bridges
 * the gap: a context stored in {@link #getInstance()} is bound around every subsequent lifecycle, test and
 * dynamic-test invocation. Register it with {@code @ExtendWith(ContextAccessorSupport.class)} on the test class.
 * <p>
 * Note that code running in the remainder of the very method that sets the context does not see it bound;
 * wrap such code in {@link Context#callWith} explicitly.
 */
public final class ContextAccessorSupport
    implements InvocationInterceptor
{
    private static final ThreadLocal<Context> HOLDER = new ThreadLocal<>();

    public static ThreadLocal<Context> getInstance()
    {
        return HOLDER;
    }

    @Override
    public void interceptBeforeEachMethod( final Invocation<Void> invocation,
                                           final ReflectiveInvocationContext<Method> invocationContext,
                                           final ExtensionContext extensionContext )
        throws Throwable
    {
        proceed( invocation );
    }

    @Override
    public void interceptAfterEachMethod( final Invocation<Void> invocation,
                                          final ReflectiveInvocationContext<Method> invocationContext,
                                          final ExtensionContext extensionContext )
        throws Throwable
    {
        proceed( invocation );
    }

    @Override
    public void interceptTestMethod( final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext,
                                     final ExtensionContext extensionContext )
        throws Throwable
    {
        proceed( invocation );
    }

    @Override
    public void interceptTestTemplateMethod( final Invocation<Void> invocation,
                                             final ReflectiveInvocationContext<Method> invocationContext,
                                             final ExtensionContext extensionContext )
        throws Throwable
    {
        proceed( invocation );
    }

    @Override
    public <T> T interceptTestFactoryMethod( final Invocation<T> invocation,
                                             final ReflectiveInvocationContext<Method> invocationContext,
                                             final ExtensionContext extensionContext )
        throws Throwable
    {
        final Context context = HOLDER.get();
        if ( context == null )
        {
            return invocation.proceed();
        }
        return ScopedValue.where( ContextAccessor.INSTANCE, context ).call( invocation::proceed );
    }

    @Override
    public void interceptDynamicTest( final Invocation<Void> invocation, final DynamicTestInvocationContext invocationContext,
                                      final ExtensionContext extensionContext )
        throws Throwable
    {
        proceed( invocation );
    }

    private static void proceed( final Invocation<Void> invocation )
        throws Throwable
    {
        final Context context = HOLDER.get();
        if ( context == null )
        {
            invocation.proceed();
        }
        else
        {
            ScopedValue.where( ContextAccessor.INSTANCE, context ).call( invocation::proceed );
        }
    }
}
