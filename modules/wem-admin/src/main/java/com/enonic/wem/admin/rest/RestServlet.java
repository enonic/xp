package com.enonic.wem.admin.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.enonic.wem.core.web.servlet.ServletRequestHolder;

public final class RestServlet
    extends HttpServletDispatcher
{
    private List<Object> resources;

    @Override
    public void init( final ServletConfig config )
        throws ServletException
    {
        super.init( config );
        configure();
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse resp )
        throws ServletException, IOException
    {
        try
        {
            ServletRequestHolder.setRequest( req );
            super.service( req, resp );
        }
        finally
        {
            ServletRequestHolder.setRequest( null );
        }
    }

    public void setResources( final List<Object> list )
    {
        this.resources = list;
    }

    private void configure()
    {
        this.resources.forEach( this::addResource );
    }

    private void addResource( final Object instance )
    {
        final Class<?> type = instance.getClass();
        if ( type.getAnnotation( Provider.class ) != null )
        {
            getDispatcher().getProviderFactory().register( instance );
        }
        else
        {
            getDispatcher().getRegistry().addSingletonResource( instance );
        }
    }
}
