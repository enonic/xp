package com.enonic.wem.servlet.internal;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ServletBootstrap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.wem.servlet.ServletRequestHolder;
import com.enonic.wem.servlet.internal.exception.ExceptionFeature;
import com.enonic.wem.servlet.jaxrs.JaxRsComponent;

@Component(immediate = true, service = Servlet.class, property = {"alias=/*"})
public final class JaxRsServlet
    extends HttpServlet
{
    private final JaxRsDispatcher dispatcher;

    private final JaxRsApplication app;

    private boolean needsRefresh;

    public JaxRsServlet()
    {
        this.dispatcher = new JaxRsDispatcher();
        this.app = new JaxRsApplication();
        this.app.addComponent( new ExceptionFeature() );
        this.needsRefresh = false;
    }

    @Override
    public void init( final ServletConfig config )
        throws ServletException
    {
        super.init( config );
        initDispatcher();
    }

    private void initDispatcher()
        throws ServletException
    {
        final ServletBootstrap bootstrap = new ServletBootstrap( getServletConfig() );
        final RequestFactoryImpl requestFactory = new RequestFactoryImpl( getServletContext() );
        final ResponseFactoryImpl responseFactory = new ResponseFactoryImpl();

        this.dispatcher.init( getServletContext(), bootstrap, requestFactory, responseFactory );

        final SynchronousDispatcher synchronousDispatcher = (SynchronousDispatcher) this.dispatcher.getDispatcher();
        requestFactory.setDispatcher( synchronousDispatcher );
        responseFactory.setDispatcher( synchronousDispatcher );
        synchronousDispatcher.getDefaultContextObjects().put( ServletConfig.class, getServletConfig() );

        this.dispatcher.addApplication( this.app );
    }

    @Override
    public void destroy()
    {
        this.dispatcher.destroy();
    }

    private void refreshIfNeeded()
        throws ServletException
    {
        if ( !this.needsRefresh )
        {
            return;
        }

        refresh();
    }

    private synchronized void refresh()
        throws ServletException
    {
        destroy();
        initDispatcher();
        this.needsRefresh = false;
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse resp )
        throws ServletException, IOException
    {
        refreshIfNeeded();

        try
        {
            ServletRequestHolder.setRequest( req );
            this.dispatcher.service( req.getMethod(), req, resp, false );
        }
        finally
        {
            ServletRequestHolder.setRequest( null );
        }
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addComponent( final JaxRsComponent resource )
    {
        this.app.addComponent( resource );
        this.needsRefresh = true;
    }

    public void removeComponent( final JaxRsComponent resource )
    {
        this.app.removeComponent( resource );
        this.needsRefresh = true;
    }
}
