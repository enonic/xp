package com.enonic.xp.launcher;

public final class LauncherMain
{
    public static void main( final String... args )
        throws Exception
    {
        System.setProperty( "java.awt.headless", "true" );

        final Launcher launcher = new Launcher( args );
        new ShutdownHook( launcher::stop ).register();
        launcher.start();
    }
}
