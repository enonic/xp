package com.enonic.wem.portal.script.runner;

import javax.inject.Inject;
import javax.inject.Provider;

import com.enonic.wem.portal.script.lib.ContextScriptBean;
import com.enonic.wem.portal.script.lib.SystemScriptBean;

public final class ScriptRunnerFactoryImpl
    implements ScriptRunnerFactory
{
    private final ScriptCompiler compiler;

    @Inject
    protected SystemScriptBean systemScriptBean;

    @Inject
    protected Provider<ContextScriptBean> contextServiceBeans;

    public ScriptRunnerFactoryImpl()
    {
        this.compiler = new ScriptCompiler();
    }

    @Override
    public ScriptRunner newRunner()
    {
        final ScriptRunnerImpl runner = new ScriptRunnerImpl();
        runner.compiler = this.compiler;
        runner.contextServiceBean = this.contextServiceBeans.get();
        runner.property( "system", this.systemScriptBean );
        return runner;
    }
}
