package com.enonic.xp.ignite.impl.config;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class Log4JWrapperTest
{
    @Test
    public void levels()
    {
        final Logger unwrapped = LoggerFactory.getLogger( Log4JWrapperTest.class );
        final Log4JWrapper wrapper = new Log4JWrapper( unwrapped );

        assertEquals( unwrapped.isDebugEnabled(), wrapper.isDebugEnabled() );
        assertEquals( unwrapped.isTraceEnabled(), wrapper.isTraceEnabled() );
        assertEquals( unwrapped.isInfoEnabled(), wrapper.isInfoEnabled() );

        final boolean anythingTurnedOn =
            unwrapped.isTraceEnabled() && unwrapped.isDebugEnabled() && unwrapped.isErrorEnabled() && unwrapped.isWarnEnabled();
        assertEquals( !anythingTurnedOn, wrapper.isQuiet() );
    }

    @Test
    public void log_levels()
    {
        final Logger unwrapped = LoggerFactory.getLogger( Log4JWrapperTest.class );
        final Log4JWrapper wrapper = new Log4JWrapper( unwrapped );

        wrapper.debug( "debug" );
        wrapper.trace( "trace" );

        wrapper.warning( "warning" );
        wrapper.warning( "warning", new RuntimeException( "warningException" ) );

        wrapper.error( "error" );
        wrapper.error( "error", new RuntimeException( "errorException" ) );

        wrapper.info( "info" );
    }

    @Test
    public void getLogger()
    {
        final Logger unwrapped = LoggerFactory.getLogger( Log4JWrapperTest.class );
        final Log4JWrapper wrapper = new Log4JWrapper( unwrapped );

        assertNotNull( wrapper.getLogger( Log4JWrapperTest.class ) );
    }
}