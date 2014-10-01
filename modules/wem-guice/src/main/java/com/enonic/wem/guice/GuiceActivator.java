package com.enonic.wem.guice;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.guice.internal.GuiceManager;

public abstract class GuiceActivator
    extends OsgiModule
    implements BundleActivator
{
    protected final Logger logger;

    private GuiceManager manager;

    private BundleContext context;

    public GuiceActivator()
    {
        this.logger = LoggerFactory.getLogger( getClass() );
    }

    protected final BundleContext getContext()
    {
        return this.context;
    }

    @Override
    public final void start( final BundleContext context )
        throws Exception
    {
        this.context = context;
        activate();
        doStart();
    }

    @Override
    public final void stop( final BundleContext context )
        throws Exception
    {
        try
        {
            doStop();
            deactivate();
        }
        finally
        {
            this.manager = null;
        }
    }

    private void activate()
        throws Exception
    {
        this.manager = new GuiceManager().
            context( this.context ).
            module( this );

        this.manager.activate();
    }

    private void deactivate()
        throws Exception
    {
        this.manager.deactivate();
    }

    protected void doStart()
        throws Exception
    {
        // Do nothing
    }

    protected void doStop()
        throws Exception
    {
        // Do nothing
    }
}
