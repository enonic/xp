package com.enonic.xp.core.impl.dump;

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
