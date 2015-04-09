package com.enonic.xp.name;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;

@Beta
public class NameCharacterHelper
{
    private final static char[] ADDITIONAL_ALLOWED_CHARACTERS =
        {' ', '.', '-', '_', ':', '#', '%', '+', '^', '&', '(', ')', '<', '>', ';', '$', '\''};

    private final static char[] EXIPLICITLY_ILLEGAL_CHARACTERS = {'/', '\\'};

    static final String DEFAULT_REPLACE = "";

    static final String NOT_ASCII = "[^\\p{ASCII}]";

    static final String DIACRITICAL = "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+";

    static final Pattern DIACRITICS = Pattern.compile( DIACRITICAL );

    static final ImmutableMap<Character, String> NON_DIACRITICS = ImmutableMap.<Character, String>builder().
        //remove crap strings with no semantics
            put( '\"', "" ).
        put( '\'', "" ).

        //keep relevant characters as separation
            put( ' ', DEFAULT_REPLACE ).
        put( ']', DEFAULT_REPLACE ).
        put( '[', DEFAULT_REPLACE ).
        put( ')', DEFAULT_REPLACE ).
        put( '(', DEFAULT_REPLACE ).
        put( '=', DEFAULT_REPLACE ).
        put( '!', DEFAULT_REPLACE ).
        put( '/', DEFAULT_REPLACE ).
        put( '\\', DEFAULT_REPLACE ).
        put( '&', DEFAULT_REPLACE ).
        put( ',', DEFAULT_REPLACE ).
        put( '?', DEFAULT_REPLACE ).
        put( '\u00b0', DEFAULT_REPLACE ).
        put( '|', DEFAULT_REPLACE ).
        put( '<', DEFAULT_REPLACE ).
        put( '>', DEFAULT_REPLACE ).
        put( ';', DEFAULT_REPLACE ).
        put( ':', DEFAULT_REPLACE ).
        put( '#', DEFAULT_REPLACE ).
        put( '~', DEFAULT_REPLACE ).
        put( '+', DEFAULT_REPLACE ).
        put( '*', DEFAULT_REPLACE ).

        //replace non-diacritics as their equivalent chars
            put( '\u0141', "l" ).    // BiaLystock
        put( '\u0142', "l" ).    // Bialystock
        put( '\u00df', "ss" ).
        put( '\u00e6', "ae" ).
        put( '\u00f8', "o" ).
        put( '\u00a9', "c" ).
        put( '\u00D0', "d" ).     // all \u00d0 \u00f0 from http://de.wikipedia.org/wiki/%C3%90
        put( '\u00F0', "d" ).
        put( '\u0110', "d" ).
        put( '\u0111', "d" ).
        put( '\u0189', "d" ).
        put( '\u0256', "d" ).
        put( '\u00DE', "th" ).    // thorn \u00de
        put( '\u00FE', "th" ).   // thorn \u00fe

        // cyrillic letters transliteration
        // big letters
        put( '\u0410', "a" ). // А
        put( '\u0411', "b" ). // Б
        put( '\u0412', "v" ). // В
        put( '\u0413', "g" ). // Г
        put( '\u0414', "d" ). // Д
        put( '\u0415', "e" ). // Е
        put( '\u0401', "jo" ). // Ё
        put( '\u0416', "zh" ). // Ж
        put( '\u0417', "z" ). // З
        put( '\u0418', "i" ). // И
        put( '\u0419', "j" ). // Й
        put( '\u041a', "k" ). // К
        put( '\u041b', "l" ). // Л
        put( '\u041c', "m" ). // М
        put( '\u041d', "n" ). // Н
        put( '\u041e', "o" ). // О
        put( '\u041f', "p" ). // П
        put( '\u0420', "r" ). // Р
        put( '\u0421', "s" ). // С
        put( '\u0422', "t" ). // Т
        put( '\u0423', "u" ). // У
        put( '\u0424', "f" ). // Ф
        put( '\u0425', "h" ). // Х
        put( '\u0426', "c" ). // Ц
        put( '\u0427', "ch" ). // Ч
        put( '\u0428', "sh" ). // Ш
        put( '\u0429', "sch" ). // Щ
        put( '\u042a', "" ). // Ъ
        put( '\u042b', "y" ). // Ы
        put( '\u042c', "" ). // Ь
        put( '\u042d', "eh" ). // Э
        put( '\u042e', "ju" ). // Ю
        put( '\u042f', "ja" ). // Я

