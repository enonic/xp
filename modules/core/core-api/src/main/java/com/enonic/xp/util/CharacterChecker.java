package com.enonic.xp.util;

import com.enonic.xp.migration.StringUtils;

public final class CharacterChecker
{
    private final static char[] ILLEGAL_CHARACTERS = {'<', '>', '"', '\''};

    public static String check( final String value, final String errorMessage )
    {
        if ( StringUtils.containsAny( value, ILLEGAL_CHARACTERS ) )
        {
            throw new IllegalArgumentException( errorMessage );
        }

        return value;
    }
}
