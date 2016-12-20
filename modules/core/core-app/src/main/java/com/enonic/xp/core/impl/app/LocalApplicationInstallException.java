package com.enonic.xp.core.impl.app;

public class LocalApplicationInstallException
    extends ApplicationInstallException
{
    public LocalApplicationInstallException( final String message )
    {
        super( message );
    }

    public LocalApplicationInstallException( final Throwable cause )
    {
        super( cause );
    }

    public LocalApplicationInstallException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
