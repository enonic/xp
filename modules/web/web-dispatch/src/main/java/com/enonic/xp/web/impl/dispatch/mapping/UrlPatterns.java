package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Builds the request path matcher of a mapping.
 * <p>
 * A url pattern is a glob: {@code *} stands for any sequence of characters, every other character stands for
 * itself, and the whole path has to match. A path matches a mapping when it matches any of its patterns.
 */
final class UrlPatterns
{
    private UrlPatterns()
    {
    }

    static Predicate<String> matcher( final Set<String> urlPatterns )
    {
        return urlPatterns.stream().map( UrlPatterns::matcher ).reduce( Predicate::or ).orElse( path -> false );
    }

    private static Predicate<String> matcher( final String urlPattern )
    {
        final int star = urlPattern.indexOf( '*' );
        if ( star < 0 )
        {
            return urlPattern::equals;
        }

        final String prefix = urlPattern.substring( 0, star );

        if ( urlPattern.indexOf( '*', star + 1 ) < 0 )
        {
            final String suffix = urlPattern.substring( star + 1 );

            if ( suffix.isEmpty() )
            {
                return path -> path.startsWith( prefix );
            }
            if ( prefix.isEmpty() )
            {
                return path -> path.endsWith( suffix );
            }
            return path -> path.length() >= prefix.length() + suffix.length() && path.startsWith( prefix ) && path.endsWith( suffix );
        }

        final String[] literals = urlPattern.split( "\\*", -1 );
        return path -> matches( path, literals );
    }

    private static boolean matches( final String path, final String[] literals )
    {
        final String prefix = literals[0];
        final String suffix = literals[literals.length - 1];

        int from = prefix.length();
        final int until = path.length() - suffix.length();

        if ( from > until || !path.startsWith( prefix ) || !path.endsWith( suffix ) )
        {
            return false;
        }

        // a * matches anything, so the leftmost occurrence of a literal is always as good a place to carry
        // on from as any later one: there is nothing to backtrack over
        for ( int i = 1; i < literals.length - 1; i++ )
        {
            final String literal = literals[i];
            final int at = path.indexOf( literal, from );
            if ( at < 0 || at + literal.length() > until )
            {
                return false;
            }
            from = at + literal.length();
        }

        return true;
    }
}
