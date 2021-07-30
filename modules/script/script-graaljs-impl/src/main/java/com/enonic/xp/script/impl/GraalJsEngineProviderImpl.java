package com.enonic.xp.script.impl;

import org.graalvm.polyglot.Engine;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component
public class GraalJsEngineProviderImpl
    implements GraalJsEngineProvider
{
    private final Engine engine;

    @Activate
    public GraalJsEngineProviderImpl()
    {
        this.engine = Engine.newBuilder().allowExperimentalOptions( true ).option( "engine.WarnInterpreterOnly", "false" ).build();
    }

    @Deactivate
    public void destroy()
    {
        this.engine.close();
    }

    @Override
    public Engine getEngine()
    {
        return engine;
    }
}
