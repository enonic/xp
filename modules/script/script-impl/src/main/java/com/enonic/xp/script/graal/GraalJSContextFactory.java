package com.enonic.xp.script.graal;


import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import com.enonic.xp.script.graal.util.JsFunctionHandle;

public final class GraalJSContextFactory
{
    private final ClassLoader classLoader;

    /**
     * Resolved when a context is actually built, never before: an application that executes no
     * script — pure Java, or simply without a bootstrap script — gets an executor but no context,
     * and must not pay for an engine it never uses.
     */
    private final Supplier<Engine> sharedEngine;

    public GraalJSContextFactory()
    {
        this( null, null );
    }

    public GraalJSContextFactory( final ClassLoader classLoader, final Supplier<Engine> sharedEngine )
    {
        this.classLoader = classLoader;
        this.sharedEngine = sharedEngine;
    }

    public Context create()
    {
        final AtomicReference<Context> contextRef = new AtomicReference<>();

        // Note: guest values passed between contexts silently re-enter their owning context on
        // every access — with the context pool that means latent serialization and deadlock
        // risk. allowValueSharing(false) would fail fast on such leaks, but GraalJS forbids
        // disabling it for contexts bound to a shared engine, which we require for code-cache
        // sharing. Isolation therefore relies on the executor's slot discipline: never pass
        // guest objects across slots — convert eagerly or share host-backed state.
        final Context.Builder contextBuilder = Context.newBuilder( "js" )
            .allowHostAccess( hostAccess( contextRef ) )
            .allowHostClassLookup( className -> true )
            .option( "js.strict", "true" )
            .allowHostClassLoading( true );

        if ( sharedEngine != null )
        {
            contextBuilder.engine( sharedEngine.get() );
        }
        if ( classLoader != null )
        {
            contextBuilder.hostClassLoader( classLoader );
        }

        final Context context = contextBuilder.build();
        contextRef.set( context );
        return context;
    }

    /**
     * JS functions passed to Java functional-interface parameters (e.g. bean setters taking a
     * {@code Function} or {@code Consumer}) must become {@link JsFunctionHandle}s instead of the
     * default host proxies: default proxies enter the context on whatever thread invokes them
     * and fail on concurrent access, while handles route through the context's ownership
     * discipline. Host-object functions are left to the default conversion.
     * <p>
     * Handles must lock the exact {@link Context} instance every other execution path
     * synchronizes on, so the mappings close over a reference set once the context is built —
     * deriving the context from the {@link Value} can yield a different wrapper instance,
     * silently breaking mutual exclusion.
     * <p>
     * The mappings are a closed set, and every other interface is refused rather than left to the
     * default conversion. {@code HostAccess.ALL} satisfies <em>any</em> interface from a script
     * function — and from a plain script object, which no mapping can intercept — with a proxy
     * that enters the context on the calling thread. That proxy works until the first invocation
     * from another thread, so a bean declaring an interface outside this set would fail under
     * load rather than at the boundary. Refusing it reports the unsupported type at the call
     * that passes the function, and the fix is to declare one of the types below.
     */
    private static HostAccess hostAccess( final AtomicReference<Context> contextRef )
    {
        final Predicate<Value> isJsFunction = value -> value.canExecute() && !value.isHostObject();
        final Function<Value, JsFunctionHandle> toHandle = value -> new JsFunctionHandle( contextRef.get(), value );
        final Function<Value, JsFunctionHandle.TwoArg> toTwoArg = value -> new JsFunctionHandle.TwoArg( toHandle.apply( value ) );
        return HostAccess.newBuilder( HostAccess.ALL )
            .allowAllImplementations( false )
            .allowAllClassImplementations( false )
            .targetTypeMapping( Value.class, Function.class, isJsFunction, toHandle::apply )
            .targetTypeMapping( Value.class, UnaryOperator.class, isJsFunction, toHandle::apply )
            .targetTypeMapping( Value.class, BiFunction.class, isJsFunction, toTwoArg::apply )
            .targetTypeMapping( Value.class, BinaryOperator.class, isJsFunction, toTwoArg::apply )
            .targetTypeMapping( Value.class, Consumer.class, isJsFunction, toHandle::apply )
            .targetTypeMapping( Value.class, BiConsumer.class, isJsFunction, toHandle::apply )
            .targetTypeMapping( Value.class, Runnable.class, isJsFunction, toHandle::apply )
            .targetTypeMapping( Value.class, Supplier.class, isJsFunction, toHandle::apply )
            .targetTypeMapping( Value.class, Callable.class, isJsFunction, toHandle::apply )
            .targetTypeMapping( Value.class, Predicate.class, isJsFunction, toHandle::apply )
            .targetTypeMapping( Value.class, BiPredicate.class, isJsFunction, toTwoArg::apply )
            .targetTypeMapping( Value.class, Comparator.class, isJsFunction, toHandle::apply )
            .build();
    }
}
