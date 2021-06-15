package com.enonic.xp.launcher;

final class ShutdownHook
    extends Thread
{
    ShutdownHook( final Runnable runnable )
    {
        super( runnable, "XP Shutdown Hook" );
    }

    public void register()
    {
        Runtime.getRuntime().addShutdownHook( this );
    }
}
