package com.enonic.wem.portal.internal;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.enonic.wem.core.web.servlet.ServletRequestHolder;

public final class PortalServlet
    extends HttpServletDispatcher
{
    private PortalApplication application;

    public void setApplication( final PortalApplication application )
    {
        this.application = application;
    }

    @Override
    public void init( final ServletConfig config )
        throws ServletException
    {
        super.init( config );
        this.application.getProviders().forEach( this::addProvider );
        this.application.getResourceProviders().forEach( this::addResourceProvider );
        this.application.getResources().forEach( this::addResource );
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        try
        {
            ServletRequestHolder.setRequest( req );
            super.service( req, res );
        }
        finally
        {
            ServletRequestHolder.setRequest( null );
        }
    }

    private void addResource( final Object resource )
    {
        getDispatcher().getRegistry().addSingletonResource( resource );
    }

    private void addResourceProvider( final ResourceProvider provider )
    {
        getDispatcher().getRegistry().addResourceFactory( new ResourceFactoryImpl( provider ) );
    }

    private void addProvider( final Object provider )
    {
        getDispatcher().getProviderFactory().register( provider );
    }
}
