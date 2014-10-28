package com.enonic.wem.api.security.auth;

import com.enonic.wem.api.exception.BaseException;

public class AuthenticationException
    extends BaseException
{
    public AuthenticationException( final String message )
    {
        super( message );
    }

    public AuthenticationException( final Throwable t, final String message )
    {
        super( t, message );
    }

    public AuthenticationException( final String message, final Object... args )
    {
        super( message, args );
    }

    public AuthenticationException( final Throwable cause, final String message, final Object... args )
    {
        super( cause, message, args );
    }
}
