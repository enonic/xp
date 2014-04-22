package com.enonic.wem.admin;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.web.WebInitializerBinder;

public final class AdminModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        WebInitializerBinder.from( binder() ).add( AdminWebInitializer.class );
    }
}
