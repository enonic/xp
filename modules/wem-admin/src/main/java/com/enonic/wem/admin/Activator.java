package com.enonic.wem.admin;

import javax.servlet.http.HttpServlet;

import com.enonic.wem.core.config.ConfigProperties;
import com.enonic.wem.guice.GuiceActivator;

public final class Activator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        install( new AdminModule() );

        // Import services
        importService( ConfigProperties.class ).toSingle();

        // Export services
        exportService( HttpServlet.class ).property( "alias", "/" ).to( ResourceServlet.class );
        // exportService( HttpServlet.class ).property( "alias", "/admin/rest" ).to( RestServlet.class );
    }
}
