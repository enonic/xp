package com.enonic.xp.web.impl.dos;

import java.util.Collections;
import java.util.List;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FilterConfigImplTest
{
    private FilterConfigImpl wrapped;

    private ServletContext servletContext;

    @BeforeEach
    public void setup()
    {
        final FilterConfig delegate = Mockito.mock( FilterConfig.class );
        Mockito.when( delegate.getFilterName() ).thenReturn( "dos-filter" );

        this.servletContext = Mockito.mock( ServletContext.class );
        Mockito.when( delegate.getServletContext() ).thenReturn( this.servletContext );

        this.wrapped = new FilterConfigImpl( delegate );
    }

    @Test
    public void testGetters()
    {
        assertEquals( "dos-filter", this.wrapped.getFilterName() );
        assertSame( this.servletContext, this.wrapped.getServletContext() );
    }

    @Test
    public void testConfig()
    {
        final DosFilterConfig config = Mockito.mock( DosFilterConfig.class );
        Mockito.when( config.maxRequestsPerSec() ).thenReturn( 25 );
        Mockito.when( config.delayMs() ).thenReturn( 100L );
        Mockito.when( config.maxWaitMs() ).thenReturn( 50L );
        Mockito.when( config.throttledRequests() ).thenReturn( 5 );
        Mockito.when( config.throttleMs() ).thenReturn( 30000L );
        Mockito.when( config.maxRequestMs() ).thenReturn( 40000L );
        Mockito.when( config.maxIdleTrackerMs() ).thenReturn( 50000L );
        Mockito.when( config.insertHeaders() ).thenReturn( true );
        Mockito.when( config.trackSessions() ).thenReturn( true );
        Mockito.when( config.remotePort() ).thenReturn( false );
        Mockito.when( config.ipWhitelist() ).thenReturn( "a,b" );

        this.wrapped.populate( config );

        final List<String> names = Collections.list( this.wrapped.getInitParameterNames() );
        assertEquals( 11, names.size() );

        assertEquals( "25", this.wrapped.getInitParameter( "maxRequestsPerSec" ) );
        assertEquals( "100", this.wrapped.getInitParameter( "delayMs" ) );
        assertEquals( "50", this.wrapped.getInitParameter( "maxWaitMs" ) );
        assertEquals( "5", this.wrapped.getInitParameter( "throttledRequests" ) );
        assertEquals( "30000", this.wrapped.getInitParameter( "throttleMs" ) );
        assertEquals( "40000", this.wrapped.getInitParameter( "maxRequestMs" ) );
        assertEquals( "50000", this.wrapped.getInitParameter( "maxIdleTrackerMs" ) );
        assertEquals( "true", this.wrapped.getInitParameter( "insertHeaders" ) );
        assertEquals( "true", this.wrapped.getInitParameter( "trackSessions" ) );
        assertEquals( "false", this.wrapped.getInitParameter( "remotePort" ) );
        assertEquals( "a,b", this.wrapped.getInitParameter( "ipWhitelist" ) );
    }
}
