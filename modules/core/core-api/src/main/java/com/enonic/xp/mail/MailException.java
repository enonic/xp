package com.enonic.xp.mail;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class MailException
    extends RuntimeException
{
    public MailException( final String message )
    {
        super( message );
    }

    public MailException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
