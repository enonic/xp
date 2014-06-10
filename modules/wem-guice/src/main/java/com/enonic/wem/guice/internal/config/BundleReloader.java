package com.enonic.wem.guice.internal.config;

import java.util.Dictionary;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
final class BundleReloader
    implements ManagedService
{
    private final static Logger LOG = LoggerFactory.getLogger( BundleReloader.class );

    private final Bundle bundle;

    private boolean configured = false;

    @Inject
    public BundleReloader( final BundleContext context )
    {
        this.bundle = context.getBundle();
    }

    @Override
    public void updated( final Dictionary dictionary )
        throws ConfigurationException
    {
        if ( this.configured )
        {
            reload();
        }

        this.configured = true;
    }

    private void reload()
    {
        LOG.debug( "Configuration for [" + this.bundle.getSymbolicName() + "] changed. Reloading." );

        try
        {
            this.bundle.stop();
            this.bundle.start();
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
