package com.enonic.xp.core.name;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class NamePrettyfier
{
    private final static char[] ADDITIONAL_ALLOWED_CHARS = {'.', '-', ' '};

    private final static char[] REMOVE_CHARS = {'?'};

    private final static char[] REPLACE_WITH_HYPHEN_CHARS =
        {'$', '&', '|', ':', ';', '#', '/', '\\', '<', '>', '\"', '*', '+', ',', '=', '@', '%', '{', '}', '[', ']', '`', '~', '^', '_'};

    private final static String DEFAULT_PATHNAME = "page";

    private final static Pattern STRIP_BEGINNING_PATTERN = Pattern.compile( "^([\\.|\\-|_]+)(.*)$" );

    private final static Pattern STRIP_ENDING_PATTERN = Pattern.compile( "(.*[^\\.|\\-|_])([\\.|\\-|_]+)$" );

    public static String create( final String str )
    {
        return create( str, true );
    }

    public static String create( final String originalName, final boolean transliterate )
    {
        return doCreate( originalName, transliterate );
    }

    private static String doCreate( final String originalName, final boolean transliterate )
    {
        if ( StringUtils.isBlank( originalName ) )
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

        if ( transliterate )
        {
            prettifiedPathName = transcribe( prettifiedPathName );
        }

        if ( StringUtils.isBlank( prettifiedPathName ) )
        {
            return DEFAULT_PATHNAME;
        }

        return prettifiedPathName;
    }

    private static String replaceTrailingHyphens( String prettifiedName )
    {
        if ( StringUtils.isBlank( prettifiedName ) )
        {
            return "";
        }

        prettifiedName = prettifiedName.replaceAll( "-[-]+", "-" );

        return prettifiedName;
    }

    private static String replaceHyphensAroundDot( String prettifiedName )
    {
        if ( StringUtils.isBlank( prettifiedName ) )
        {
            return "";
        }

        prettifiedName = prettifiedName.replaceAll( "-?\\.-?", "." );

        return prettifiedName;
    }

    private static String ensureNiceBeginningAndEnding( String prettifiedName )
    {
        if ( StringUtils.isBlank( prettifiedName ) )
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
        if ( StringUtils.isEmpty( prettifiedName ) )
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
        if ( StringUtils.isEmpty( prettifiedName ) )
        {
            return "";
        }

        prettifiedName = prettifiedName.toLowerCase();
        return prettifiedName;
    }

    private static String replaceBlankSpaces( String prettifiedName )
    {
        if ( StringUtils.isEmpty( prettifiedName ) )
        {
            return "";
        }

        String trimmedName = prettifiedName.trim();

        trimmedName = trimmedName.replaceAll( "\\s+", "-" );

        return trimmedName;
    }

    private static String removeUnsafeCharacters( String prettifiedName )
    {
        if ( StringUtils.isEmpty( prettifiedName ) )
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
            final String replace = NameCharacterHelper.NON_DIACRITICS.get( character );
            final String toReplace = replace == null ? String.valueOf( character ) : replace;
            stringBuilder.append( toReplace );
        }

        final String normalized = Normalizer.normalize( stringBuilder, Normalizer.Form.NFD );
        final String diacriticsCleaned =
            NameCharacterHelper.DIACRITICS.matcher( normalized ).replaceAll( NameCharacterHelper.DEFAULT_REPLACE );
        final String nonAsciiCleaned = diacriticsCleaned.replaceAll( NameCharacterHelper.NOT_ASCII, NameCharacterHelper.DEFAULT_REPLACE );
        return nonAsciiCleaned.toLowerCase();
    }

}
