package com.enonic.wem.portal.script.runner;

import javax.inject.Inject;
import javax.inject.Provider;

import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.runtime.JsApiBridge;

public final class ScriptRunnerFactoryImpl
    implements ScriptRunnerFactory
{
    @Inject
    protected ScriptCompiler compiler;

    @Inject
    protected ScriptLoader scriptLoader;

    @Inject
    protected Provider<JsApiBridge> apiBridgeProvider;

    @Override
    public ScriptRunner newRunner()
    {
        final ScriptRunnerImpl runner = new ScriptRunnerImpl();
        runner.scriptLoader = this.scriptLoader;
        runner.compiler = this.compiler;
        runner.apiBridge = this.apiBridgeProvider.get();
        return runner;
    }
}
