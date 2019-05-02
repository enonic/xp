package com.enonic.xp.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.exception.InitializationException;

public abstract class Initializer
{
    private static final Logger LOG = LoggerFactory.getLogger( Initializer.class );

    private static final long INITIALIZATION_CHECK_PERIOD = 1000;

    private static final long INITIALIZATION_CHECK_MAX_COUNT = 30;

    private final long initializationCheckPeriod;

    private final long initializationCheckMaxCount;

    protected Initializer( final Builder builder )
    {
        this.initializationCheckPeriod =
            builder.initializationCheckPeriod == null ? INITIALIZATION_CHECK_PERIOD : builder.initializationCheckPeriod;
        this.initializationCheckMaxCount =
            builder.initializationCheckMaxCount == null ? INITIALIZATION_CHECK_MAX_COUNT : builder.initializationCheckMaxCount;
    }

    public void initialize()
    {
        while ( true )
        {
            if ( isMaster() )
            {
                if ( !isInitialized() )
                {
                    LOG.info( "Initializing " + getInitializationSubject() );
                    doInitialize();
                    LOG.info( getInitializationSubject() + " successfully initialized" );
                }
                return;
            }
            else
            {
                for ( int i = 0; i < initializationCheckMaxCount; i++ )
                {
                    final boolean initialized = isInitialized();
                    if ( initialized )
                    {
                        return;
                    }
                    try
                    {
                        LOG.info( "Waiting [" + ( initializationCheckPeriod / 1000 ) + "s] for " + getInitializationSubject() +
                                      " to be initialized" );
                        Thread.sleep( initializationCheckPeriod );
                    }
                    catch ( InterruptedException e )
                    {
                        throw new InitializationException( getInitializationSubject() + " initialization check thread interrupted", e );
                    }
                }
            }
        }
    }

    protected abstract boolean isMaster();

    protected abstract boolean isInitialized();

    protected abstract void doInitialize();

    protected abstract String getInitializationSubject();

    public static abstract class Builder<T extends Builder>
    {
        private Long initializationCheckPeriod;

        private Long initializationCheckMaxCount;

        @SuppressWarnings("unchecked")
        public T setInitializationCheckPeriod( final Long initializationCheckPeriod )
        {
            this.initializationCheckPeriod = initializationCheckPeriod;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T setInitializationCheckMaxCount( final Long initializationCheckMaxCount )
        {
            this.initializationCheckMaxCount = initializationCheckMaxCount;
            return (T) this;
        }
    }
}
