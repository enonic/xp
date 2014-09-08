package com.enonic.wem.script.internal;

import javax.inject.Inject;

import com.enonic.wem.script.ScriptRunner;
import com.enonic.wem.script.ScriptRunnerFactory;

public final class RhinoScriptRunnerFactory
    implements ScriptRunnerFactory
{
    private final RhinoScriptCompiler compiler;

    private ScriptEnvironment environment;

    public RhinoScriptRunnerFactory()
    {
        this.compiler = new RhinoScriptCompiler();
    }

    @Inject
    public void setEnvironment( final ScriptEnvironment environment )
    {
        this.environment = environment;
    }

    @Override
    public ScriptRunner newRunner()
    {
        final RhinoScriptRunner runner = new RhinoScriptRunner();
        runner.compiler = this.compiler;
        runner.environment = this.environment;
        return runner;
    }
}
