package com.enonic.xp.jaxrs.impl;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.plugins.server.servlet.ServletBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher;
import org.jboss.resteasy.util.GetRestful;

import com.google.common.collect.Lists;

final class JaxRsDispatcher
    extends ServletContainerDispatcher
{
    static
    {
        Logger.setLoggerType( Logger.LoggerType.SLF4J );
    }

    private String mappingPrefix;

    protected final JaxRsApplication app;

    public JaxRsDispatcher()
    {
        this.app = new JaxRsApplication();
    }

    public void setMappingPrefix( final String value )
    {
        this.mappingPrefix = value;
    }

    public void init( final ServletContext context )
        throws Exception
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
        throws Exception
    {
        final ServletConfigImpl config = new ServletConfigImpl( "jaxrs", context );
        config.setInitParameter( ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, this.mappingPrefix );

        final ServletBootstrap bootstrap = new ServletBootstrap( config );
        final RequestFactoryImpl requestFactory = new RequestFactoryImpl( context );
        final ResponseFactoryImpl responseFactory = new ResponseFactoryImpl();

        init( context, bootstrap, requestFactory, responseFactory );

        final SynchronousDispatcher synchronousDispatcher = (SynchronousDispatcher) this.dispatcher;
        requestFactory.setDispatcher( synchronousDispatcher );
        responseFactory.setDispatcher( synchronousDispatcher );
        synchronousDispatcher.getDefaultContextObjects().put( ServletConfig.class, config );

        applyApplication();
    }

    private void applyApplication()
    {
        final List<Object> resourceList = Lists.newArrayList();
        final List<Object> providerList = Lists.newArrayList();

        for ( final Object object : this.app.getSingletons() )
        {
            if ( isRootResource( object ) )
            {
                resourceList.add( object );
            }
            else
            {
                providerList.add( object );
            }
        }

        providerList.forEach( this.providerFactory::registerProviderInstance );
        resourceList.forEach( this.dispatcher.getRegistry()::addSingletonResource );
    }

    private boolean isRootResource( final Object instance )
    {
        return GetRestful.isRootResource( instance.getClass() );
    }
}
