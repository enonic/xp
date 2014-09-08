package com.enonic.wem.portal.internal.controller;

import javax.inject.Inject;

import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.script.ScriptService;

public final class JsControllerFactoryImpl
    implements JsControllerFactory
{
    @Inject
    protected ScriptService scriptService;

    @Inject
    protected PostProcessor postProcessor;

    @Override
    public JsController newController()
    {
        final JsControllerImpl jsController = new JsControllerImpl( this.scriptService );
        jsController.postProcessor( this.postProcessor );
        return jsController;
    }
}
