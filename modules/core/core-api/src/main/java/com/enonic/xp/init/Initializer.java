package com.enonic.xp.init;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.exception.InitializationException;

public abstract class Initializer
{
    private static final Logger LOG = LoggerFactory.getLogger( Initializer.class );

    private static final long INITIALIZATION_CHECK_PERIOD = 1000;

    private static final long INITIALIZATION_CHECK_MAX_COUNT = 900;

    private final long initializationCheckPeriod;

    private final long initializationCheckMaxCount;

    private final boolean forceInitialization;

    protected Initializer( final Builder builder )
    {
        this.initializationCheckPeriod = Objects.requireNonNullElse( builder.initializationCheckPeriod, INITIALIZATION_CHECK_PERIOD );
        this.initializationCheckMaxCount = builder.initializationCheckMaxCount != null
            ? builder.initializationCheckMaxCount
            : Long.getLong( "xp.init.maxTries", INITIALIZATION_CHECK_MAX_COUNT );
        this.forceInitialization = builder.forceInitialization;
    }

    public void initialize()
    {
        final String initializationSubject = getInitializationSubject();

        for ( int i = 0; i < initializationCheckMaxCount; i++ )
        {
            if ( readyToInitialize() )
            {
                final boolean initialized = isInitialized();
                if ( initialized )
                {
                    LOG.debug( "Already initialized {}", initializationSubject );
                    return;
                }

                if ( forceInitialization || isMaster() )
                {
                    LOG.info( "Initializing {}", initializationSubject );
                    doInitialize();
                    LOG.info( "{} successfully initialized", initializationSubject );
                    return;
                }
            }
            standBy( initializationSubject );
        }
        throw new InitializationException( "Could not initialize" );
    }

    void standBy( final String initializationSubject )
    {
        try
        {
            LOG.info( "Waiting [{}ms] for {} to be initialized", initializationCheckPeriod, initializationSubject );
            Thread.sleep( initializationCheckPeriod );
        }
        catch ( InterruptedException e )
        {
            throw new InitializationException( e, initializationSubject + " initialization check thread interrupted" );
        }
    }

    protected abstract boolean isMaster();

    protected abstract boolean isInitialized();

    protected abstract void doInitialize();

    protected abstract boolean readyToInitialize();

    protected abstract String getInitializationSubject();

    public abstract static class Builder<T extends Builder>
    {
        private Long initializationCheckPeriod;

        private Long initializationCheckMaxCount;

        private boolean forceInitialization;

        public T setInitializationCheckPeriod( final Long initializationCheckPeriod )
        {
            this.initializationCheckPeriod = initializationCheckPeriod;
            return (T) this;
        }

        public T setInitializationCheckMaxCount( final Long initializationCheckMaxCount )
        {
            this.initializationCheckMaxCount = initializationCheckMaxCount;
            return (T) this;
        }

        /**
         * Allows to call initializer from non-master cluster node.
         * IMPORTANT: do not change for any multi-cluster-node calls(app's main.js etc.)
         *
         * @param forceInitialization force initialization value.
         */
        public T forceInitialization( final boolean forceInitialization )
        {
            this.forceInitialization = forceInitialization;
            return (T) this;
        }
    }
}
