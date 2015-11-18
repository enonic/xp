package com.enonic.xp.server.impl.status;

import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.server.BuildInfo;
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
        final BuildInfo build = Mockito.mock( BuildInfo.class );
        Mockito.when( build.getBranch() ).thenReturn( "master" );
        Mockito.when( build.getHash() ).thenReturn( "12345678" );
        Mockito.when( build.getShortHash() ).thenReturn( "1234" );
        Mockito.when( build.getTimestamp() ).thenReturn( "2012-11-10T20:21:22" );

        final ServerInfo info = Mockito.mock( ServerInfo.class );
        Mockito.when( info.getName() ).thenReturn( "demo" );
        Mockito.when( info.getBuildInfo() ).thenReturn( build );

        this.reporter = new ServerReporter();
        this.reporter.setServerInfo( info );
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
