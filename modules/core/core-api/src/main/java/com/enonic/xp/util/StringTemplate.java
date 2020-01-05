package com.enonic.xp.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringTemplate
{
    private final String template;

    private static final Interpolator APPLY_METHOD_INTERPOLATOR = new Interpolator( "{{", "}}", '\\' );

    private static final Interpolator CLASSIC_INTERPOLATOR = new Interpolator( "${", "}", '$' );

    public StringTemplate( final String template )
    {
        this.template = template;
    }

    /**
     * Substitute variables into placeholders formatted like {{variable}} with \ as escape symbol.
     *
     * @param model variables map
     * @return interpolated string
     */
    public String apply( final Map<String, String> model )
    {
        return APPLY_METHOD_INTERPOLATOR.interpolate( this.template, model::get );
    }

    /**
     * Substitute variables into placeholders formatted like ${variable} with $ as escape symbol.
     *
     * @param valuesResolver variables resolver
     * @return interpolated string
     */
    public String interpolate( Function<String, String> valuesResolver )
    {
        return CLASSIC_INTERPOLATOR.interpolate( template, valuesResolver );
    }

    public static StringTemplate load( final Class context, final String name )
    {
        final InputStream stream = context.getResourceAsStream( name );
        if ( stream == null )
        {
            throw new IllegalArgumentException( "Could not find resource [" + name + "]" );
        }
        try (stream)
        {
            final String value = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
            return new StringTemplate( value );
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private static class Interpolator
    {
        private final Pattern pattern;

        Interpolator( final String prefix, final String suffix, char escape )
        {
            pattern = Pattern.compile(
                "(?<escape>" + Pattern.quote( String.valueOf( escape ) ) + ")?" + "(?<placeholder>" + Pattern.quote( prefix ) +
                    "(?<variableName>[\\w.-]+)" + Pattern.quote( suffix ) + ")" );
        }

        private String interpolate( CharSequence sequence, Function<String, String> values )
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
                    final String resolvedVariable = values.apply( variableName );
                    if ( resolvedVariable != null )
                    {
                        replacement = interpolate( resolvedVariable, values );
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
}
