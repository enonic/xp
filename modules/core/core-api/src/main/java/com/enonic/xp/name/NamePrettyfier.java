package com.enonic.xp.name;

import java.text.Normalizer;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

@PublicApi
public final class NamePrettyfier
{
    private static final String NOT_ASCII = "[^\\p{ASCII}]";

    private static final char[] ADDITIONAL_ALLOWED_CHARS = {'.', '-', ' '};

    private static final char[] REMOVE_CHARS = {'?'};

    private static final char[] REPLACE_WITH_HYPHEN_CHARS =
        {'$', '&', '|', ':', ';', '#', '/', '\\', '<', '>', '\"', '*', '+', ',', '=', '@', '%', '{', '}', '[', ']', '`', '~', '^', '_'};

    private static final String DEFAULT_PATHNAME = "page";

    private static final Pattern STRIP_BEGINNING_PATTERN = Pattern.compile( "^([\\.|\\-|_]+)(.*)$" );

    private static final Pattern STRIP_ENDING_PATTERN = Pattern.compile( "(.*[^\\.|\\-|_])([\\.|\\-|_]+)$" );

    private static final String DEFAULT_REPLACE = "";

    private static final String DIACRITICAL = "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+";

    private static final Pattern DIACRITICS = Pattern.compile( DIACRITICAL );

    private static final Map<Character, String> NON_DIACRITICS = buildNonDiacriticsMap();

    public static String create( final String originalName )
    {
        if ( nullToEmpty( originalName ).isBlank() )
        {
            throw new IllegalArgumentException( "Generate name failed; Original name cannot be empty or blank" );
        }

        String prettifiedPathName = originalName;

        prettifiedPathName = makeLowerCase( prettifiedPathName );
        prettifiedPathName = replaceWithHyphens( prettifiedPathName );
        prettifiedPathName = replaceBlankSpaces( prettifiedPathName );
        prettifiedPathName = removeUnsafeCharacters( prettifiedPathName );
        prettifiedPathName = replaceTrailingHyphens( prettifiedPathName );
        prettifiedPathName = replaceHyphensAroundDot( prettifiedPathName );
        prettifiedPathName = ensureNiceBeginningAndEnding( prettifiedPathName );

        prettifiedPathName = transcribe( prettifiedPathName );

        if ( nullToEmpty( prettifiedPathName ).isBlank() )
        {
            return DEFAULT_PATHNAME;
        }

        return prettifiedPathName;
    }

    private static String replaceTrailingHyphens( String prettifiedName )
    {
        if ( nullToEmpty( prettifiedName ).isBlank() )
        {
            return "";
        }

        prettifiedName = prettifiedName.replaceAll( "-[-]+", "-" );

        return prettifiedName;
    }

    private static String replaceHyphensAroundDot( String prettifiedName )
    {
        if ( nullToEmpty( prettifiedName ).isBlank() )
        {
            return "";
        }

        prettifiedName = prettifiedName.replaceAll( "-?\\.-?", "." );

        return prettifiedName;
    }

    private static String ensureNiceBeginningAndEnding( String prettifiedName )
    {
        if ( nullToEmpty( prettifiedName ).isBlank() )
        {
            return "";
        }

        Matcher m = STRIP_BEGINNING_PATTERN.matcher( prettifiedName );

        if ( m.matches() )
        {
            prettifiedName = m.replaceFirst( m.group( 2 ) );
        }

        m = STRIP_ENDING_PATTERN.matcher( prettifiedName );

        if ( m.matches() )
        {
            prettifiedName = m.replaceFirst( m.group( 1 ) );
        }

        return prettifiedName;
    }

    private static String replaceWithHyphens( String prettifiedName )
    {
        if ( isNullOrEmpty( prettifiedName ) )
        {
            return "";
        }

        for ( char toBeReplaced : REPLACE_WITH_HYPHEN_CHARS )
        {
            prettifiedName = prettifiedName.replace( toBeReplaced, '-' );
        }

        return prettifiedName;
    }

