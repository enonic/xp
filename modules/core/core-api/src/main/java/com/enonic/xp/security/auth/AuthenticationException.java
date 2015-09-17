package com.enonic.xp.security.auth;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.BaseException;

@Beta
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
