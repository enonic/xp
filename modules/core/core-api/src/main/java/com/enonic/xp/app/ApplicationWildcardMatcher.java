package com.enonic.xp.app;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class ApplicationWildcardMatcher<T>
{
    private static final String APP_WILDCARD = "${app}";

    private static final String ANY_WILDCARD = "*";

    private static final String APP_WILDCARD_PREFIX = APP_WILDCARD + ":";

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
        final Predicate<String> predicate = resolvedAppName.contains( ANY_WILDCARD )
            ? Pattern.compile( resolvedAppName.replace( "*", ".*" ) ).asMatchPredicate()
            : resolvedAppName::equals;

        return t -> predicate.test( toNameFunction.apply( t ) );
    }

    private static String resolveAppPlaceholder( final String nameToResolve, final ApplicationKey applicationKey )
    {
        return nameToResolve.startsWith( APP_WILDCARD_PREFIX )
            ? applicationKey + nameToResolve.substring( APP_WILDCARD.length() )
            : nameToResolve;
    }
}
