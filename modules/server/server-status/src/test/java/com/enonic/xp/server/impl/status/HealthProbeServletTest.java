package com.enonic.xp.server.impl.status;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.status.health.HealthCheckResult;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HealthProbeServletTest
{
    private HealthProbeServlet servlet;

    @Mock(stubOnly = true)
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse res;

    @Mock
    private PrintWriter printWriter;

    @BeforeEach
    public void activate()
        throws Exception
    {
        when( res.getWriter() ).thenReturn( printWriter );
        this.servlet = new HealthProbeServlet();
    }


    @Test
    public void testReady()
        throws Exception
    {

        servlet.addHealthCheck( () -> HealthCheckResult.create().build() );

        this.servlet.doGet( req, res );

        verify( res ).setStatus( eq( 200 ) );
        verify( printWriter ).println( eq( "{\"message\":\"XP is healthy!\"}" ) );
    }

    @Test
    public void testNotReady()
        throws Exception
    {
        servlet.addHealthCheck( () -> HealthCheckResult.create().addErrorMessage( "something has failed" ).build() );

        this.servlet.doGet( req, res );

        verify( res ).setStatus( eq( 500 ) );
        verify( printWriter ).println( eq( "{\"message\":\"XP is not healthy: [something has failed]\"}" ) );
    }
}
