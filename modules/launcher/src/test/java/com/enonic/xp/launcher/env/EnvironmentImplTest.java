package com.enonic.xp.launcher.env;

import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.enonic.xp.launcher.LauncherException;
import com.enonic.xp.launcher.SharedConstants;

import static org.junit.Assert.*;

public class EnvironmentImplTest
    implements SharedConstants
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testGetAsMap()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.homeDir = this.temporaryFolder.newFolder();
        env.installDir = this.temporaryFolder.newFolder();

        final Map<String, String> map = env.getAsMap();
        assertEquals( env.homeDir.getAbsolutePath(), map.get( XP_HOME_DIR ) );
        assertEquals( env.installDir.getAbsolutePath(), map.get( XP_INSTALL_DIR ) );
    }

    @Test(expected = LauncherException.class)
    public void testValidate_noInstallDir()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.validate();
    }

    @Test(expected = LauncherException.class)
    public void testValidate_noHomeDir()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.installDir = this.temporaryFolder.newFolder();
        env.validate();
    }

    @Test
    public void testValidate()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.installDir = this.temporaryFolder.newFolder();
        env.homeDir = this.temporaryFolder.newFolder();
        env.validate();
    }

    @Test
    public void testGetHomeDir()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.homeDir = this.temporaryFolder.newFolder();
        assertEquals( env.homeDir, env.getHomeDir() );
    }

    @Test
    public void testGetInstallDir()
        throws Exception
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.installDir = this.temporaryFolder.newFolder();
        assertEquals( env.installDir, env.getInstallDir() );
    }
}
