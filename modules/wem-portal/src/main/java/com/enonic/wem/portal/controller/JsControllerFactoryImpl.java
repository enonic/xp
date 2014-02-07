package com.enonic.wem.portal.controller;

import javax.inject.Inject;

import com.enonic.wem.portal.postprocess.PostProcessor;
import com.enonic.wem.portal.script.runner.ScriptRunnerFactory;

public final class JsControllerFactoryImpl
    implements JsControllerFactory
{
    @Inject
    protected ScriptRunnerFactory scriptRunnerFactory;

    @Inject
    protected PostProcessor postProcessor;

    @Override
    public JsController newController()
    {
        final JsControllerImpl jsController = new JsControllerImpl( this.scriptRunnerFactory.newRunner() );
        jsController.postProcessor( this.postProcessor );
        return jsController;
    }

    public void setScriptRunnerFactory( final ScriptRunnerFactory scriptRunnerFactory )
    {
        this.scriptRunnerFactory = scriptRunnerFactory;
    }

    public void setPostProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
    }
}
