package com.enonic.xp.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.jspecify.annotations.NullMarked;

/**
 * A filter on names, configured as a comma separated list of patterns, as in the application filter
 * ({@code filter} in {@code com.enonic.xp.app.cfg}).
 * <p>
 * A pattern is a name, or a name prefix followed by {@code *}. A pattern starting with {@code !} rejects the names it
 * matches. The first matching pattern decides; a name no pattern matches is rejected. For example
 * {@code some.app.*, !some.app.name} accepts {@code some.app.other} and {@code some.app.name}, while
 * {@code !some.app.name, some.app.*} accepts {@code some.app.other} only.
 */
@NullMarked
public final class NameFilter
{
    private final List<Rule> rules;

    private NameFilter( final List<Rule> rules )
    {
        this.rules = rules;
    }

    /**
     * Parses a comma separated list of patterns. Blank entries are skipped.
     */
    public static NameFilter parse( final String patterns )
    {
        return new NameFilter(
            Arrays.stream( patterns.split( "," ) ).map( String::trim ).filter( s -> !s.isEmpty() ).map( Rule::new ).toList() );
    }

    /**
     * Returns true when the first pattern matching the name accepts it, and false when it rejects it or no pattern matches.
     */
    public boolean accept( final String name )
    {
        for ( final Rule rule : rules )
        {
            if ( rule.pattern.matcher( name ).matches() )
            {
                return rule.positive;
            }
        }
        return false;
    }

    /**
     * Returns the patterns, without the {@code !} of rejecting ones, in the order they are checked.
     */
    public List<String> getPatterns()
    {
        return rules.stream().map( rule -> rule.source ).toList();
    }

    /**
     * Returns true when the filter has no patterns, and so rejects every name.
     */
    public boolean isEmpty()
    {
        return rules.isEmpty();
    }

    private static final class Rule
    {
        final boolean positive;

        final String source;

        final Pattern pattern;

        Rule( final String rule )
        {
            this.positive = !rule.startsWith( "!" );
            this.source = positive ? rule : rule.substring( 1 );
            this.pattern = source.endsWith( "*" )
                ? Pattern.compile( Pattern.quote( source.substring( 0, source.length() - 1 ) ) + ".*" )
                : Pattern.compile( Pattern.quote( source ) );
        }
    }
}
