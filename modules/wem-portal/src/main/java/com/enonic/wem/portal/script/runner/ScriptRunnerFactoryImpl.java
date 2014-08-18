package com.enonic.wem.portal.script.runner;

public final class ScriptRunnerFactoryImpl
    implements ScriptRunnerFactory
{
    private final ScriptCompiler compiler;

    public ScriptRunnerFactoryImpl()
    {
        this.compiler = new ScriptCompiler();
    }

    @Override
    public ScriptRunner newRunner()
    {
        final ScriptRunnerImpl runner = new ScriptRunnerImpl();
        runner.compiler = this.compiler;
        return runner;
    }
}
