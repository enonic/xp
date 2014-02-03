package com.enonic.wem.web.servlet;

import com.google.inject.AbstractModule;

import com.enonic.wem.web.WebInitializerBinder;

public final class ServletModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        WebInitializerBinder.from( binder() ).add( ServletWebInitializer.class );
    }
}
