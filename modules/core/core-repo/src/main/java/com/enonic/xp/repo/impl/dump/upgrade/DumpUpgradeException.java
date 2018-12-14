package com.enonic.xp.repo.impl.dump.upgrade;

public class DumpUpgradeException
    extends RuntimeException
{
    public DumpUpgradeException( final String message )
    {
        super( message );
    }

    public DumpUpgradeException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
