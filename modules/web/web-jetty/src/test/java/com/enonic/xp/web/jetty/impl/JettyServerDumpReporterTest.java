package com.enonic.xp.web.jetty.impl;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class JettyServerDumpReporterTest
{
    @Test
    void getName()
    {
        final Server server = mock( Server.class, withSettings().stubOnly() );

        final JettyServerDumpReporter reporter = new JettyServerDumpReporter( server );
        assertEquals( "http.serverdump", reporter.getName() );
    }

    @Test
    void getMediaType()
    {
        final Server server = mock( Server.class, withSettings().stubOnly() );

        final JettyServerDumpReporter reporter = new JettyServerDumpReporter( server );
        assertEquals( MediaType.PLAIN_TEXT_UTF_8, reporter.getMediaType() );
    }

    @Test
    void report()
        throws Exception
    {
        final Server server = mock( Server.class );

        final String testString = "test string";
        when( server.dump() ).thenReturn( testString );
        final JettyServerDumpReporter reporter = new JettyServerDumpReporter( server );
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reporter.report( outputStream );
        assertEquals( testString, outputStream.toString( StandardCharsets.UTF_8 ) );
    }
}