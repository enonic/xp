package com.enonic.wem.core.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LifecycleBean
{
    private final RunLevel runLevel;

    private final Logger log;

    private boolean running;

    public LifecycleBean( final RunLevel runLevel )
    {
        this.runLevel = runLevel;
        this.log = LoggerFactory.getLogger( getClass() );
        this.running = false;
    }

    public final String getName()
    {
        return this.getClass().getSimpleName();
    }

    public final RunLevel getRunLevel()
    {
        return this.runLevel;
    }

    public final boolean isRunning()
    {
        return this.running;
    }

    public final void start()
        throws Exception
    {
        if ( this.running )
        {
            return;
        }

        try
        {
            doStart();
            this.running = true;
        }
        catch ( final Exception e )
        {
            this.log.error( "Error starting service [" + getName() + "]", e );
            throw e;
        }
    }

    public final void stop()
    {
        if ( !this.running )
        {
            return;
        }

        try
        {
            doStop();
        }
        catch ( final Exception e )
        {
            this.log.warn( "Error stopping service [" + getName() + "]", e );
        }
        finally
        {
            this.running = false;
        }
    }

    protected abstract void doStart()
        throws Exception;

    protected abstract void doStop()
        throws Exception;
}
