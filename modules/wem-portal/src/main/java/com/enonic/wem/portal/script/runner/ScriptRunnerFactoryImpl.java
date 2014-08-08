package com.enonic.wem.portal.script.runner;

import javax.inject.Inject;
import javax.inject.Provider;

import com.enonic.wem.portal.script.compiler.ScriptCompilerImpl;
import com.enonic.wem.portal.script.lib.ContextScriptBean;
import com.enonic.wem.portal.script.lib.SystemScriptBean;
import com.enonic.wem.portal.script.loader.ScriptLoader;

public final class ScriptRunnerFactoryImpl
    implements ScriptRunnerFactory
{
    private final ScriptCompilerImpl compiler;

    @Inject
    protected ScriptLoader scriptLoader;

    @Inject
    protected SystemScriptBean systemScriptBean;

    @Inject
    protected Provider<ContextScriptBean> contextServiceBeans;

    public ScriptRunnerFactoryImpl()
    {
        this.compiler = new ScriptCompilerImpl();
    }

    @Override
    public ScriptRunner newRunner()
    {
        final ScriptRunnerImpl runner = new ScriptRunnerImpl();
        runner.scriptLoader = this.scriptLoader;
        runner.compiler = this.compiler;
        runner.contextServiceBean = this.contextServiceBeans.get();
        runner.property( "system", this.systemScriptBean );
        return runner;
    }
}
