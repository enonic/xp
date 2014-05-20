package com.enonic.wem.core.web.jaxrs;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

import com.enonic.wem.core.web.servlet.ServletRequestHolder;

public abstract class JaxRsServlet
    extends HttpServlet
{
    private final class Container
        extends ServletContainer
    {
        @Override
        protected ResourceConfig getDefaultResourceConfig( final Map<String, Object> props, final WebConfig wc )
            throws ServletException
        {
            JaxRsServlet.this.config.getProperties().putAll( props );
            return JaxRsServlet.this.config;
        }

        @Override
        protected void initiate( final ResourceConfig rc, final WebApplication wa )
        {
            JaxRsServlet.this.configure();
            wa.initiate( rc );
        }
    }

    private final JaxRsResourceConfig config;

    @Inject
    protected Injector injector;

    private final Container container;

    public JaxRsServlet()
    {
        this.config = new JaxRsResourceConfig();
        this.container = new Container();
    }

    @Override
    public final void init( final ServletConfig config )
        throws ServletException
    {
        this.container.init( config );
    }

    @Override
    protected final void service( final HttpServletRequest req, final HttpServletResponse resp )
        throws ServletException, IOException
    {
        try
        {
            ServletRequestHolder.setRequest( req );
            this.container.service( req, resp );
        }
        finally
        {
            ServletRequestHolder.setRequest( null );
        }
    }

    @Override
    public final void destroy()
    {
        this.container.destroy();
    }

    protected final void addSingleton( final Class<?> type )
    {
        this.config.addSingleton( this.injector.getInstance( type ) );
    }

    protected final void setFeature( final String name, final boolean flag )
    {
        this.config.setFeature( name, flag );
    }

    protected final void setProperty( final String name, final Object value )
    {
        this.config.setProperty( name, value );
    }

    protected abstract void configure();
}
