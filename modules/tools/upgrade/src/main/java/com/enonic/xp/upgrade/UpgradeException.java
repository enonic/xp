package com.enonic.xp.upgrade;

public final class UpgradeException
    extends RuntimeException
{
    public UpgradeException( final String message )
    {
        super( message );
    }

    public UpgradeException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
