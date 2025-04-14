package com.enonic.xp.server.impl.status;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.status.BaseReporterTest;

public class ServerReporterTest
    extends BaseReporterTest<ServerReporter>
{
    public ServerReporterTest()
    {
        super( "server", MediaType.JSON_UTF_8 );
    }

    @Override
    protected ServerReporter newReporter()
    {
        VersionInfo.set( "0.0.0" );

        final Properties props = new Properties();
        props.put( "xp.build.branch", "master" );
        props.put( "xp.build.hash", "12345678" );
        props.put( "xp.build.shortHash", "1234" );
        props.put( "xp.build.timestamp", "2012-11-10T20:21:22" );
        props.put( "xp.name", "demo" );
        props.put( "xp.runMode", "prod" );

        final ServerReporter reporter = new ServerReporter();
        reporter.serverInfo = new ServerInfo( props );
        return reporter;
    }

    @Test
    public void testReport()
        throws Exception
    {
        final JsonNode json = jsonReport();
        final JsonNode expected = this.helper.loadTestJson( "result.json" );

        this.helper.assertJsonEquals( expected, json );
    }
}
