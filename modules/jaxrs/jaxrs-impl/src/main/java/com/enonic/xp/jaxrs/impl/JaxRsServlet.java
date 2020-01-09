package com.enonic.xp.jaxrs.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.spi.UnhandledException;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.jaxrs.JaxRsComponent;

final class JaxRsServlet
    extends HttpServlet
{
    private JaxRsDispatcher dispatcher;

    private final JaxRsApplication app;

    private boolean needsRefresh;

    JaxRsServlet()
    {
        this.needsRefresh = true;
        this.app = new JaxRsApplication();
    }

    @Override
    public void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        req.setAttribute( "error.handled", true );

        try
        {
            refreshIfNeeded( req.getServletContext() );
            this.dispatcher.service( req.getMethod(), req, res, true );
        }
        catch ( final UnhandledException e )
        {
            if ( !res.isCommitted() )
            {
                throw e;
            }
        }
    }

    @Override
    public void destroy()
    {
        if ( this.dispatcher != null )
        {
            this.dispatcher.destroy();
        }
    }

    private void refreshIfNeeded( final ServletContext context )
        throws ServletException
    {
        if ( !this.needsRefresh )
        {
            return;
        }

        refresh( context );
    }

    private synchronized void refresh( final ServletContext context )
        throws ServletException
    {
        final JaxRsDispatcher newDispatcher = new JaxRsDispatcher( this.app );
        newDispatcher.init( context );

        final JaxRsDispatcher oldDispatcher = this.dispatcher;
        this.dispatcher = newDispatcher;
        this.needsRefresh = false;

        if ( oldDispatcher != null )
        {
            oldDispatcher.destroy();
        }
    }

    void addComponent( final JaxRsComponent component )
    {
        this.app.addSingleton( component );
        this.needsRefresh = true;
    }

    void removeComponent( final JaxRsComponent component )
    {
        this.app.removeSingleton( component );
        this.needsRefresh = true;
    }

    List<JaxRsComponent> getComponents()
    {
        return ImmutableList.copyOf( this.app.getComponents() );
    }
}
