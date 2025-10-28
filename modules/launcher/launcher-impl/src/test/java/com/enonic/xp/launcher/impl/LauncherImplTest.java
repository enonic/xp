package com.enonic.xp.launcher.impl;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LauncherImplTest
{
    @TempDir
    public Path temporaryFolder;

    @Test
    void testLaunch()
        throws Exception
    {
        final LauncherImpl launcher = new LauncherImpl( "-Dxp.install=" + this.temporaryFolder.toString() );
        launcher.start();
        launcher.stop();
    }
}
