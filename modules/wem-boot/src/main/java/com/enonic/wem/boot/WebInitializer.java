package com.enonic.wem.boot;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import com.enonic.wem.admin.ResourceServlet;
import com.enonic.wem.admin.app.AppServlet;
import com.enonic.wem.admin.rest.RestServlet;
import com.enonic.wem.core.web.servlet.RequestContextListener;
import com.enonic.wem.portal.PortalServlet;

@Singleton
final class WebInitializer
{
    @Inject
    protected RestServlet restServlet;

    @Inject
    protected ResourceServlet resourceServlet;

    @Inject
    protected AppServlet appServlet;

    @Inject
    protected PortalServlet portalServlet;

    public void initialize( final ServletContext context )
    {
        context.addListener( new RequestContextListener() );

        final ServletRegistration.Dynamic resourceServlet = context.addServlet( "resource", this.resourceServlet );
        resourceServlet.setLoadOnStartup( 2 );
        resourceServlet.addMapping( "/" );

        final ServletRegistration.Dynamic restServlet = context.addServlet( "rest", this.restServlet );
        restServlet.setLoadOnStartup( 3 );
        restServlet.addMapping( "/admin/rest/*" );

        final ServletRegistration.Dynamic portalServlet = context.addServlet( "portal", this.portalServlet );
        portalServlet.setLoadOnStartup( 4 );
        portalServlet.addMapping( "/portal/*" );
    }
}
