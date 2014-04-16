package com.enonic.wem.core.script.service;

import javax.inject.Inject;

import org.mozilla.javascript.ContextFactory;

import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.core.script.ScriptRunner;
import com.enonic.wem.core.script.ScriptService;
import com.enonic.wem.core.script.compiler.ScriptCompiler;

public final class ScriptServiceImpl
    implements ScriptService
{
    private ResourceService resourceService;

    private ScriptCompiler compiler;

    static
    {
        ContextFactory.initGlobal( new RhinoContextFactory() );
    }

    @Override
    public ScriptRunner newRunner()
    {
        final ScriptRunnerImpl runner = new ScriptRunnerImpl();
        runner.compiler = this.compiler;
        runner.resourceService = this.resourceService;
        return runner;
    }

    @Inject
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Inject
    public void setCompiler( final ScriptCompiler compiler )
    {
        this.compiler = compiler;
    }
}
