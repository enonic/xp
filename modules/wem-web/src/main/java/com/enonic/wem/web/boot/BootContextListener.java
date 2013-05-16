package com.enonic.wem.web.boot;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;

import com.enonic.wem.core.lifecycle.LifecycleManager;
import com.enonic.wem.web.ResourceServlet;
import com.enonic.wem.web.servlet.RequestContextListener;

public final class BootContextListener
    extends GuiceServletContextListener
{
    private final static Logger LOG = LoggerFactory.getLogger( BootContextListener.class );

    private BootEnvironment env;

    private Injector injector;

    @Override
    protected Injector getInjector()
    {
        LOG.info( "Creating injector for all beans." );
        return this.injector = Guice.createInjector( new BootModule() );
    }

    @Override
    public void contextInitialized( final ServletContextEvent event )
    {
        this.env = new BootEnvironment();
        this.env.initialize();

        configure( event.getServletContext() );
        super.contextInitialized( event );
    }

    @Override
    public void contextDestroyed( final ServletContextEvent event )
    {
        this.injector.getInstance( LifecycleManager.class ).dispose();
        this.env.destroy();
        super.contextDestroyed( event );
    }

    private void configure( final ServletContext context )
    {
        context.addListener( new RequestContextListener() );

        /*
        final FilterRegistration.Dynamic allowOriginFilter = context.addFilter( "allowOrigin", new AllowOriginFilter() );
        allowOriginFilter.addMappingForUrlPatterns( EnumSet.of( DispatcherType.REQUEST ), true, "/*" );
*/
        final FilterRegistration.Dynamic guiceFilter = context.addFilter( "guice", new GuiceFilter() );
        guiceFilter.addMappingForUrlPatterns( EnumSet.of( DispatcherType.REQUEST ), true, "/*" );

        final ServletRegistration.Dynamic resourceServlet = context.addServlet( "resource", new ResourceServlet() );
        resourceServlet.setLoadOnStartup( 2 );
        resourceServlet.addMapping( "/" );
    }
}
