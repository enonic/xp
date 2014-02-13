package com.enonic.wem.boot;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;

public final class BootContextListener
    extends GuiceServletContextListener
{
    private final static Logger LOG = LoggerFactory.getLogger( BootContextListener.class );

    @Inject
    protected BootStartupManager startupManager;

    @Override
    protected Injector getInjector()
    {
        LOG.info( "Creating injector for all beans." );

        final Injector injector = Guice.createInjector( Stage.PRODUCTION, new BootModule() );
        injector.injectMembers( this );
        return injector;
    }

    @Override
    public void contextInitialized( final ServletContextEvent event )
    {
        new BootEnvironment().initialize();

        super.contextInitialized( event );
        this.startupManager.start();
    }

    @Override
    public void contextDestroyed( final ServletContextEvent event )
    {
        this.startupManager.stop();
        super.contextDestroyed( event );
    }
}
