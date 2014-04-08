package com.enonic.wem.core.script.service;

import javax.inject.Inject;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.module.source.SourceResolver;
import com.enonic.wem.core.script.ScriptRunner;
import com.enonic.wem.core.script.ScriptRunnerService;
import com.enonic.wem.core.script.engine.ScriptEngineService;

public final class ScriptServiceImpl
    implements ScriptRunnerService
{
    private SourceResolver sourceResolver;

    private ScriptEngineService scriptEngineService;

    @Override
    public ScriptRunner newRunner( final ModuleResourceKey resource )
    {
        final ScriptRunnerImpl runner = new ScriptRunnerImpl();
        runner.sourceResolver = this.sourceResolver;
        runner.scriptEngineService = this.scriptEngineService;
        runner.resourceKey = resource;
        runner.bindings = this.scriptEngineService.createBindings();
        return runner;
    }

    @Inject
    public void setSourceResolver( final SourceResolver sourceResolver )
    {
        this.sourceResolver = sourceResolver;
    }

    @Inject
    public void setScriptEngineService( final ScriptEngineService scriptEngineService )
    {
        this.scriptEngineService = scriptEngineService;
    }
}
