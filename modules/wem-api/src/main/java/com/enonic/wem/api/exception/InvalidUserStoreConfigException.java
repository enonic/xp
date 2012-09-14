package com.enonic.wem.api.exception;

public class InvalidUserStoreConfigException
    extends BaseException
{
    public InvalidUserStoreConfigException( final String message, final Object... args )
    {
        super( message, args );
    }

    public InvalidUserStoreConfigException( final Throwable cause, final String message, final Object... args )
    {
        super( cause, message, args );
    }
}
