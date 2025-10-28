package com.enonic.xp.launcher.impl.env;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static com.enonic.xp.launcher.impl.SharedConstants.XP_HOME_DIR;
import static com.enonic.xp.launcher.impl.SharedConstants.XP_INSTALL_DIR;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvironmentResolverTest
{
    @TempDir
    public Path temporaryFolder;

    @Test
    void testInstallDir()
        throws Exception
    {
        final Path dir = Files.createDirectories( this.temporaryFolder.resolve( "dir" ) );

        final Environment env2 = resolve( Map.of( XP_INSTALL_DIR, dir.toAbsolutePath().toString() ) );
        assertEquals( dir, env2.getInstallDir() );
        assertEquals( dir.resolve( "home" ), env2.getHomeDir() );
    }

    @Test
    void testHomeDir()
        throws Exception
    {
        final Path dir = Files.createDirectories( this.temporaryFolder.resolve( "dir" ) );
        final Path dirInstall = Files.createDirectories( this.temporaryFolder.resolve( "dirInstall" ) );

        final Environment env2 =
            resolve( Map.of( XP_INSTALL_DIR, dirInstall.toAbsolutePath().toString(), XP_HOME_DIR, dir.toAbsolutePath().toString() ) );
        assertEquals( dir, env2.getHomeDir() );
    }

    private Environment resolve( final Map<String, String> values )
    {
        final SystemProperties props = new SystemProperties();
        props.putAll( values );

        return new EnvironmentResolver( props ).resolve();
    }
}
