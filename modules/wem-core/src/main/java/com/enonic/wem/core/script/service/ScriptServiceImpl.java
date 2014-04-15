package com.enonic.wem.core.script.service;

import javax.inject.Inject;

import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.core.script.ScriptRunner;
import com.enonic.wem.core.script.ScriptService;
import com.enonic.wem.core.script.compiler.ScriptCompiler;

public final class ScriptServiceImpl
    implements ScriptService
{
    @Inject
    protected ResourceService resourceService;

    @Inject
    protected ScriptCompiler compiler;

    @Override
    public ScriptRunner newRunner()
    {
        final ScriptRunnerImpl runner = new ScriptRunnerImpl();
        runner.compiler = this.compiler;
        runner.resourceService = this.resourceService;
        return runner;
    }
}
