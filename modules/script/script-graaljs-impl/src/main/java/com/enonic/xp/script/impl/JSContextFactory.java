package com.enonic.xp.script.impl;

import java.util.List;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

public final class JSContextFactory
{
    private static final HostAccess HOST_ACCESS = HostAccess.newBuilder( HostAccess.ALL ).
        allowListAccess( true ).
        allowMapAccess( true ).
        targetTypeMapping( Value.class, Object.class, Value::hasArrayElements, value -> value.as( List.class ) ).
        build();

    public static Context create( final Engine sharedEngine, final ClassLoader classLoader )
    {
        final Context.Builder contextBuilder = Context.newBuilder( "js" ).
            allowHostAccess( HOST_ACCESS ).
            allowHostClassLookup( className -> true ).
            allowExperimentalOptions( true ).
            option( "js.nashorn-compat", "true" ).
            option( "js.ecmascript-version", "6" );

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

    public static Context create()
    {
        return create( null, null );
    }

    public static Context create( final ClassLoader classLoader )
    {
        return create( null, classLoader );
    }
}
