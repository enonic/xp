package com.enonic.wem.portal.script.runner;

import javax.inject.Inject;
import javax.inject.Provider;

import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.lib.ContextScriptBean;
import com.enonic.wem.portal.script.lib.SystemScriptBean;
import com.enonic.wem.portal.script.loader.ScriptLoader;

public class ScriptRunnerFactoryImpl
    implements ScriptRunnerFactory
{
    @Inject
    protected ScriptCompiler compiler;

    @Inject
    protected ScriptLoader scriptLoader;

    @Inject
    protected SystemScriptBean systemScriptBean;

    @Inject
    protected Provider<ContextScriptBean> contextServiceBeans;

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

    public void setSystemScriptBean( final SystemScriptBean systemScriptBean )
    {
        this.systemScriptBean = systemScriptBean;
    }

    public void setContextServiceBeans( final Provider<ContextScriptBean> contextServiceBeans )
    {
        this.contextServiceBeans = contextServiceBeans;
    }
}
