package com.enonic.xp.server;

import java.io.File;
import java.util.Properties;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.jupiter.api.Assertions.*;

public class ServerInfoTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testInfo()
        throws Exception
    {
        final File homeDir = this.temporaryFolder.newFolder();
        final File installDir = this.temporaryFolder.newFolder();

        final Properties props = new Properties();
        props.put( "xp.home", homeDir.getAbsolutePath() );
        props.put( "xp.install", installDir.getAbsolutePath() );
        props.put( "xp.name", "demo" );

        final ServerInfo info = new ServerInfo( props );
        assertEquals( "demo", info.getName() );
        assertEquals( homeDir, info.getHomeDir() );
        assertEquals( installDir, info.getInstallDir() );
        assertNotNull( info.getBuildInfo() );
    }
}
