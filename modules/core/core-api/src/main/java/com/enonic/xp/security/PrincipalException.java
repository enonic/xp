package com.enonic.xp.security;

import com.google.common.annotations.Beta;

@Beta
public class PrincipalException
    extends RuntimeException
{
    public PrincipalException( final String message )
    {
        super( message );
    }

    public PrincipalException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
