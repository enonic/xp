package com.enonic.wem.core.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LifecycleBean
{
    private final LifecycleStage runLevel;

    private final Logger log;

    private boolean running;

    public LifecycleBean( final LifecycleStage runLevel )
    {
        this.runLevel = runLevel;
        this.log = LoggerFactory.getLogger( getClass() );
        this.running = false;
    }

    public final LifecycleStage getStage()
    {
        return this.runLevel;
    }

    public final boolean isRunning()
    {
        return this.running;
    }

    public final void start()
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
            throw new RuntimeException( "Error starting service [" + getClass().getName() + "]", e );
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
            this.log.warn( "Error stopping service [" + getClass().getName() + "]", e );
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
