package com.enonic.wem.portal;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import com.enonic.wem.web.servlet.WebInitializer;

public final class PortalWebInitializer
    implements WebInitializer
{
    private PortalServlet portalServlet;

    @Override
    public void initialize( final ServletContext context )
    {
        final ServletRegistration.Dynamic portalServlet = context.addServlet( "portal", this.portalServlet );
        portalServlet.setLoadOnStartup( 4 );
        portalServlet.addMapping( "/site/*" );
    }

    @Inject
    public void setPortalServlet( final PortalServlet portalServlet )
    {
        this.portalServlet = portalServlet;
    }
}
