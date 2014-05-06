package com.enonic.wem.boot;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;

import com.enonic.wem.admin.AdminModule;
import com.enonic.wem.core.CoreModule;
import com.enonic.wem.portal.PortalModule;

final class BootModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new ServletModule() );
        install( new CoreModule() );
        install( new AdminModule() );
        install( new PortalModule() );
    }
}
