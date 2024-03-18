package com.enonic.xp.mail;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class MailHeader
{
    private final String key;

    private final String value;

    public MailHeader( final String key, final String value )
    {
        this.key = key;
        this.value = value;
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
