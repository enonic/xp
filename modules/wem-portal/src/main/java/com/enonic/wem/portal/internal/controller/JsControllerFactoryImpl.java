package com.enonic.wem.portal.internal.controller;

import javax.inject.Inject;

import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.portal.internal.script.lib.SystemScriptBean;
import com.enonic.wem.script.ScriptRunner;
import com.enonic.wem.script.ScriptRunnerFactory;

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
