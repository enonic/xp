package com.enonic.wem.portal.script.runner;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.runtime.JsApiBridge;

public final class ScriptRunnerFactoryImpl
    implements ScriptRunnerFactory
{
    private final Scriptable globalScope;

    @Inject
    protected ScriptCompiler compiler;

    @Inject
    protected ScriptLoader scriptLoader;

    @Inject
    protected Provider<JsApiBridge> apiBridgeProvider;

    public ScriptRunnerFactoryImpl()
    {
        ContextFactory.initGlobal( new RhinoContextFactory() );
        this.globalScope = new GlobalScopeInitializer().initialize();
    }

    @Override
    public ScriptRunner newRunner()
    {
        final ScriptRunnerImpl runner = new ScriptRunnerImpl();
        runner.scriptLoader = this.scriptLoader;
        runner.compiler = this.compiler;
        runner.apiBridge = this.apiBridgeProvider.get();
        runner.globalScope = this.globalScope;
        return runner;
    }
}
