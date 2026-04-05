package com.enonic.xp.core.impl.app;

public class ApplicationBundleException
    extends RuntimeException
{
    public ApplicationBundleException( final String message )
    {
        super( message );
    }

    public ApplicationBundleException( final Throwable cause )
    {
        super( cause );
    }

    public ApplicationBundleException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
