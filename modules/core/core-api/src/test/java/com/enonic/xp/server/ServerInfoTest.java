package com.enonic.xp.server;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ServerInfoTest
{
    @TempDir
    public Path temporaryFolder;

    @Test
    public void testInfo()
        throws Exception
    {
        final File homeDir = Files.createDirectory( this.temporaryFolder.resolve( "home" ) ).toFile();
        final File installDir = Files.createDirectory( this.temporaryFolder.resolve( "install" ) ).toFile();

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
