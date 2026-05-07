package com.enonic.xp.app;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class ApplicationWildcardMatcher<T>
{
    private static final String APP_WILDCARD = "${app}";

    private static final int[] MATCH_SPECIAL_CHARACTERS = ":\\^$.|?*+()[{".chars().sorted().toArray();

    private final ApplicationKey applicationKey;

    private final Function<T, String> toNameFunction;

    public ApplicationWildcardMatcher( final ApplicationKey applicationKey, final Function<T, String> toNameFunction )
    {
        this.applicationKey = applicationKey;
        this.toNameFunction = toNameFunction;
    }

    public boolean matches( final String wildcard, T input )
    {
        return createPredicate( wildcard ).test( input );
    }

    public Predicate<T> createPredicate( final String wildcard )
    {
        Pattern pattern = compilePattern( wildcard );

        return t -> pattern.asMatchPredicate().test( toNameFunction.apply( t ) );
    }

    private Pattern compilePattern( final String pattern )
    {
        final String resolvedApp = isFullExpression( pattern ) ? pattern : APP_WILDCARD + ":" + pattern;
        final String resolvedWildcard = resolvedApp.replace( APP_WILDCARD, Pattern.quote( applicationKey.getName() ) ).replace( "*", ".*" );

        return Pattern.compile( resolvedWildcard );
    }

    private static boolean isFullExpression( final String pattern )
    {
        return pattern.chars().anyMatch( cp -> Arrays.binarySearch( MATCH_SPECIAL_CHARACTERS, cp ) >= 0 );
    }
}
