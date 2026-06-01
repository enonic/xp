package com.enonic.xp.impl.server.rest;

import java.util.List;
import java.util.regex.Pattern;

import com.enonic.xp.core.internal.SimpleCsvParser;

final class UrlAllowList
{
    private final List<Pattern> patterns;

    UrlAllowList( final String patternsString )
    {
        this.patterns = patternsString == null || patternsString.isBlank()
            ? List.of()
            : SimpleCsvParser.parseLine( patternsString ).stream()
                .filter( s -> !s.isEmpty() )
                .map( UrlAllowList::compile )
                .toList();
    }

    boolean matches( final String url )
    {
        for ( final Pattern pattern : patterns )
        {
            if ( pattern.matcher( url ).matches() )
            {
                return true;
            }
        }
        return false;
    }

    private static Pattern compile( final String pattern )
    {
        if ( pattern.endsWith( "*" ) )
        {
            return Pattern.compile( "^" + Pattern.quote( pattern.substring( 0, pattern.length() - 1 ) ) + ".*" );
        }
        return Pattern.compile( "^" + Pattern.quote( pattern ) + "$" );
    }
}
