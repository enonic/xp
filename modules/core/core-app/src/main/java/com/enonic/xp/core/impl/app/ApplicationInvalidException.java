package com.enonic.xp.core.impl.app;

class ApplicationInvalidException
    extends RuntimeException
{
    public ApplicationInvalidException( final String message )
    {
        super( message );
    }

    public ApplicationInvalidException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
