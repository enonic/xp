package com.enonic.xp.launcher;

public final class LauncherMain
{
    public static void main( final String... args )
        throws Exception
    {
        System.setProperty( "xp.install", "/Users/srs/development/workspace/wem/wem-master/modules/distro-v2/target/distro-v2-5.0.0-SNAPSHOT" );
        // System.setProperty( "xp.home", "/Users/srs/development/server/xp-distro/home" );
        System.setProperty( "wem.home", "/Users/srs/development/workspace/wem/wem-master/modules/distro-v2/target/distro-v2-5.0.0-SNAPSHOT/home" );

        final Launcher launcher = new Launcher();
        new ShutdownHook( launcher::stop ).register();
        launcher.start();
    }
}
