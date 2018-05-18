package com.enonic.xp.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.exception.TimeoutException;

public abstract class Initializer
{
    private static final Logger LOG = LoggerFactory.getLogger( Initializer.class );

    private static final long INITIALIZATION_CHECK_PERIOD = 1000;

    private static final long INITIALIZATION_CHECK_MAX_COUNT = 30;

    public void initialize()
        throws InterruptedException
    {
        if ( isMaster() )
        {
            if ( !isInitialized() )
            {
                doInitialize();
            }
        }
        else
        {
            for ( int i = 0; i < INITIALIZATION_CHECK_MAX_COUNT; i++ )
            {
                final boolean initialized = isInitialized();
                LOG.info( "Is " + getInitializationSubject() + " initialized? " + initialized );
                if ( initialized )
                {
                    return;
                }
                Thread.sleep( INITIALIZATION_CHECK_PERIOD );
            }
            throw new TimeoutException( getInitializationSubject() + " not initialized by master node" );
        }
    }

    protected abstract boolean isMaster();

    protected abstract boolean isInitialized();

    protected abstract void doInitialize();

    protected abstract String getInitializationSubject();
}
