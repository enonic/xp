package com.enonic.wem.portal;

import java.io.IOException;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

import com.enonic.wem.core.web.servlet.ServletRequestHolder;
import com.enonic.wem.portal.content.ComponentResource;
import com.enonic.wem.portal.content.ContentResource;
import com.enonic.wem.portal.exception.mapper.DefaultExceptionMapper;
import com.enonic.wem.portal.exception.mapper.PortalWebExceptionMapper;
import com.enonic.wem.portal.exception.mapper.SourceExceptionMapper;
import com.enonic.wem.portal.exception.mapper.WebApplicationExceptionMapper;
import com.enonic.wem.portal.underscore.ImageByIdResource;
import com.enonic.wem.portal.underscore.ImageResource;
import com.enonic.wem.portal.underscore.PublicResource;
import com.enonic.wem.portal.underscore.ServicesResource;

@Singleton
public final class PortalServlet
    extends HttpServlet
{
    private final class Container
        extends ServletContainer
    {
        @Override
        protected ResourceConfig getDefaultResourceConfig( final Map<String, Object> props, final WebConfig wc )
            throws ServletException
        {
            PortalServlet.this.config.getProperties().putAll( props );
            return PortalServlet.this.config;
        }

        @Override
        protected void initiate( final ResourceConfig rc, final WebApplication wa )
        {
            PortalServlet.this.configure();
            wa.initiate( rc );
        }
    }

    private final DefaultResourceConfig config;

    @Inject
    protected Injector injector;

    private final Container container;

    public PortalServlet()
    {
        this.config = new DefaultResourceConfig();
        this.container = new Container();
    }

    @Override
    public void init( final ServletConfig config )
        throws ServletException
    {
        this.container.init( config );
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse resp )
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
    public void destroy()
    {
        this.container.destroy();
    }

    private void addSingleton( final Class<?> type )
    {
        this.config.getSingletons().add( this.injector.getInstance( type ) );
    }

    private void configure()
    {
        addSingleton( ImageResource.class );
        addSingleton( ImageByIdResource.class );
        addSingleton( PublicResource.class );
        addSingleton( ContentResource.class );
        addSingleton( ComponentResource.class );
        addSingleton( ServicesResource.class );
        addSingleton( SourceExceptionMapper.class );
        addSingleton( PortalWebExceptionMapper.class );
        addSingleton( DefaultExceptionMapper.class );
        addSingleton( WebApplicationExceptionMapper.class );
    }
}
