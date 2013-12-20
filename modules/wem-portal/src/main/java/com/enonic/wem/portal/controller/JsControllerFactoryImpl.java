package com.enonic.wem.portal.controller;

import javax.inject.Inject;

import com.enonic.wem.portal.script.runner.ScriptRunnerFactory;

public final class JsControllerFactoryImpl
    implements JsControllerFactory
{
    @Inject
    protected ScriptRunnerFactory scriptRunnerFactory;

    @Override
    public JsController newController()
    {
        return new JsControllerImpl( this.scriptRunnerFactory.newRunner() );
    }

    public void setScriptRunnerFactory( final ScriptRunnerFactory scriptRunnerFactory )
    {
        this.scriptRunnerFactory = scriptRunnerFactory;
    }
}
