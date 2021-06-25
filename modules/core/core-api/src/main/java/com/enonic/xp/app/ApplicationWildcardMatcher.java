package com.enonic.xp.app;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class ApplicationWildcardMatcher<T>
{
    private static final String APP_WILDCARD = "${app}";

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
        final String resolvedAppName = resolveAppPlaceholder( wildcard, applicationKey );

        final Predicate<String> predicate =
            Pattern.compile( resolvedAppName.replace( "*", ".*" ) ).
                asMatchPredicate();

        return t -> predicate.test( toNameFunction.apply( t ) );
    }

    private static String resolveAppPlaceholder( final String nameToResolve, final ApplicationKey applicationKey )
    {
        return nameToResolve.replace( APP_WILDCARD, applicationKey.getName().replace( ".", "\\." ) );
    }
}
