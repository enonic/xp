package com.enonic.xp.launcher.impl.env;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static com.enonic.xp.launcher.impl.SharedConstants.XP_HOME_DIR;
import static com.enonic.xp.launcher.impl.SharedConstants.XP_INSTALL_DIR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnvironmentImplTest
{
    @TempDir
    public Path temporaryFolder;

    @Test
    void testGetAsMap()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl( Files.createDirectory( this.temporaryFolder.resolve( "home" ) ),
                                                         Files.createDirectory( this.temporaryFolder.resolve( "install" ) ) );

        final Map<String, String> map = env.getAsMap();
        assertEquals( env.getHomeDir().toAbsolutePath().toString(), map.get( XP_HOME_DIR ) );
        assertEquals( env.getInstallDir().toAbsolutePath().toString(), map.get( XP_INSTALL_DIR ) );
    }

    @Test
    void testValidate_noInstallDir()
    {
        assertThrows( NullPointerException.class,
                      () -> new EnvironmentImpl( null, Files.createDirectory( this.temporaryFolder.resolve( "home" ) ) ) );

    }

    @Test
    void testValidate_noHomeDir()
    {
        assertThrows( NullPointerException.class,
                      () -> new EnvironmentImpl( Files.createDirectory( this.temporaryFolder.resolve( "install" ) ), null ) );
    }

    @Test
    void testGetHomeDir()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl( Files.createDirectory( this.temporaryFolder.resolve( "install" ) ),
                                                         Files.createDirectory( this.temporaryFolder.resolve( "home" ) ) );
        assertEquals( env.getHomeDir(), env.getHomeDir() );
    }

    @Test
    void testGetInstallDir()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl( Files.createDirectory( this.temporaryFolder.resolve( "install" ) ),
                                                         Files.createDirectory( this.temporaryFolder.resolve( "home" ) ) );
        assertEquals( env.getInstallDir(), env.getInstallDir() );
    }
}
