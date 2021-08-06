package com.enonic.xp.exception;

public class ThrottlingException
    extends RuntimeException
{
    public ThrottlingException( final String message )
    {
        super( message );
    }
}
