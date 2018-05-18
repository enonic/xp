package com.enonic.xp.exception;

import com.google.common.annotations.Beta;

@Beta
public final class TimeoutException
    extends BaseException
{
    public TimeoutException( final String message, final Object... args )
    {
        super( message, args );
    }

    public TimeoutException( final Throwable cause, final String message, final Object... args )
    {
        super( cause, message, args );
    }
}
