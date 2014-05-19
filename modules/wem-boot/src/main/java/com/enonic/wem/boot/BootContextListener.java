package com.enonic.wem.boot;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

public final class BootContextListener
    implements ServletContextListener
{
    private final static Logger LOG = LoggerFactory.getLogger( BootContextListener.class );

    @Inject
    protected BootStartupManager startupManager;

    private void createInjector()
    {
        LOG.info( "Creating injector for all beans." );

        final Injector injector = Guice.createInjector( Stage.PRODUCTION, new BootModule() );
        injector.injectMembers( this );
    }

    @Override
    public void contextInitialized( final ServletContextEvent event )
    {
        new BootEnvironment().initialize();
        createInjector();
        this.startupManager.start( event.getServletContext() );
    }

    @Override
    public void contextDestroyed( final ServletContextEvent event )
    {
        this.startupManager.stop();
    }
}
