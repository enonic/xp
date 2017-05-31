package com.enonic.xp.repo.impl.dump;

public class RepoDumpException extends RuntimeException
{

    public RepoDumpException( final String message )
    {
        super( message );
    }

    public RepoDumpException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
