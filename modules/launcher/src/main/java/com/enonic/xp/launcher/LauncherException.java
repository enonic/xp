package com.enonic.xp.launcher;

public final class LauncherException
    extends RuntimeException
{
    public LauncherException( final String message, final Object... args )
    {
        super( String.format( message, args ) );
    }
}
