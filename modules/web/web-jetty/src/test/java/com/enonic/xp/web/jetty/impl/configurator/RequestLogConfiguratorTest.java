package com.enonic.xp.web.jetty.impl.configurator;

import java.io.File;

import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.RequestLogWriter;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestLogConfiguratorTest
    extends JettyConfiguratorTest<Server>
{
    private Server server;

    @Override
    protected Server setupObject()
    {
        System.setProperty( "xp.home", "." );
        this.server = new Server();
        return this.server;
    }

    @Override
    protected JettyConfigurator<Server> newConfigurator()
    {
        return new RequestLogConfigurator();
    }

    private RequestLogWriter getRequestLog()
    {
        final CustomRequestLog requestLog = (CustomRequestLog) this.server.getRequestLog();
        if ( requestLog == null )
        {
            return null;
        }
        return (RequestLogWriter) requestLog.getWriter();
    }

    @Test
    public void testConfigure()
    {
        Mockito.when( this.config.log_enabled() ).thenReturn( true );

        configure();

        final RequestLogWriter log = getRequestLog();
        assertNotNull( log );
        assertTrue( log.getFileName().endsWith( File.separator + "jetty-yyyy_mm_dd.request.log" ) );
        assertEquals( "GMT", log.getTimeZone() );
        assertEquals( 31, log.getRetainDays() );
        assertTrue( log.isAppend() );
    }

    @Test
    public void testConfigure_disabled()
    {
        configure();
        assertNull( getRequestLog() );
    }

    @Test
    public void testConfigure_override()
    {
        Mockito.when( this.config.log_enabled() ).thenReturn( true );
        Mockito.when( this.config.log_append() ).thenReturn( false );
        Mockito.when( this.config.log_extended() ).thenReturn( false );
        Mockito.when( this.config.log_file() ).thenReturn( "somefile.log" );
        Mockito.when( this.config.log_timeZone() ).thenReturn( "GMT+1" );
        Mockito.when( this.config.log_retainDays() ).thenReturn( 60 );

        configure();

        final RequestLogWriter log = getRequestLog();
        assertNotNull( log );
        assertEquals( "somefile.log", log.getFileName() );
        assertEquals( "GMT+1", log.getTimeZone() );
        assertEquals( 60, log.getRetainDays() );
        assertEquals( false, log.isAppend() );
    }
}
