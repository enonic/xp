package com.enonic.wem.portal.internal.script;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.portal.internal.controller.JsControllerFactoryImpl;

public final class ScriptModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( JsControllerFactory.class ).to( JsControllerFactoryImpl.class ).in( Singleton.class );
    }
}
