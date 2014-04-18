package com.enonic.wem.core.script.service;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.core.script.ScriptRunner;
import com.enonic.wem.core.script.ScriptService;

public final class ScriptServiceImpl
    implements ScriptService
{
    private final ScriptEngine scriptEngine;

    private ResourceService resourceService;

    public ScriptServiceImpl()
    {
        final ScriptEngineManager engineManager = new ScriptEngineManager();
        this.scriptEngine = engineManager.getEngineByExtension( "js" );
    }

    @Override
    public ScriptRunner newRunner()
    {
        final ScriptRunnerImpl runner = new ScriptRunnerImpl();
        runner.scriptEngine = this.scriptEngine;
        runner.resourceService = this.resourceService;
        return runner;
    }

    @Inject
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
