package com.enonic.wem.api.exception;

public final class SystemException
    extends BaseException
{
    public SystemException( final String message, final Object... args )
    {
        super( message, args );
    }

    public SystemException( final Throwable cause, final String message, final Object... args )
    {
        super( cause, message, args );
    }
}
