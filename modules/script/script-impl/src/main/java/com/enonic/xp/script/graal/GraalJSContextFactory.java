package com.enonic.xp.script.graal;


import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import com.enonic.xp.script.graal.util.JsFunctionHandle;

public final class GraalJSContextFactory
{
    private final ClassLoader classLoader;

    private final Engine sharedEngine;

    public GraalJSContextFactory()
    {
        this( null, null );
    }

    public GraalJSContextFactory( final ClassLoader classLoader, final Engine sharedEngine )
    {
        this.classLoader = classLoader;
        this.sharedEngine = sharedEngine;
    }

    public Context create()
    {
        final AtomicReference<Context> contextRef = new AtomicReference<>();

        final Context.Builder contextBuilder = Context.newBuilder( "js" )
            .allowHostAccess( hostAccess( contextRef ) )
            .allowHostClassLookup( className -> true )
            .option( "js.strict", "true" )
            .allowHostClassLoading( true );

        if ( Boolean.getBoolean( "xp.script-engine.nashorn-compat" ) )
        {
            contextBuilder.allowExperimentalOptions( true );
            contextBuilder.option( "js.nashorn-compat", "true" );
            contextBuilder.option( "js.ecmascript-version", "2020" );
        }

        if ( sharedEngine != null )
        {
            contextBuilder.engine( sharedEngine );
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
     */
    private static HostAccess hostAccess( final AtomicReference<Context> contextRef )
    {
        final Predicate<Value> isJsFunction = value -> value.canExecute() && !value.isHostObject();
        final Function<Value, JsFunctionHandle> toHandle = value -> new JsFunctionHandle( contextRef.get(), value );
        return HostAccess.newBuilder( HostAccess.ALL )
            .targetTypeMapping( Value.class, Function.class, isJsFunction, toHandle::apply )
            .targetTypeMapping( Value.class, Consumer.class, isJsFunction, toHandle::apply )
            .targetTypeMapping( Value.class, Runnable.class, isJsFunction, toHandle::apply )
            .targetTypeMapping( Value.class, Supplier.class, isJsFunction, toHandle::apply )
            .targetTypeMapping( Value.class, Predicate.class, isJsFunction, toHandle::apply )
            .build();
    }
}
