package com.enonic.wem.admin;

import javax.inject.Inject;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import com.enonic.wem.admin.app.AppServlet;
import com.enonic.wem.admin.rest.RestServlet;
import com.enonic.wem.core.servlet.WebInitializer;

public final class AdminWebInitializer
    implements WebInitializer
{
    private RestServlet restServlet;

    private ResourceServlet resourceServlet;

    private AppServlet appServlet;

    @Override
    public void initialize( final ServletContext context )
    {
        final ServletRegistration.Dynamic resourceServlet = context.addServlet( "resource", this.resourceServlet );
        resourceServlet.setLoadOnStartup( 2 );
        resourceServlet.addMapping( "/" );

        final ServletRegistration.Dynamic restServlet = context.addServlet( "rest", this.restServlet );
        restServlet.setLoadOnStartup( 3 );
        restServlet.addMapping( "/admin/rest/*" );

        final MultipartConfigElement multiPartConfig = new MultipartConfigElement( null, -1, -1, 0 );
        restServlet.setMultipartConfig( multiPartConfig );

        final ServletRegistration.Dynamic appServlet = context.addServlet( "app", this.appServlet );
        appServlet.setLoadOnStartup( 4 );
        appServlet.addMapping( "/admin" );
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

    @Inject
    public void setAppServlet( final AppServlet appServlet )
    {
        this.appServlet = appServlet;
    }
}
