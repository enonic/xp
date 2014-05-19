package com.enonic.wem.jaxrs.internal;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

@Singleton
final class JaxRsServlet
    extends HttpServlet
{
    private final ServletContainer container;

    private final RootApplication application;

    public JaxRsServlet()
    {
        this.container = new ServletContainer();
        this.application = new RootApplication();
        setProperty( ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true );
        setProperty( ServerProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true );
    }

    public void setProperty( final String name, final Object value )
    {
        this.application.setProperty( name, value );
    }

    public void addResource( final Object resource )
    {
        this.application.addResource( resource );
        refreshApplication();
    }

    public void removeResource( final Object resource )
    {
        this.application.removeResource( resource );
        refreshApplication();
    }

    private void refreshApplication()
    {
        this.container.reload( ResourceConfig.forApplication( this.application ) );
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse resp )
        throws ServletException, IOException
    {
        this.container.service( req, resp );
    }

    @Override
    public void init( final ServletConfig config )
        throws ServletException
    {
        this.container.init( config );
    }

    @Override
    public void destroy()
    {
        this.container.destroy();
    }
}
