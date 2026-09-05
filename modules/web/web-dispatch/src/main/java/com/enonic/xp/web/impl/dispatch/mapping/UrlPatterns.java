package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Builds the request uri matcher of a mapping out of its url patterns.
 * <p>
 * A url pattern is a glob: {@code *} stands for any sequence of characters and every other character stands
 * for itself. The patterns used to be spliced into one regular expression with only {@code *} translated,
 * which left every other metacharacter live - {@code /api/v1.0/*} also matched {@code /api/v1X0/}, a pattern
 * holding {@code |} silently became two alternatives, and one holding {@code (} or {@code [} threw
 * {@link java.util.regex.PatternSyntaxException} rather than being registered at all.
 * <p>
 * Matching walks the uri instead. That makes it literal, and it keeps the regex engine off a path that runs
 * for every filter and every servlet of every request: the patterns that occur in practice - {@code /*}, a
 * prefix, an extension, an exact path - each come down to one {@link String} comparison.
 */
final class UrlPatterns
{
    private UrlPatterns()
    {
    }

    static Predicate<String> matcher( final Set<String> urlPatterns )
    {
        return urlPatterns.stream().map( UrlPatterns::matcher ).reduce( Predicate::or ).orElse( uri -> false );
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
                return uri -> uri.startsWith( prefix );
            }
            if ( prefix.isEmpty() )
            {
                return uri -> uri.endsWith( suffix );
            }
            return uri -> uri.length() >= prefix.length() + suffix.length() && uri.startsWith( prefix ) && uri.endsWith( suffix );
        }

        final String[] literals = urlPattern.split( "\\*", -1 );
        return uri -> matches( uri, literals );
    }

    private static boolean matches( final String uri, final String[] literals )
    {
        final String prefix = literals[0];
        final String suffix = literals[literals.length - 1];

        int from = prefix.length();
        final int until = uri.length() - suffix.length();

        if ( from > until || !uri.startsWith( prefix ) || !uri.endsWith( suffix ) )
        {
            return false;
        }

        // a * matches anything, so the leftmost occurrence of a literal is always as good a place to carry
        // on from as any later one and there is nothing to backtrack over
        for ( int i = 1; i < literals.length - 1; i++ )
        {
            final String literal = literals[i];
            final int at = uri.indexOf( literal, from );
            if ( at < 0 || at + literal.length() > until )
            {
                return false;
            }
            from = at + literal.length();
        }

        return true;
    }
}
