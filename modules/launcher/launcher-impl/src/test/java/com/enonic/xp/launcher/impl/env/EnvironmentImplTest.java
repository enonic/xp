package com.enonic.xp.launcher.impl.env;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.launcher.LauncherException;
import com.enonic.xp.launcher.impl.SharedConstants;

import static org.junit.jupiter.api.Assertions.*;

public class EnvironmentImplTest
    implements SharedConstants
{
    @TempDir
    public Path temporaryFolder;

    @Test
    public void testGetAsMap()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.homeDir = Files.createDirectory(this.temporaryFolder.resolve( "home" ) ).toFile();
        env.installDir = Files.createDirectory(this.temporaryFolder.resolve( "install" ) ).toFile();

        final Map<String, String> map = env.getAsMap();
        assertEquals( env.homeDir.getAbsolutePath(), map.get( XP_HOME_DIR ) );
        assertEquals( env.installDir.getAbsolutePath(), map.get( XP_INSTALL_DIR ) );
    }

    @Test
    public void testValidate_noInstallDir()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        assertThrows( LauncherException.class, () -> env.validate());
    }

    @Test
    public void testValidate_noHomeDir()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.installDir = Files.createDirectory(this.temporaryFolder.resolve( "install" ) ).toFile();
        assertThrows( LauncherException.class, () -> env.validate() );
    }

    @Test
    public void testValidate()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.installDir = Files.createDirectory(this.temporaryFolder.resolve( "install" ) ).toFile();
        env.homeDir = Files.createDirectory(this.temporaryFolder.resolve( "home" ) ).toFile();
        assertThrows( LauncherException.class, () -> env.validate() );
    }

    @Test
    public void testGetHomeDir()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.homeDir = Files.createDirectory(this.temporaryFolder.resolve( "home" ) ).toFile();
        assertEquals( env.homeDir, env.getHomeDir() );
    }

    @Test
    public void testGetInstallDir()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.installDir = Files.createDirectory(this.temporaryFolder.resolve( "install" ) ).toFile();
        assertEquals( env.installDir, env.getInstallDir() );
    }
}
