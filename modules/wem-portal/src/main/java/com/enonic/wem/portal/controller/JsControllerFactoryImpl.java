package com.enonic.wem.portal.controller;

import javax.inject.Inject;

import com.enonic.wem.portal.postprocess.PostProcessor;
import com.enonic.wem.portal.script.lib.SystemScriptBean;
import com.enonic.wem.portal.script.runner.ScriptRunner;
import com.enonic.wem.portal.script.runner.ScriptRunnerFactory;

public final class JsControllerFactoryImpl
    implements JsControllerFactory
{
    @Inject
    protected ScriptRunnerFactory scriptRunnerFactory;

    @Inject
    protected PostProcessor postProcessor;

    @Inject
    protected SystemScriptBean systemScriptBean;

    @Override
    public JsController newController()
    {
        final ScriptRunner runner = this.scriptRunnerFactory.newRunner();
        runner.property( SystemScriptBean.NAME, this.systemScriptBean );

        final JsControllerImpl jsController = new JsControllerImpl( runner );
        jsController.postProcessor( this.postProcessor );
        return jsController;
    }
}
