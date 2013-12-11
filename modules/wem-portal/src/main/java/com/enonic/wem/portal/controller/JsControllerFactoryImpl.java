package com.enonic.wem.portal.controller;

import javax.inject.Inject;

import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.runner.ScriptRunnerFactory;

public final class JsControllerFactoryImpl
    implements JsControllerFactory
{
    @Inject
    protected ScriptRunnerFactory scriptRunnerFactory;

    @Inject
    private ScriptLoader loader;

    @Override
    public JsController newController()
    {
        return new JsControllerImpl( this.scriptRunnerFactory, this.loader );
    }
}
