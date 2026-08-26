package com.enonic.xp.script.graal;


import java.util.Comparator;
import java.util.concurrent.Callable;
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
    /**
     * One instance for the process: contexts sharing an engine must present {@link
     * HostAccess#equals equal} configurations, and two built separately never are — their target
     * type mappings hold distinct lambdas, compared by identity. Built per context, only the first
     * context on the shared engine would ever open.
     */
    private static final HostAccess HOST_ACCESS = hostAccess();

    private static final String CREATOR_CONTEXT = "com.enonic.xp.script.graal.creatorContext";

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
        // Note: guest values passed between contexts silently re-enter their owning context on
        // every access — with the context pool that means latent serialization and deadlock
        // risk. allowValueSharing(false) would fail fast on such leaks, but GraalJS forbids
        // disabling it for contexts bound to a shared engine, which we require for code-cache
        // sharing. Isolation therefore relies on the executor's slot discipline: never pass
        // guest objects across slots — convert eagerly or share host-backed state.
        final Context.Builder contextBuilder = Context.newBuilder( "js" )
            .allowHostAccess( HOST_ACCESS )
            .allowHostClassLookup( className -> true )
            .option( "js.strict", "true" )
            .option( "js.text-encoding", "true" )
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
        // where creatorContext() reads it back from; these bindings are per context and die with it
        context.getPolyglotBindings().putMember( CREATOR_CONTEXT, new CreatorHolder( context ) );
        return context;
    }

    /**
     * Carries the context without exposing it. Scripts read the polyglot bindings through
     * {@code Java.type('org.graalvm.polyglot.Context')}, so a bare {@link Context} there would be
     * {@code close()} one property read away; this has no member {@code HostAccess.ALL} can reach.
     * A guard against reaching the context by accident, not a sandbox — reflection still unwraps
     * it, and scripts hold the host's authority here by design.
     */
    private static final class CreatorHolder
    {
        private final Context context;

        private CreatorHolder( final Context context )
        {
            this.context = context;
        }
    }

    /**
     * JS functions passed to Java functional-interface parameters (e.g. bean setters taking a
     * {@code Function} or {@code Consumer}) must become {@link JsFunctionHandle}s instead of the
     * default host proxies: default proxies enter the context on whatever thread invokes them
     * and fail on concurrent access, while handles route through the context's ownership
     * discipline. Host-object functions are left to the default conversion.
     * <p>
     * Handles must lock the exact {@link Context} instance every other execution path
     * synchronizes on, so the mappings resolve it through {@link #creatorContext()} at conversion
     * time rather than capturing it when the context is built — a captured reference would make
     * this configuration per context, and contexts on a shared engine must present the same one.
     * <p>
     * The mappings are a closed set: {@code HostAccess.ALL} would satisfy any other interface
     * with a proxy that enters the context on the calling thread and so fails only under load,
     * which is why implementations are disallowed. A bean wanting a script function must declare
     * one of the types below.
     */
    private static HostAccess hostAccess()
    {
        final Predicate<Value> isJsFunction = value -> value.canExecute() && !value.isHostObject();
        final Function<Value, JsFunctionHandle> toHandle = value -> new JsFunctionHandle( creatorContext(), value );
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

    /**
     * Resolved from the entered context, because that is the one that owns the value being
     * converted — the handle must lock its owner, not whatever execution happens to be in progress.
     * <p>
     * A {@link ScopedValue} published by the executor would be the obvious channel and is the wrong
     * one: it names the context the executor last bound, which stops matching the entered context
     * as soon as a handle belonging to one application is invoked inside another's execution, and
     * the handle then locks a context it does not run in. The binding read here is script-reachable
     * and a script can break it, but only for its own context, and correctness comes first.
     */
    private static Context creatorContext()
    {
        final Value holder = currentBindings().getMember( CREATOR_CONTEXT );
        final Object found = holder != null && holder.isHostObject() ? holder.asHostObject() : null;
        if ( !( found instanceof CreatorHolder ) )
        {
            throw new IllegalStateException( "Script context was not created by " + GraalJSContextFactory.class.getSimpleName() +
                                                 ", or its creator binding was replaced" );
        }
        return ( (CreatorHolder) found ).context;
    }

    /**
     * {@link Context#getCurrent()} throws when nothing is entered, and reads as a Graal internal
     * error rather than as what it is: a conversion reached from a thread with no context to
     * resolve a creator from.
     */
    private static Value currentBindings()
    {
        try
        {
            return Context.getCurrent().getPolyglotBindings();
        }
        catch ( final IllegalStateException e )
        {
            throw new IllegalStateException( "No script context is entered on this thread, so its creator cannot be resolved", e );
        }
    }
}
