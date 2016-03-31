package com.enonic.xp.web.jetty.impl.configurator;

import java.io.File;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

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

    private NCSARequestLog getRequestLog()
    {
        return (NCSARequestLog) this.server.getRequestLog();
    }

    @Test
    public void testConfigure()
    {
        Mockito.when( this.config.log_enabled() ).thenReturn( true );

        configure();

        final NCSARequestLog log = getRequestLog();
        assertNotNull( log );
        assertTrue( log.getFilename().endsWith( File.separator + "jetty-yyyy_mm_dd.request.log" ) );
        assertEquals( "GMT", log.getLogTimeZone() );
        assertEquals( 31, log.getRetainDays() );
        assertEquals( true, log.isExtended() );
        assertEquals( true, log.isAppend() );
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

        final NCSARequestLog log = getRequestLog();
        assertNotNull( log );
        assertEquals( "somefile.log", log.getFilename() );
        assertEquals( "GMT+1", log.getLogTimeZone() );
        assertEquals( 60, log.getRetainDays() );
        assertEquals( false, log.isExtended() );
        assertEquals( false, log.isAppend() );
    }
}
