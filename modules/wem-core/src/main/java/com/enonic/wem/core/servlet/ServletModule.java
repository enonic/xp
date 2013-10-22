package com.enonic.wem.core.servlet;

import com.google.inject.AbstractModule;

public final class ServletModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        WebInitializerBinder.from( binder() ).add( ServletWebInitializer.class );
    }
}
