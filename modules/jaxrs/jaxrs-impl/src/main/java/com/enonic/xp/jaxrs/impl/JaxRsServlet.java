package com.enonic.xp.jaxrs.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.UnhandledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.jaxrs.JaxRsComponent;

final class JaxRsServlet
    extends HttpServlet
{
    private static final Logger LOG = LoggerFactory.getLogger( JaxRsServlet.class );

    private final Set<JaxRsComponent> singletons = ConcurrentHashMap.newKeySet();

    private volatile JaxRsDispatcher dispatcher;

    private volatile boolean needsRefresh = true;

    @Override
    public void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        req.setAttribute( "error.handled", Boolean.TRUE );

        try
        {
            getDispatcher( req.getServletContext() ).service( req.getMethod(), req, res, true );
        }
        catch ( final UnhandledException e )
        {
            if ( !res.isCommitted() )
            {
                throw e;
            }
            else
            {
                LOG.warn( "UnhandledException in JaxRsServlet", e );
            }
        }
    }

    @Override
    public synchronized void destroy()
    {
        final JaxRsDispatcher dispatcher = this.dispatcher;
        if ( dispatcher != null )
        {
            dispatcher.destroy();
        }
    }

    private JaxRsDispatcher getDispatcher( final ServletContext context )
        throws ServletException
    {
        final JaxRsDispatcher dispatcher = this.dispatcher;
        if ( dispatcher == null || this.needsRefresh )
        {
            return refresh( context );
        }
        else
        {
            return dispatcher;
        }
    }

    private synchronized JaxRsDispatcher refresh( final ServletContext context )
        throws ServletException
    {
        if ( this.needsRefresh )
        {
            final ServletConfigImpl config = new ServletConfigImpl( "jaxrs", context );
            config.setInitParameter( ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, "/" );
            config.setInitParameter( ResteasyContextParameters.RESTEASY_ROLE_BASED_SECURITY, "true" );

            final JaxRsDispatcher newDispatcher = new JaxRsDispatcher( config, new JaxRsApplication( singletons ) );
            newDispatcher.init();

            final JaxRsDispatcher oldDispatcher = this.dispatcher;
            this.dispatcher = newDispatcher;

            if ( oldDispatcher != null )
            {
                oldDispatcher.destroy();
            }
            this.needsRefresh = false;
        }
        return this.dispatcher;
    }

    synchronized void addComponent( final JaxRsComponent component )
    {
        this.singletons.add( component );
        this.needsRefresh = true;
    }

    synchronized void removeComponent( final JaxRsComponent component )
    {
        this.singletons.remove( component );
        this.needsRefresh = true;
    }

    private static class JaxRsApplication
        extends Application
    {
        final Set<Object> singletons;

        JaxRsApplication( final Set<JaxRsComponent> singletons )
        {
            this.singletons = new HashSet<>( singletons );
        }

        @Override
        public Set<Object> getSingletons()
        {
            return singletons;
        }
    }

    Set<JaxRsComponent> getComponents()
    {
        return Set.copyOf( singletons );
    }
}