    private static String makeLowerCase( String prettifiedName )
    {
        if ( isNullOrEmpty( prettifiedName ) )
        {
            return "";
        }

        prettifiedName = prettifiedName.toLowerCase();
        return prettifiedName;
    }

    private static String replaceBlankSpaces( String prettifiedName )
    {
        if ( isNullOrEmpty( prettifiedName ) )
        {
            return "";
        }

        String trimmedName = prettifiedName.trim();

        trimmedName = trimmedName.replaceAll( "\\s+", "-" );

        return trimmedName;
    }

    private static String removeUnsafeCharacters( String prettifiedName )
    {
        if ( isNullOrEmpty( prettifiedName ) )
        {
            return "";
        }

        return processChars( prettifiedName.toCharArray() );
    }

    private static String processChars( char[] chars )
    {
        final StringBuilder str = new StringBuilder();
        for ( char ch : chars )
        {
            if ( isValidChar( ch ) && !NameCharacterHelper.isInvisible( ch ) )
            {
                str.append( ch );
            }
        }

        return str.toString();
    }

    private static boolean isValidChar( char ch )
    {
        for ( char unsafe : REMOVE_CHARS )
        {
            if ( ch == unsafe )
            {
                return false;
            }
        }

        if ( Character.isJavaIdentifierPart( ch ) )
        {
            return true;
        }

        for ( char other : ADDITIONAL_ALLOWED_CHARS )
        {
            if ( ch == other )
            {
                return true;
            }
        }

        return false;
    }

    private static String transcribe( final String string )
    {
        if ( string == null )
        {
            return null;
        }

        final StringBuilder stringBuilder = new StringBuilder();

        final int length = string.length();
        final char[] characters = new char[length];
        string.getChars( 0, length, characters, 0 );

        for ( final char character : characters )
        {
            final String replace = NON_DIACRITICS.get( character );
            final String toReplace = replace == null ? String.valueOf( character ) : replace;
            stringBuilder.append( toReplace );
        }

        final String normalized = Normalizer.normalize( stringBuilder, Normalizer.Form.NFD );
        final String diacriticsCleaned = DIACRITICS.matcher( normalized ).replaceAll( DEFAULT_REPLACE );
        final String nonAsciiCleaned = diacriticsCleaned.replaceAll( NOT_ASCII, DEFAULT_REPLACE );
        return nonAsciiCleaned.toLowerCase();
    }

