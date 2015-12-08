package com.enonic.xp.util;

import org.apache.commons.lang.StringUtils;

public class StringChecker
{
    private final static char[] DEFAULT_ILLEGAL_CHARACTERS = {'<', '>', '"', '\''};

    private final char[] ILLEGAL_CHARACTERS;

    public StringChecker( final char[] ILLEGAL_CHARACTERS )
    {
        this.ILLEGAL_CHARACTERS = ILLEGAL_CHARACTERS;
    }

    public static String defaultCheck( final String value, final String errorMessage )
    {
        return doCheck( value, errorMessage, DEFAULT_ILLEGAL_CHARACTERS );
    }

    public String check( final String value, final String errorMessage )
    {
        return doCheck( value, errorMessage, ILLEGAL_CHARACTERS != null ? ILLEGAL_CHARACTERS : DEFAULT_ILLEGAL_CHARACTERS );
    }

    private static String doCheck( final String value, final String errorMessage, final char[] illegalChars )
    {
        if ( StringUtils.containsAny( value, illegalChars ) )
        {
            throw new IllegalArgumentException( errorMessage );
        }

        return value;
    }
}
