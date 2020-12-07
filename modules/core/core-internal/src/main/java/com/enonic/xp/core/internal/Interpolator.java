package com.enonic.xp.core.internal;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal class to implement simple string interpolations.
 * Not intended to be used for HTML or any other markup syntax.
 */
public final class Interpolator
{
    private static final Interpolator CLASSIC_INTERPOLATOR = new Interpolator( "${", "}", '$' );

    private final Pattern pattern;

    /**
     * Creates a new Interpolator instance with specified prefix suffix and escape character.
     *
     * @param prefix the prefix for variables
     * @param suffix the suffix for variables
     * @param escape the escape character
     */
    public Interpolator( final String prefix, final String suffix, char escape )
    {
        pattern = Pattern.compile(
            "(?<escape>" + Pattern.quote( String.valueOf( escape ) ) + ")?" + "(?<placeholder>" + Pattern.quote( prefix ) +
                "(?<variableName>[\\w.-]+)" + Pattern.quote( suffix ) + ")" );
    }

    /**
     * Returns predefined interpolator
     *
     * @return interpolator with ${ as prefix, } as suffix and $ as escape character
     */
    public static Interpolator classic()
    {
        return CLASSIC_INTERPOLATOR;
    }

    /**
     * Substitutes all variables into placeholders.
     * Substitution happens recursively. If resolver returns {@code null} no further substitutions are done.
     *
     * @param variablesResolver variables resolver
     * @return interpolated string
     */
    public String interpolate( CharSequence sequence, Function<String, String> variablesResolver )
    {
        final StringBuilder builder = new StringBuilder();
        final Matcher matcher = pattern.matcher( sequence );
        while ( matcher.find() )
        {
            final String escape = matcher.group( "escape" );
            final String replacement;
            if ( escape == null )
            {
                final String variableName = matcher.group( "variableName" );
                final String resolvedVariable = variablesResolver.apply( variableName );
                if ( resolvedVariable != null )
                {
                    replacement = interpolate( resolvedVariable, variablesResolver );
                }
                else
                {
                    replacement = matcher.group();
                }
            }
            else
            {
                replacement = matcher.group( "placeholder" );
            }

            matcher.appendReplacement( builder, Matcher.quoteReplacement( replacement ) );
        }
        return matcher.appendTail( builder ).toString();
    }
}
