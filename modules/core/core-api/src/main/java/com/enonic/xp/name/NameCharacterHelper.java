package com.enonic.xp.name;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
final class NameCharacterHelper
{
    private final static ImmutableSet<Character> ADDITIONAL_ALLOWED_CHARACTERS = ImmutableSet.of( ' ', '-' );

    private final static ImmutableSet<Byte> ALLOWED_UNICODE_CATEGORIES =
        ImmutableSet.of( Character.LOWERCASE_LETTER, Character.MODIFIER_LETTER, Character.UPPERCASE_LETTER, Character.DECIMAL_DIGIT_NUMBER,
                         Character.END_PUNCTUATION, Character.START_PUNCTUATION, Character.FINAL_QUOTE_PUNCTUATION,
                         Character.INITIAL_QUOTE_PUNCTUATION, Character.OTHER_PUNCTUATION, Character.CURRENCY_SYMBOL,
                         Character.MODIFIER_SYMBOL, Character.MATH_SYMBOL, Character.OTHER_SYMBOL, Character.DASH_PUNCTUATION );

    private static final String EXPLICITLY_ILLEGAL_CHARACTERS = "/\\*?|";

    private static final CharMatcher EXPLICITLY_ILLEGAL_CHAR_MATCHER = CharMatcher.anyOf( EXPLICITLY_ILLEGAL_CHARACTERS );

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
        return CharMatcher.invisible().matches( c );
    }

    private static boolean isExplicitlyAllowed( final char c )
    {
        final byte unicodeCategory = Integer.valueOf( Character.getType( c ) ).byteValue();
        return ALLOWED_UNICODE_CATEGORIES.contains( unicodeCategory ) || ADDITIONAL_ALLOWED_CHARACTERS.contains( c );
    }

    public static boolean hasNoExplicitIllegal( final String value )
    {
        return EXPLICITLY_ILLEGAL_CHAR_MATCHER.matchesNoneOf( value );
    }

    static String getUnicodeString( final char c )
    {
        return "U+" + Integer.toHexString( c | 0x10000 ).substring( 1 );
    }

    public static char[] getExplicitlyIllegalCharacters()
    {
        return EXPLICITLY_ILLEGAL_CHARACTERS.toCharArray();
    }
}
