package com.enonic.xp.script.graal;


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
        final Context.Builder contextBuilder = Context.newBuilder( "js" )
            .allowHostAccess( hostAccess() )
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

        return contextBuilder.build();
    }

    /**
     * JS functions passed to Java functional-interface parameters (e.g. bean setters taking a
     * {@code Function} or {@code Consumer}) must become {@link JsFunctionHandle}s instead of the
     * default host proxies: default proxies enter the context on whatever thread invokes them
     * and fail on concurrent access, while handles route through the context's ownership
     * discipline. Host-object functions are left to the default conversion.
     */
    private static HostAccess hostAccess()
    {
        final Predicate<Value> isJsFunction = value -> value.canExecute() && !value.isHostObject();
        return HostAccess.newBuilder( HostAccess.ALL )
            .targetTypeMapping( Value.class, Function.class, isJsFunction, JsFunctionHandle::of )
            .targetTypeMapping( Value.class, Consumer.class, isJsFunction, JsFunctionHandle::of )
            .targetTypeMapping( Value.class, Runnable.class, isJsFunction, JsFunctionHandle::of )
            .targetTypeMapping( Value.class, Supplier.class, isJsFunction, JsFunctionHandle::of )
            .targetTypeMapping( Value.class, Predicate.class, isJsFunction, JsFunctionHandle::of )
            .build();
    }
}
