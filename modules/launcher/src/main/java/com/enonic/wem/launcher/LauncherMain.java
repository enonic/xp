package com.enonic.wem.launcher;

public final class LauncherMain
{
    public static void main( final String... args )
        throws Exception
    {
        System.setProperty( "xp.install", "/Users/srs/development/server/xp-distro" );
        // System.setProperty( "xp.home", "/Users/srs/development/server/xp-distro/home" );

        new Launcher2().launch();
    }
}
