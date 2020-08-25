package com.enonic.xp.launcher;

import com.enonic.xp.launcher.impl.LauncherImpl;

public final class LauncherMain
{
    private final Launcher launcher;

    public LauncherMain( final Launcher launcher )
    {
        this.launcher = launcher;
    }

    private void launch()
        throws Exception
    {
        System.setProperty( "java.awt.headless", "true" );
        System.setProperty( "java.net.preferIPv4Stack", "true" );
        new ShutdownHook( this.launcher::stop ).register();
        this.launcher.start();
    }

    public static void main( final String... args )
        throws Exception
    {
        final Launcher launcher = new LauncherImpl( args );
        new LauncherMain( launcher ).launch();
    }
}
