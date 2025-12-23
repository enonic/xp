package com.enonic.xp.script.graal;

import java.util.concurrent.Executors;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptFixturesFacade;
import com.enonic.xp.script.graal.executor.GraalScriptExecutor;
import com.enonic.xp.script.graal.util.GraalJavascriptHelperFactory;
import com.enonic.xp.script.graal.value.GraalScriptValueFactory;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.function.ApplicationInfoBuilder;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.impl.value.ScriptValueFactory;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

public class GraalJsScriptFixturesFacade
    implements ScriptFixturesFacade
{
    public ScriptValueFactory<?> scriptValueFactory()
    {
        return new GraalScriptValueFactory( new GraalJSContextFactory(), new GraalJavascriptHelperFactory() );
    }

    public ScriptExecutor createExecutor( final ScriptSettings scriptSettings, final ServiceRegistry serviceRegistry,
                                          ResourceService resourceService, Application application )
    {
        return new GraalScriptExecutor( new GraalJSContextFactory(), Executors.newSingleThreadExecutor(), application.getClassLoader(),
                                        scriptSettings, serviceRegistry, resourceService,
                                        new ApplicationInfoBuilder( application.getKey(), application.getConfig(),
                                                                    application.getVersion() ), RunMode.DEV );
    }
}
