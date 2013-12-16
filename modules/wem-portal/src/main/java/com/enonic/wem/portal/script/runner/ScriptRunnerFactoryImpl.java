package com.enonic.wem.portal.script.runner;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mozilla.javascript.ContextFactory;

import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.runtime.JsApiBridge;
import com.enonic.wem.portal.script.runtime.RootRuntimeObject;

public final class ScriptRunnerFactoryImpl
    implements ScriptRunnerFactory
{
    @Inject
    protected ScriptCompiler compiler;

    @Inject
    protected ScriptLoader scriptLoader;

    @Inject
    protected Provider<RootRuntimeObject> rootRuntimeObjects;

    @Inject
    protected Provider<JsApiBridge> apiBridgeProvider;

    public ScriptRunnerFactoryImpl()
    {
        ContextFactory.initGlobal( new RhinoContextFactory() );
    }

    @Override
    public ScriptRunner newRunner()
    {
        final ScriptRunnerImpl runner = new ScriptRunnerImpl();
        runner.scriptLoader = this.scriptLoader;
        runner.compiler = this.compiler;
        runner.apiBridge = this.apiBridgeProvider.get();
        runner.rootRuntimeObjects = this.rootRuntimeObjects;
        return runner;
    }
}
