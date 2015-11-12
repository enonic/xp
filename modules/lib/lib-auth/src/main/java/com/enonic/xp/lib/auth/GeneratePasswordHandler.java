package com.enonic.xp.lib.auth;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public final class GeneratePasswordHandler
{
    private enum CharType
    {
        SPECIAL,
        DIGIT,
        UPPERCASE,
        LOWERCASE
    }

    private static final String SPECIAL_CHARS = "!@#$%^&*()_+{}:\"<>?|[];\',./`~";

    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";

    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String DIGIT_CHARS = "0123456789";

    private SecureRandom random = new SecureRandom();

    public String generatePassword()
    {
        int length = getRandomNumberInRange( 14, 16 ),
            maxSpecials = getRandomNumberInRange( 1, 3 ),
            specials = 0,
            maxDigits = getRandomNumberInRange( 2, 4 ),
            digits = 0,
            maxUppercase = getRandomNumberInRange( 2, 4 ),
            uppercase = 0,
            maxLowercase = length - maxSpecials - maxDigits - maxUppercase,
            lowercase = 0;

        StringBuilder result = new StringBuilder( "" );

        List<CharType> types = new ArrayList<>();
        for ( CharType charType : CharType.values() )
        {
            types.add( charType );
        }

        for ( int i = 0; i < length; i++ )
        {
            CharType type = types.get( getRandomNumberInRange( 0, types.size() - 1 ) );
            switch ( type )
            {
                case SPECIAL:
                    if ( specials < maxSpecials )
                    {
                        result.append( SPECIAL_CHARS.charAt( getRandomNumberInRange( 0, SPECIAL_CHARS.length() - 1 ) ) );
                        specials++;
                    }
                    else
                    {
                        i--;
                        types.remove( CharType.SPECIAL );
                    }
                    break;
                case DIGIT:
                    if ( digits < maxDigits )
                    {
                        result.append( DIGIT_CHARS.charAt( getRandomNumberInRange( 0, DIGIT_CHARS.length() - 1 ) ) );
                        digits++;
                    }
                    else
                    {
                        i--;
                        types.remove( CharType.DIGIT );
                    }
                    break;
                case UPPERCASE:
                    if ( uppercase < maxUppercase )
                    {
                        result.append( UPPERCASE_CHARS.charAt( getRandomNumberInRange( 0, UPPERCASE_CHARS.length() - 1 ) ) );
                        uppercase++;
                    }
                    else
                    {
                        i--;
                        types.remove( CharType.UPPERCASE );
                    }
                    break;
                case LOWERCASE:
                    if ( lowercase < maxLowercase )
                    {
                        result.append( LOWERCASE_CHARS.charAt( getRandomNumberInRange( 0, LOWERCASE_CHARS.length() - 1 ) ) );
                        lowercase++;
                    }
                    else
                    {
                        i--;
                        types.remove( CharType.LOWERCASE );
                    }
                    break;
            }
        }

        return result.toString();
    }

    private int getRandomNumberInRange( int min, int max )
    {
        return this.random.ints( min, ( max + 1 ) ).limit( 1 ).findFirst().getAsInt();
    }
}
