package com.enonic.wem.portal.script.runner;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.lib.ContextScriptBean;
import com.enonic.wem.portal.script.lib.GlobalScriptBean;
import com.enonic.wem.portal.script.loader.ScriptLoader;

public class ScriptRunnerFactoryImpl
    implements ScriptRunnerFactory
{
    @Inject
    protected ScriptCompiler compiler;

    @Inject
    protected ScriptLoader scriptLoader;

    @Inject
    protected GlobalScriptBean globalScriptBean;

    @Inject
    protected ModuleResourcePathResolver moduleResourcePathResolver;

    @Inject
    protected Provider<ContextScriptBean> contextServiceBeans;

    protected ScriptableObject rootScope;

    static {
        ContextFactory.initGlobal( new RhinoContextFactory() );
    }

    public ScriptRunnerFactoryImpl()
    {
    }

    private void initRootScope()
    {
        if ( this.rootScope == null )
        {
            this.rootScope = createRootScope();
        }
    }

    @Override
    public ScriptRunner newRunner()
    {
        initRootScope();

        final ScriptRunnerImpl runner = new ScriptRunnerImpl();
        runner.scriptLoader = this.scriptLoader;
        runner.compiler = this.compiler;
        runner.rootScope = this.rootScope;
        runner.contextServiceBean = this.contextServiceBeans.get();
        return runner;
    }

    private ScriptableObject createRootScope()
    {
        final Context cx = Context.enter();

        try
        {
            final ScriptableObject scope = cx.initStandardObjects( this.globalScriptBean, true );
            this.globalScriptBean.initialize();
            return scope;
        }
        finally
        {
            Context.exit();
        }
    }

    public void setGlobalScriptBean( final GlobalScriptBean globalScriptBean )
    {
        this.globalScriptBean = globalScriptBean;
    }

    public void setContextServiceBeans( final Provider<ContextScriptBean> contextServiceBeans )
    {
        this.contextServiceBeans = contextServiceBeans;
    }
}
