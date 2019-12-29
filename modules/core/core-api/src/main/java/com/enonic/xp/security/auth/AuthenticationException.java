package com.enonic.xp.security.auth;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.BaseException;

@PublicApi
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
