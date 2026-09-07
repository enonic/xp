package com.enonic.xp.mail;

final class MailHeaderValue
{
    private MailHeaderValue()
    {
    }

    static String requireSingleLine( final String value, final String field )
    {
        if ( value != null && ( value.indexOf( '\r' ) >= 0 || value.indexOf( '\n' ) >= 0 ) )
        {
            throw new IllegalArgumentException( field + " must not contain line breaks" );
        }
        return value;
    }
}
