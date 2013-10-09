package com.enonic.wem.admin;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import com.enonic.wem.admin.rest.RestServlet;
import com.enonic.wem.web.servlet.WebInitializer;

public final class AdminWebInitializer
    implements WebInitializer
{
    private RestServlet restServlet;

    private ResourceServlet resourceServlet;

    @Override
    public void initialize( final ServletContext context )
    {
        final ServletRegistration.Dynamic resourceServlet = context.addServlet( "resource", this.resourceServlet );
        resourceServlet.setLoadOnStartup( 2 );
        resourceServlet.addMapping( "/" );

        final ServletRegistration.Dynamic restServlet = context.addServlet( "rest", this.restServlet );
        restServlet.setLoadOnStartup( 3 );
        restServlet.addMapping( "/admin/rest/*" );
    }

    @Inject
    public void setRestServlet( final RestServlet restServlet )
    {
        this.restServlet = restServlet;
    }

    @Inject
    public void setResourceServlet( final ResourceServlet resourceServlet )
    {
        this.resourceServlet = resourceServlet;
    }
}
