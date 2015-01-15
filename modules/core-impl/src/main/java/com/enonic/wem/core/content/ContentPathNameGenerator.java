package com.enonic.wem.core.content;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public final class ContentPathNameGenerator
{
    private final static char[] ADDITIONAL_ALLOWED_CHARS = {'.', '-', ' '};

    private final static char[] REMOVE_CHARS = {'?'};

    private final static char[] REPLACE_WITH_HYPHEN_CHARS =
        {'$', '&', '|', ':', ';', '#', '/', '\\', '<', '>', '\"', '*', '+', ',', '=', '@', '%', '{', '}', '[', ']', '`', '~', '^', '_'};

    private final static Pattern STRIP_BEGINNING_PATTERN = Pattern.compile( "^([\\.|\\-|_]+)(.*)$" );

    private final static Pattern STRIP_ENDING_PATTERN = Pattern.compile( "(.*[^\\.|\\-|_])([\\.|\\-|_]+)$" );

    public String generatePathName( final String displayName )
    {
        if ( StringUtils.isBlank( displayName ) )
        {
            return "";
        }

        String prettifiedPathName = displayName;

        prettifiedPathName = makeLowerCase( prettifiedPathName );
        prettifiedPathName = replaceWithHyphens( prettifiedPathName );
        prettifiedPathName = removeUnsafeCharacters( prettifiedPathName );
        prettifiedPathName = replaceBlankSpaces( prettifiedPathName );
        prettifiedPathName = replaceTrailingHyphens( prettifiedPathName );
        prettifiedPathName = replaceHyphensAroundDot( prettifiedPathName );
        prettifiedPathName = ensureNiceBeginningAndEnding( prettifiedPathName );

        if ( StringUtils.isBlank( prettifiedPathName ) )
        {
            return "";
        }

        return prettifiedPathName;
    }

    private String replaceTrailingHyphens( final String prettifiedName )
    {
        if ( StringUtils.isBlank( prettifiedName ) )
        {
            return "";
        }

        return prettifiedName.replaceAll( "-[-]+", "-" );
    }

    private String replaceHyphensAroundDot( final String prettifiedName )
    {
        if ( StringUtils.isBlank( prettifiedName ) )
        {
            return "";
        }

        return prettifiedName.replaceAll( "-?\\.-?", "." );
    }

    private String ensureNiceBeginningAndEnding( String prettifiedName )
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

    private String replaceWithHyphens( String prettifiedName )
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

    private String makeLowerCase( final String prettifiedName )
    {
        if ( StringUtils.isEmpty( prettifiedName ) )
        {
            return "";
        }

        return prettifiedName.toLowerCase();
    }

    private String replaceBlankSpaces( final String prettifiedName )
    {
        if ( StringUtils.isEmpty( prettifiedName ) )
        {
            return "";
        }

        String trimmedName = prettifiedName.trim();

        return trimmedName.replaceAll( "\\s+", "-" );
    }

    private String removeUnsafeCharacters( final String prettifiedName )
    {
        if ( StringUtils.isEmpty( prettifiedName ) )
        {
            return "";
        }

        return convertName( prettifiedName );
    }

    private String convertName( String str )
    {
        return convertName( str.toCharArray() );
    }

    private String convertName( char[] chars )
    {
        final StringBuilder str = new StringBuilder();
        for ( char ch : chars )
        {
            if ( isValidChar( ch ) )
            {
                str.append( ch );
            }
        }

        return str.toString();
    }

    private boolean isValidChar( final char ch )
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
}
