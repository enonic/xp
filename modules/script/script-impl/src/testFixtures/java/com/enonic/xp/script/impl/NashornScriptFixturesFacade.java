package com.enonic.xp.script.impl;

import java.util.concurrent.Executors;

import javax.script.ScriptEngine;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptFixturesFacade;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutorImpl;
import com.enonic.xp.script.impl.function.ApplicationInfoBuilder;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.impl.util.JavascriptHelperFactory;
import com.enonic.xp.script.impl.util.NashornHelper;
import com.enonic.xp.script.impl.value.ScriptValueFactory;
import com.enonic.xp.script.impl.value.ScriptValueFactoryImpl;
import com.enonic.xp.script.runtime.ScriptSettings;

public class NashornScriptFixturesFacade
    implements ScriptFixturesFacade
{
    public ScriptValueFactory<?> scriptValueFactory()
    {
        final ScriptEngine scriptEngine = NashornHelper.getScriptEngine( ScriptFixturesFacade.class.getClassLoader() );

        return new ScriptValueFactoryImpl( new JavascriptHelperFactory( scriptEngine ).create() );
    }

    public ScriptExecutor createExecutor( final ScriptSettings scriptSettings, final ServiceRegistry serviceRegistry,
                                          ResourceService resourceService, Application application )
    {
        return new ScriptExecutorImpl( Executors.newSingleThreadExecutor(), application.getClassLoader(), scriptSettings, serviceRegistry,
                                       resourceService, new ApplicationInfoBuilder( application.getKey(), application.getConfig(),
                                                                                    application.getVersion() ) );
    }
}
