package com.enonic.xp.core.impl.app;

class ApplicationInvalidException
    extends RuntimeException
{
    ApplicationInvalidException( final String message )
    {
        super( message );
    }

    ApplicationInvalidException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
