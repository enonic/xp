package com.enonic.xp.core.impl.app;

public class ApplicationInstallException
    extends RuntimeException
{
    public ApplicationInstallException( final String message )
    {
        super( message );
    }

    public ApplicationInstallException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
