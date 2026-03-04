package com.enonic.xp.exception;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class ThrottlingException
    extends RuntimeException
{
    public ThrottlingException( final String message )
    {
        super( message );
    }
}
