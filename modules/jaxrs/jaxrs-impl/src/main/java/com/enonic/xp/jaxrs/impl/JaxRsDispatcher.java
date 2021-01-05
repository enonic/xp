package com.enonic.xp.jaxrs.impl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.plugins.server.servlet.ServletBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher;

final class JaxRsDispatcher
    extends ServletContainerDispatcher
{
    static
    {
        Logger.setLoggerType( Logger.LoggerType.SLF4J );
    }

    private final Application app;

    JaxRsDispatcher( final Application app )
    {
        this.app = app;
    }

    void init( final ServletContext context )
        throws ServletException
    {
        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( ServletContainerDispatcher.class.getClassLoader() );

        try
        {
            doInit( context );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }
    }

    private void doInit( final ServletContext context )
        throws ServletException
    {
        final ServletConfigImpl config = new ServletConfigImpl( "jaxrs", context );
        config.setInitParameter( ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, "/" );
        config.setInitParameter( ResteasyContextParameters.RESTEASY_ROLE_BASED_SECURITY, "true" );

        final ServletBootstrap bootstrap = new ServletBootstrap( config );
        final RequestFactoryImpl requestFactory = new RequestFactoryImpl( context );
        final ResponseFactoryImpl responseFactory = new ResponseFactoryImpl();

        init( context, bootstrap, requestFactory, responseFactory );

        final SynchronousDispatcher synchronousDispatcher = (SynchronousDispatcher) this.dispatcher;
        requestFactory.setDispatcher( synchronousDispatcher );
        responseFactory.setDispatcher( synchronousDispatcher );
        synchronousDispatcher.getDefaultContextObjects().put( ServletConfig.class, config );

        processApplication( app );
    }
}
