package com.enonic.xp.ignite.impl.config;

import org.apache.ignite.IgniteLogger;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Log4JWrapper
    implements IgniteLogger
{
    private Logger underLyingLogger;

    Log4JWrapper( final Logger underLyingLogger )
    {
        this.underLyingLogger = underLyingLogger;
    }

    @Override
    public IgniteLogger getLogger( final Object o )
    {
        String className = null;

        if ( o instanceof String )
        {
            className = (String) o;
        }
        else if ( o instanceof Class )
        {
            className = ( (Class) o ).getName();
        }

        return new Log4JWrapper( LoggerFactory.getLogger( className ) );
    }

    @Override
    public void trace( final String msg )
    {
        underLyingLogger.trace( msg );
    }

    @Override
    public void debug( final String msg )
    {
        underLyingLogger.debug( msg );
    }

    @Override
    public void info( final String msg )
    {
        underLyingLogger.info( msg );
    }

    @Override
    public void warning( final String msg )
    {
        underLyingLogger.warn( msg );
    }

    @Override
    public void warning( final String msg, @Nullable final Throwable e )
    {
        underLyingLogger.warn( msg, e );
    }

    @Override
    public void error( final String msg )
    {
        underLyingLogger.error( msg );
    }

    @Override
    public void error( final String msg, @Nullable final Throwable e )
    {
        underLyingLogger.error( msg, e );
    }

    @Override
    public boolean isTraceEnabled()
    {
        return underLyingLogger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled()
    {
        return underLyingLogger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled()
    {
        return underLyingLogger.isInfoEnabled();
    }

    @Override
    public boolean isQuiet()
    {
        return false;
    }

    @Override
    public String fileName()
    {
        return null;
    }
}
