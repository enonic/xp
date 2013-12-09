package com.enonic.wem.api.query.facet;

import java.util.regex.Pattern;

public enum RegExpFlag
{

    CASE_INSENSITIVE( Pattern.CASE_INSENSITIVE ),
    MULTILINE( Pattern.MULTILINE ),
    DOTALL( Pattern.DOTALL ),
    UNICODE_CASE( Pattern.UNICODE_CASE ),
    CANON_EQ( Pattern.CANON_EQ ),
    UNIX_LINES( Pattern.UNIX_LINES ),
    LITERAL( Pattern.LITERAL ),
    COMMENTS( Pattern.COMMENTS );

    int value;

    private RegExpFlag( final int value )
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
