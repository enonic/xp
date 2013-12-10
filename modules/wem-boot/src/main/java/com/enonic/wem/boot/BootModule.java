package com.enonic.wem.boot;

import javax.servlet.ServletContext;

import com.google.inject.AbstractModule;

import com.enonic.wem.admin.AdminModule;
import com.enonic.wem.core.CoreModule;
import com.enonic.wem.portal.PortalModule;
import com.enonic.wem.web.WebModule;

final class BootModule
    extends AbstractModule
{
    private final ServletContext context;

    public BootModule( final ServletContext context )
    {
        this.context = context;
    }

    @Override
    protected void configure()
    {
        bind( ServletContext.class ).toInstance( this.context );

        install( new CoreModule() );
        install( new WebModule() );
        install( new AdminModule() );
        install( new PortalModule() );
    }
}
