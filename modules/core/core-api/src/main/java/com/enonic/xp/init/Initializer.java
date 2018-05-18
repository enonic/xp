package com.enonic.xp.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.exception.InitializationException;

public abstract class Initializer
{
    private static final Logger LOG = LoggerFactory.getLogger( Initializer.class );

    private static final long INITIALIZATION_CHECK_PERIOD = 1000;

    private static final long INITIALIZATION_CHECK_MAX_COUNT = 30;

    public void initialize()
    {
        if ( isMaster() )
        {
            if ( !isInitialized() )
            {
                LOG.info( "Initializing " + getInitializationSubject() );
                doInitialize();
                LOG.info( getInitializationSubject() + " successfully initialized" );
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
                try
                {
                    Thread.sleep( INITIALIZATION_CHECK_PERIOD );
                }
                catch ( InterruptedException e )
                {
                    throw new InitializationException( getInitializationSubject() + " initialization check thread interrupted", e );
                }
            }
            throw new InitializationException( getInitializationSubject() + " not initialized by master node" );
        }
    }

    protected abstract boolean isMaster();

    protected abstract boolean isInitialized();

    protected abstract void doInitialize();

    protected abstract String getInitializationSubject();
}
