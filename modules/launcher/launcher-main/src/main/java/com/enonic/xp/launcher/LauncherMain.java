package com.enonic.xp.launcher;

import com.enonic.xp.launcher.impl.LauncherImpl;

public final class LauncherMain
{
    public static void main( final String... args )
        throws Exception
    {
        final Launcher launcher = new LauncherImpl( args );
        new ShutdownHook( launcher::stop ).register();
        launcher.start();
    }
}
