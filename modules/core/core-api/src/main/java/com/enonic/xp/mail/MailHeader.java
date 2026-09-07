package com.enonic.xp.mail;

public final class MailHeader
{
    private final String key;

    private final String value;

    public MailHeader( final String key, final String value )
    {
        this.key = MailHeaderValue.requireSingleLine( key, "Mail header name" );
        this.value = MailHeaderValue.requireSingleLine( value, "Mail header value" );
    }

    public static MailHeader from( final String key, final String value )
    {
        return new MailHeader( key, value );
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
