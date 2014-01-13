package com.enonic.wem.portal.controller;

import javax.inject.Inject;

import com.enonic.wem.portal.postprocess.PostProcessorFactory;
import com.enonic.wem.portal.script.runner.ScriptRunnerFactory;

public final class JsControllerFactoryImpl
    implements JsControllerFactory
{
    @Inject
    protected ScriptRunnerFactory scriptRunnerFactory;

    @Inject
    protected PostProcessorFactory postProcessorFactory;

    @Override
    public JsController newController()
    {
        final JsControllerImpl jsController = new JsControllerImpl( this.scriptRunnerFactory.newRunner() );
        jsController.postProcessor( postProcessorFactory.newPostProcessor() );
        return jsController;
    }

    public void setScriptRunnerFactory( final ScriptRunnerFactory scriptRunnerFactory )
    {
        this.scriptRunnerFactory = scriptRunnerFactory;
    }

    public void setPostProcessorFactory( final PostProcessorFactory postProcessorFactory )
    {
        this.postProcessorFactory = postProcessorFactory;
    }
}
