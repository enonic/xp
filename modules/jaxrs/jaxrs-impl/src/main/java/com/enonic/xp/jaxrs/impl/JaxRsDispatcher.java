package com.enonic.xp.jaxrs.impl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ServletBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher;

final class JaxRsDispatcher
    extends ServletContainerDispatcher
{
    private final Application app;

    JaxRsDispatcher( final ServletConfig servletConfig, final Application app )
    {
        super( servletConfig );
        this.app = app;
    }

    void init()
        throws ServletException
    {
        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( ServletContainerDispatcher.class.getClassLoader() );

        try
        {
            doInit( servletConfig.getServletContext() );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }
    }

    private void doInit( final ServletContext context )
        throws ServletException
    {
        final ServletBootstrap bootstrap = new ServletBootstrap( this.servletConfig );
        final RequestFactoryImpl requestFactory = new RequestFactoryImpl( context );
        final ResponseFactoryImpl responseFactory = new ResponseFactoryImpl();

        init( context, bootstrap, requestFactory, responseFactory );

        final SynchronousDispatcher synchronousDispatcher = (SynchronousDispatcher) this.dispatcher;
        requestFactory.setDispatcher( synchronousDispatcher );
        responseFactory.setDispatcher( synchronousDispatcher );

        processApplication( app );
    }
}
