package com.enonic.xp.core.impl.app;

public class GlobalApplicationInstallException
    extends ApplicationInstallException
{
    public GlobalApplicationInstallException( final String message )
    {
        super( message );
    }

    public GlobalApplicationInstallException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
