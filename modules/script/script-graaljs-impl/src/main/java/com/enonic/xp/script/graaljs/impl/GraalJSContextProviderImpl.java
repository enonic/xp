package com.enonic.xp.script.graaljs.impl;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component
public class GraalJSContextProviderImpl
    implements GraalJSContextProvider
{
    private final Context context;

    @Activate
    public GraalJSContextProviderImpl()
    {
        this.context = Context.newBuilder( "js" ).
            allowHostAccess( HostAccess.ALL ).
            allowHostClassLookup( className -> true ).
            allowExperimentalOptions( true ).
            engine( Engine.newBuilder().allowExperimentalOptions( true ).build() ).
            option( "js.nashorn-compat", "true" ).
            build();
    }

    @Deactivate
    public void deactivate()
    {
        this.context.close();
    }

    @Override
    public Context getContext()
    {
        return context;
    }
}
