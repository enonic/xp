package com.enonic.xp.launcher;

import com.enonic.xp.launcher.impl.LauncherImpl;
import com.enonic.xp.launcher.ui.MainWindow;

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
        if ( this.launcher.hasArg( "ui" ) )
        {
            launchUI();
        }
        else
        {
            launchConsole();
        }
    }

    private void launchConsole()
        throws Exception
    {
        System.setProperty( "java.awt.headless", "true" );
        new ShutdownHook( this.launcher::stop ).register();
        this.launcher.start();
    }

    private void launchUI()
        throws Exception
    {
        final MainWindow window = new MainWindow( this.launcher );
        new ShutdownHook( this.launcher::stop ).register();
        window.showFrame();
    }

    public static void main( final String... args )
        throws Exception
    {
        final Launcher launcher = new LauncherImpl( args );
        new LauncherMain( launcher ).launch();
    }
}