        // small letters
        put( '\u0430', "a" ). // а
        put( '\u0431', "b" ). // б
        put( '\u0432', "v" ). // в
        put( '\u0433', "g" ). // г
        put( '\u0434', "d" ). // д
        put( '\u0435', "e" ). // е
        put( '\u0451', "jo" ). // ё
        put( '\u0436', "zh" ). // ж
        put( '\u0437', "z" ). // з
        put( '\u0438', "i" ). // и
        put( '\u0439', "j" ). // й
        put( '\u043a', "k" ). // к
        put( '\u043b', "l" ). // л
        put( '\u043c', "m" ). // м
        put( '\u043d', "n" ). // н
        put( '\u043e', "o" ). // о
        put( '\u043f', "p" ). // п
        put( '\u0440', "r" ). // р
        put( '\u0441', "s" ). // с
        put( '\u0442', "t" ). // т
        put( '\u0443', "u" ). // у
        put( '\u0444', "f" ). // ф
        put( '\u0445', "h" ). // х
        put( '\u0446', "c" ). // ц
        put( '\u0447', "ch" ). // ч
        put( '\u0448', "sh" ). // ш
        put( '\u0449', "sch" ). // щ
        put( '\u044a', "" ). // ъ
        put( '\u044b', "y" ). // ы
        put( '\u044c', "" ). // ь
        put( '\u044d', "eh" ). // э
        put( '\u044e', "ju" ). // ю
        put( '\u044f', "ja" ). // я

        // others

        put( '\u0406', "i" ).  // І
        put( '\u0472', "fh" ).  // Ѳ
        put( '\u0462', "je" ).  // Ѣ
        put( '\u0474', "yh" ).  // Ѵ
        put( '\u0490', "gj" ).  // Ґ
        put( '\u0403', "gj" ).  // Ѓ
        put( '\u0404', "ye" ).  // Є
        put( '\u0407', "yi" ).  // Ї
        put( '\u0405', "dz" ).  // Ѕ
        put( '\u0408', "jj" ).  // Ј
        put( '\u0409', "lj" ).  // Љ
        put( '\u040a', "nj" ).  // Њ
        put( '\u040c', "kj" ).  // Ќ
        put( '\u040f', "dj" ).  // Џ
        put( '\u040e', "uj" ).  // Ў

        put( '\u0456', "i" ).  // і
        put( '\u0473', "fh" ).  // ѳ
        put( '\u0463', "je" ).  // ѣ
        put( '\u0475', "yh" ).  // ѵ
        put( '\u0491', "gj" ).  // ґ
        put( '\u0453', "gj" ).  // ѓ
        put( '\u0454', "ye" ).  // є
        put( '\u0457', "yi" ).  // ї
        put( '\u0455', "dz" ).  // ѕ
        put( '\u0458', "jj" ).  // ј
        put( '\u0459', "lj" ).  // љ
        put( '\u045a', "nj" ).  // њ
        put( '\u045c', "kj" ).  // ќ
        put( '\u045f', "dj" ).  // џ
        put( '\u045e', "uj" ).   // ў

        // greek
        // big letters
        put( '\u03b1', "a" ).  // Α
        put( '\u03b2', "b" ).  // Β
        put( '\u03b3', "g" ).  // Γ
        put( '\u03b4', "d" ).  // Δ
        put( '\u03b5', "e" ).  // Ε
        put( '\u03b6', "z" ).  // Ζ
        put( '\u03b7', "e" ).  // Η
        put( '\u03b8', "th" ).  // Θ
        put( '\u03b9', "i" ).  // Ι
        put( '\u03ba', "c" ).  // Κ
        put( '\u03bb', "l" ).  // Λ
        put( '\u03bc', "m" ).  // Μ
        put( '\u03bd', "n" ).  // Ν
        put( '\u03be', "x" ).  // Ξ
        put( '\u03bf', "o" ).  // Ο
        put( '\u03c0', "p" ).  // Π
        put( '\u03c1', "r" ).  // Ρ
        put( '\u03c3', "s" ).  // Σ
        put( '\u03c4', "t" ).  // Τ
        put( '\u03c5', "y" ).  // Υ
        put( '\u03c6', "ph" ).  // Φ
        put( '\u03c7', "ch" ).  // Χ
        put( '\u03c8', "ps" ).   // Ψ
        put( '\u03c9', "o" ).  // Ω

        // small letters
        put( '\u0391', "a" ).  // α
        put( '\u0392', "b" ).  // β
        put( '\u0393', "g" ).  // γ
        put( '\u0394', "d" ).  // δ
        put( '\u0395', "e" ).  // ε
        put( '\u0396', "z" ).  // ζ
        put( '\u0397', "e" ).  // η
        put( '\u0398', "th" ).  // θ
        put( '\u0399', "i" ).  // ι
        put( '\u039a', "c" ).  // κ
        put( '\u039b', "l" ).  // λ
        put( '\u039c', "m" ).  // μ
        put( '\u039d', "n" ).  // ν
        put( '\u039e', "x" ).  // ξ
        put( '\u039f', "o" ).  // ο
        put( '\u03a0', "p" ).  // π
        put( '\u03a1', "r" ).  // ρ
        put( '\u03a3', "s" ).  // σ
        put( '\u03a4', "t" ).  // τ
        put( '\u03a5', "y" ).  // υ
        put( '\u03a6', "ph" ).  // φ
        put( '\u03a7', "ch" ). // χ
        put( '\u03a8', "ps" ).// ψ
        put( '\u03a9', "o" ).// ω
        build();


    static boolean isValidCharacter( final char c )
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

    static boolean isInvisible( final char c )
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

    public static char[] getExplicitlyIllegalCharacters() {
        return EXIPLICITLY_ILLEGAL_CHARACTERS.clone();
    }

}
