package com.enonic.wem.portal.internal.controller;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

public final class ControllerModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( JsControllerFactory.class ).to( JsControllerFactoryImpl.class ).in( Singleton.class );
    }
}