    private static Map<Character, String> buildNonDiacriticsMap()
    {
        final ImmutableMap.Builder<Character, String> map = ImmutableMap.builder();

        //remove crap strings with no semantics
        map.put( '\"', "" );
        map.put( '\'', "" );

        //keep relevant characters as separation
        map.put( ' ', DEFAULT_REPLACE );
        map.put( ']', DEFAULT_REPLACE );
        map.put( '[', DEFAULT_REPLACE );
        map.put( ')', DEFAULT_REPLACE );
        map.put( '(', DEFAULT_REPLACE );
        map.put( '=', DEFAULT_REPLACE );
        map.put( '!', DEFAULT_REPLACE );
        map.put( '/', DEFAULT_REPLACE );
        map.put( '\\', DEFAULT_REPLACE );
        map.put( '&', DEFAULT_REPLACE );
        map.put( ',', DEFAULT_REPLACE );
        map.put( '?', DEFAULT_REPLACE );
        map.put( '\u00b0', DEFAULT_REPLACE );
        map.put( '|', DEFAULT_REPLACE );
        map.put( '<', DEFAULT_REPLACE );
        map.put( '>', DEFAULT_REPLACE );
        map.put( ';', DEFAULT_REPLACE );
        map.put( ':', DEFAULT_REPLACE );
        map.put( '#', DEFAULT_REPLACE );
        map.put( '~', DEFAULT_REPLACE );
        map.put( '+', DEFAULT_REPLACE );
        map.put( '*', DEFAULT_REPLACE );

        //replace non-diacritics as their equivalent chars
        map.put( '\u0141', "l" );    // BiaLystock
        map.put( '\u0142', "l" );    // Bialystock
        map.put( '\u00df', "ss" );
        map.put( '\u00e6', "ae" );
        map.put( '\u00f8', "o" );
        map.put( '\u00a9', "c" );
        map.put( '\u00D0', "d" );     // all \u00d0 \u00f0 from http://de.wikipedia.org/wiki/%C3%90
        map.put( '\u00F0', "d" );
        map.put( '\u0110', "d" );
        map.put( '\u0111', "d" );
        map.put( '\u0189', "d" );
        map.put( '\u0256', "d" );
        map.put( '\u00DE', "th" );   // thorn \u00de
        map.put( '\u00FE', "th" );   // thorn \u00fe

        // cyrillic letters transliteration
        // big letters
        map.put( '\u0410', "a" ); // А
        map.put( '\u0411', "b" ); // Б
        map.put( '\u0412', "v" ); // В
        map.put( '\u0413', "g" ); // Г
        map.put( '\u0414', "d" ); // Д
        map.put( '\u0415', "e" ); // Е
        map.put( '\u0401', "jo" ); // Ё
        map.put( '\u0416', "zh" ); // Ж
        map.put( '\u0417', "z" ); // З
        map.put( '\u0418', "i" ); // И
        map.put( '\u0419', "j" ); // Й
        map.put( '\u041a', "k" ); // К
        map.put( '\u041b', "l" ); // Л
        map.put( '\u041c', "m" ); // М
        map.put( '\u041d', "n" ); // Н
        map.put( '\u041e', "o" ); // О
        map.put( '\u041f', "p" ); // П
        map.put( '\u0420', "r" ); // Р
        map.put( '\u0421', "s" ); // С
        map.put( '\u0422', "t" ); // Т
        map.put( '\u0423', "u" ); // У
        map.put( '\u0424', "f" ); // Ф
        map.put( '\u0425', "h" ); // Х
        map.put( '\u0426', "c" ); // Ц
        map.put( '\u0427', "ch" ); // Ч
        map.put( '\u0428', "sh" ); // Ш
        map.put( '\u0429', "sch" ); // Щ
        map.put( '\u042a', "" ); // Ъ
        map.put( '\u042b', "y" ); // Ы
        map.put( '\u042c', "" ); // Ь
        map.put( '\u042d', "eh" ); // Э
        map.put( '\u042e', "ju" ); // Ю
        map.put( '\u042f', "ja" ); // Я

        // small letters
        map.put( '\u0430', "a" ); // а
        map.put( '\u0431', "b" ); // б
        map.put( '\u0432', "v" ); // в
        map.put( '\u0433', "g" ); // г
        map.put( '\u0434', "d" ); // д
        map.put( '\u0435', "e" ); // е
        map.put( '\u0451', "jo" ); // ё
        map.put( '\u0436', "zh" ); // ж
        map.put( '\u0437', "z" ); // з
        map.put( '\u0438', "i" ); // и
        map.put( '\u0439', "j" ); // й
        map.put( '\u043a', "k" ); // к
        map.put( '\u043b', "l" ); // л
        map.put( '\u043c', "m" ); // м
        map.put( '\u043d', "n" ); // н
        map.put( '\u043e', "o" ); // о
        map.put( '\u043f', "p" ); // п
        map.put( '\u0440', "r" ); // р
        map.put( '\u0441', "s" ); // с
        map.put( '\u0442', "t" ); // т
        map.put( '\u0443', "u" ); // у
        map.put( '\u0444', "f" ); // ф
        map.put( '\u0445', "h" ); // х
        map.put( '\u0446', "c" ); // ц
        map.put( '\u0447', "ch" ); // ч
        map.put( '\u0448', "sh" ); // ш
        map.put( '\u0449', "sch" ); // щ
        map.put( '\u044a', "" ); // ъ
        map.put( '\u044b', "y" ); // ы
        map.put( '\u044c', "" ); // ь
        map.put( '\u044d', "eh" ); // э
        map.put( '\u044e', "ju" ); // ю
        map.put( '\u044f', "ja" ); // я

        // others
        map.put( '\u0406', "i" );  // І
        map.put( '\u0472', "fh" );  // Ѳ
        map.put( '\u0462', "je" );  // Ѣ
        map.put( '\u0474', "yh" );  // Ѵ
        map.put( '\u0490', "gj" );  // Ґ
        map.put( '\u0403', "gj" );  // Ѓ
        map.put( '\u0404', "ye" );  // Є
        map.put( '\u0407', "yi" );  // Ї
        map.put( '\u0405', "dz" );  // Ѕ
        map.put( '\u0408', "jj" );  // Ј
        map.put( '\u0409', "lj" );  // Љ
        map.put( '\u040a', "nj" );  // Њ
        map.put( '\u040c', "kj" );  // Ќ
        map.put( '\u040f', "dj" );  // Џ
        map.put( '\u040e', "uj" );  // Ў

        map.put( '\u0456', "i" );  // і
        map.put( '\u0473', "fh" );  // ѳ
        map.put( '\u0463', "je" );  // ѣ
        map.put( '\u0475', "yh" );  // ѵ
        map.put( '\u0491', "gj" );  // ґ
        map.put( '\u0453', "gj" );  // ѓ
        map.put( '\u0454', "ye" );  // є
        map.put( '\u0457', "yi" );  // ї
        map.put( '\u0455', "dz" );  // ѕ
        map.put( '\u0458', "jj" );  // ј
        map.put( '\u0459', "lj" );  // љ
        map.put( '\u045a', "nj" );  // њ
        map.put( '\u045c', "kj" );  // ќ
        map.put( '\u045f', "dj" );  // џ
        map.put( '\u045e', "uj" );   // ў

        // greek
        // big letters
        map.put( '\u03b1', "a" );  // Α
        map.put( '\u03b2', "b" );  // Β
        map.put( '\u03b3', "g" );  // Γ
        map.put( '\u03b4', "d" );  // Δ
        map.put( '\u03b5', "e" );  // Ε
        map.put( '\u03b6', "z" );  // Ζ
        map.put( '\u03b7', "e" );  // Η
        map.put( '\u03b8', "th" );  // Θ
        map.put( '\u03b9', "i" );  // Ι
        map.put( '\u03ba', "c" );  // Κ
        map.put( '\u03bb', "l" );  // Λ
        map.put( '\u03bc', "m" );  // Μ
        map.put( '\u03bd', "n" );  // Ν
        map.put( '\u03be', "x" );  // Ξ
        map.put( '\u03bf', "o" );  // Ο
        map.put( '\u03c0', "p" );  // Π
        map.put( '\u03c1', "r" );  // Ρ
        map.put( '\u03c3', "s" );  // Σ
        map.put( '\u03c4', "t" );  // Τ
        map.put( '\u03c5', "y" );  // Υ
        map.put( '\u03c6', "ph" );  // Φ
        map.put( '\u03c7', "ch" );  // Χ
        map.put( '\u03c8', "ps" );   // Ψ
        map.put( '\u03c9', "o" );  // Ω

        // small letters
        map.put( '\u0391', "a" );  // α
        map.put( '\u0392', "b" );  // β
        map.put( '\u0393', "g" );  // γ
        map.put( '\u0394', "d" );  // δ
        map.put( '\u0395', "e" );  // ε
        map.put( '\u0396', "z" );  // ζ
        map.put( '\u0397', "e" );  // η
        map.put( '\u0398', "th" );  // θ
        map.put( '\u0399', "i" );  // ι
        map.put( '\u039a', "c" );  // κ
        map.put( '\u039b', "l" );  // λ
        map.put( '\u039c', "m" );  // μ
        map.put( '\u039d', "n" );  // ν
        map.put( '\u039e', "x" );  // ξ
        map.put( '\u039f', "o" );  // ο
        map.put( '\u03a0', "p" );  // π
        map.put( '\u03a1', "r" );  // ρ
        map.put( '\u03a3', "s" );  // σ
        map.put( '\u03a4', "t" );  // τ
        map.put( '\u03a5', "y" );  // υ
        map.put( '\u03a6', "ph" );  // φ
        map.put( '\u03a7', "ch" ); // χ
        map.put( '\u03a8', "ps" ); // ψ
        map.put( '\u03a9', "o" ); // ω

        return map.build();
    }
}
