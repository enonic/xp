package com.enonic.xp.launcher;

import com.enonic.xp.launcher.impl.LauncherImpl;

final class LauncherMain
{
    private final LauncherImpl launcher;

    private LauncherMain( final LauncherImpl launcher )
    {
        this.launcher = launcher;
    }

    private void launch()
    {
        System.setProperty( "java.awt.headless", "true" );
        Runtime.getRuntime().addShutdownHook( new ShutdownHook( this.launcher::stop ) );
        this.launcher.start();
    }

    static void main( final String... args )
        throws Exception
    {
        new LauncherMain( new LauncherImpl( args ) ).launch();
    }
}
