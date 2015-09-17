package com.enonic.xp.mail;

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
