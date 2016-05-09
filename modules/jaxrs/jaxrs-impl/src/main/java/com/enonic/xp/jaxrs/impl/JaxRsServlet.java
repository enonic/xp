package com.enonic.xp.jaxrs.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.spi.UnhandledException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsService;

@Component(immediate = true, service = {Servlet.class, JaxRsService.class},
    property = {"osgi.http.whiteboard.servlet.pattern=/", "osgi.http.whiteboard.servlet.pattern=/admin/assets/*",
        "osgi.http.whiteboard.servlet.pattern=/admin/common/*", "osgi.http.whiteboard.servlet.pattern=/admin/rest/*"})
public final class JaxRsServlet
    extends HttpServlet
    implements JaxRsService
{
    private JaxRsDispatcher dispatcher;

    private final JaxRsApplication app;

    private boolean needsRefresh;

    public JaxRsServlet()
    {
        this.needsRefresh = true;
        this.app = new JaxRsApplication();
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
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

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addComponent( final JaxRsComponent resource )
    {
        this.app.addSingleton( resource );
        this.needsRefresh = true;
    }

    public void removeComponent( final JaxRsComponent resource )
    {
        this.app.removeSingleton( resource );
        this.needsRefresh = true;
    }

    @Override
    public List<JaxRsComponent> getComponents()
    {
        return ImmutableList.copyOf( this.app.getComponents() );
    }
}
