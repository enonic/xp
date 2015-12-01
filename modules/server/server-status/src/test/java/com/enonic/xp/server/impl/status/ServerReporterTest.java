package com.enonic.xp.server.impl.status;

import java.util.Properties;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;

import static org.junit.Assert.*;

public class ServerReporterTest
    extends BaseReporterTest
{
    private ServerReporter reporter;

    @Override
    protected void initialize()
    {
        VersionInfo.set( "0.0.0" );

        final Properties props = new Properties();
        props.put( "xp.build.branch", "master" );
        props.put( "xp.build.hash", "12345678" );
        props.put( "xp.build.shortHash", "1234" );
        props.put( "xp.build.timestamp", "2012-11-10T20:21:22" );
        props.put( "xp.name", "demo" );
        props.put( "xp.runMode", "prod" );

        this.reporter = new ServerReporter();
        this.reporter.serverInfo = new ServerInfo( props );
    }

    @Test
    public void testName()
    {
        assertEquals( "server", this.reporter.getName() );
    }

    @Test
    public void testReport()
    {
        final JsonNode json = this.reporter.getReport();
        final JsonNode expected = this.helper.loadTestJson( "result.json" );

        this.helper.assertJsonEquals( expected, json );
    }
}
