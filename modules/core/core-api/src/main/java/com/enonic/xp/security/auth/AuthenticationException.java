package com.enonic.xp.security.auth;

import com.enonic.xp.exception.BaseException;


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
}
