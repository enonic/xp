package com.enonic.xp.name;

import org.apache.commons.lang3.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.CharMatcher;

@Beta
public final class NameCharacterHelper
{
    private final static char[] ADDITIONAL_ALLOWED_CHARACTERS =
        {' ', '.', '-', '_', ':', '#', '%', '+', '^', '&', '(', ')', '<', '>', ';', '$', '\'', ','};

    private final static char[] EXIPLICITLY_ILLEGAL_CHARACTERS = {'/', '\\'};

    public static boolean isValidCharacter( final char c )
    {
        if ( isExplicitlyAllowed( c ) )
        {
            return true;
        }

        if ( isInvisible( c ) )
        {
            return false;
        }

        return Character.isJavaIdentifierPart( c );
    }

    public static boolean isInvisible( final char c )
    {
        return CharMatcher.INVISIBLE.matches( c );
    }

    private static boolean isExplicitlyAllowed( final char c )
    {
        for ( final char additional : ADDITIONAL_ALLOWED_CHARACTERS )
        {
            if ( additional == c )
            {
                return true;
            }
        }
        return false;
    }

    public static boolean hasNoExplicitIllegal( final String value )
    {
        return !StringUtils.containsAny( value, EXIPLICITLY_ILLEGAL_CHARACTERS );
    }

    static String getUnicodeString( final char c )
    {
        return "\\u" + Integer.toHexString( c | 0x10000 ).substring( 1 );
    }

    public static char[] getExplicitlyIllegalCharacters()
    {
        return EXIPLICITLY_ILLEGAL_CHARACTERS.clone();
    }
}
