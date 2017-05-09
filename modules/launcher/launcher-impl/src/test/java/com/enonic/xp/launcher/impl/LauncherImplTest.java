package com.enonic.xp.launcher.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LauncherImplTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testLaunch()
        throws Exception
    {
        final LauncherImpl launcher = new LauncherImpl( "-Dxp.install=" + this.temporaryFolder.getRoot().toString() );
        launcher.start();
        launcher.stop();
    }
}
