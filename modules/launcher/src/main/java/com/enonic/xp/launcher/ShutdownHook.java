package com.enonic.xp.launcher;

public final class ShutdownHook
    extends Thread
{
    public ShutdownHook( final Runnable runnable )
    {
        super( runnable );
    }

    public void register()
    {
        Runtime.getRuntime().addShutdownHook( this );
    }
}
