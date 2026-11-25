package com.enonic.xp.server;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServerInfoTest
{
    @TempDir
    public Path temporaryFolder;

    @Test
    void testInfo()
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

    private static String version( final String version, final String shortHash )
    {
        final Properties props = new Properties();
        if ( shortHash != null )
        {
            props.put( "xp.build.shortHash", shortHash );
        }
        VersionInfo.set( version );
        try
        {
            return new ServerInfo( props ).getVersion();
        }
        finally
        {
            VersionInfo.setDefault();
        }
    }

    @Test
    void aSnapshotVersionCarriesTheCommitItWasBuiltFrom()
    {
        assertEquals( "8.2.0-SNAPSHOT+b741000", version( "8.2.0-SNAPSHOT", "b741000" ) );
        assertEquals( "8.2.0-SNAPSHOT", version( "8.2.0-SNAPSHOT", "N/A" ) );
        assertEquals( "8.2.0-SNAPSHOT", version( "8.2.0-SNAPSHOT", " " ) );
        assertEquals( "8.2.0-SNAPSHOT", version( "8.2.0-SNAPSHOT", null ) );
    }

    @Test
    void aReleasedVersionStandsAlone()
    {
        assertEquals( "8.2.0", version( "8.2.0", "b741000" ) );
        assertEquals( "8.2.0-B1", version( "8.2.0-B1", "b741000" ) );
    }

    @Test
    void nameDefaults()
    {
        assertEquals( ServerInfo.DEFAULT_NAME, new ServerInfo( new Properties() ).getName() );
        assertEquals( "xp", ServerInfo.DEFAULT_NAME );
    }
}
