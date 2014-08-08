package com.enonic.wem.portal.script;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.controller.JsControllerFactoryImpl;
import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.compiler.ScriptCompilerImpl;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.loader.ScriptLoaderImpl;
import com.enonic.wem.portal.script.runner.ScriptRunnerFactory;
import com.enonic.wem.portal.script.runner.ScriptRunnerFactoryImpl;

public final class ScriptModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ScriptLoader.class ).to( ScriptLoaderImpl.class ).in( Singleton.class );
        bind( ScriptCompiler.class ).to( ScriptCompilerImpl.class ).in( Singleton.class );
        bind( ScriptRunnerFactory.class ).to( ScriptRunnerFactoryImpl.class ).in( Singleton.class );
        bind( JsControllerFactory.class ).to( JsControllerFactoryImpl.class ).in( Singleton.class );
    }
}
