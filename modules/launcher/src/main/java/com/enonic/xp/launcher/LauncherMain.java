package com.enonic.xp.launcher;

public final class LauncherMain
{
    public static void main( final String... args )
        throws Exception
    {
        final Launcher launcher = new Launcher( args );
        new ShutdownHook( launcher::stop ).register();
        launcher.start();
    }
}
