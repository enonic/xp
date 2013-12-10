package com.enonic.wem.portal.controller;

import java.nio.file.Path;

import javax.inject.Inject;

import com.enonic.wem.portal.script.runner.ScriptRunnerFactory;
import com.enonic.wem.portal.script.runner.ScriptRunnerFactoryImpl;

public final class JsControllerFactoryImpl
    implements JsControllerFactory
{
    private final ScriptRunnerFactory scriptRunnerFactory;

    @Inject
    public JsControllerFactoryImpl( final ScriptRunnerFactoryImpl scriptRunnerFactory )
    {
        this.scriptRunnerFactory = scriptRunnerFactory;
    }

    @Override
    public JsController newController( final Path path )
    {
        return new JsControllerImpl( this.scriptRunnerFactory, path );
    }
}
