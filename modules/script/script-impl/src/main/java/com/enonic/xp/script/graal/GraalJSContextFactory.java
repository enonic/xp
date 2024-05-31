package com.enonic.xp.script.graal;


import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;

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
            .allowHostAccess( HostAccess.newBuilder( HostAccess.ALL ).build() )
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
}
