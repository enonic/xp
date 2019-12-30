package com.enonic.xp.exception;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class InitializationException
    extends BaseException
{
    public InitializationException( final String message, final Object... args )
    {
        super( message, args );
    }

    public InitializationException( final Throwable cause, final String message, final Object... args )
    {
        super( cause, message, args );
    }
}
