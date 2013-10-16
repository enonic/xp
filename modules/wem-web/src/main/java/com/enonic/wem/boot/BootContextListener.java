package com.enonic.wem.boot;

import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import com.enonic.wem.core.lifecycle.LifecycleManager;
import com.enonic.wem.web.servlet.WebInitializer;

public final class BootContextListener
    implements ServletContextListener
{
    private final static Logger LOG = LoggerFactory.getLogger( BootContextListener.class );

    private BootEnvironment env;

    private Injector injector;

    private Set<WebInitializer> initializers;

    private void createInjector()
    {
        LOG.info( "Creating injector for all beans." );
        this.injector = Guice.createInjector( Stage.PRODUCTION, new BootModule() );
        this.injector.injectMembers( this );
    }

    @Override
    public void contextInitialized( final ServletContextEvent event )
    {
        this.env = new BootEnvironment();
        this.env.initialize();

        createInjector();
        configure( event.getServletContext() );
    }

    @Override
    public void contextDestroyed( final ServletContextEvent event )
    {
        this.injector.getInstance( LifecycleManager.class ).dispose();
        this.env.destroy();
    }

    private void configure( final ServletContext context )
    {
        for ( final WebInitializer initializer : this.initializers )
        {
            initializer.initialize( context );
        }
    }

    @Inject
    public void setInitializers( final Set<WebInitializer> initializers )
    {
        this.initializers = initializers;
    }
}
