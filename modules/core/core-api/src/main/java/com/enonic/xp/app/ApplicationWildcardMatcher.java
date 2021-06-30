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

    private final Function<Pattern, Predicate<String>> predicateFunction;

    private final Predicate<String> fullExpressionPredicate;

    public ApplicationWildcardMatcher( final ApplicationKey applicationKey, final Function<T, String> toNameFunction )
    {
        this( applicationKey, toNameFunction, Mode.MATCH );
    }

    public ApplicationWildcardMatcher( final ApplicationKey applicationKey, final Function<T, String> toNameFunction, final Mode mode )
    {
        this.applicationKey = applicationKey;
        this.toNameFunction = toNameFunction;

        switch ( mode )
        {
            case MATCH:
                this.predicateFunction = Pattern::asMatchPredicate;
                this.fullExpressionPredicate = s -> s.chars().anyMatch( cp -> Arrays.binarySearch( MATCH_SPECIAL_CHARACTERS, cp ) >= 0 );
                break;
            case LEGACY:
                predicateFunction = Pattern::asPredicate;
                this.fullExpressionPredicate = s -> s.startsWith( APP_WILDCARD ) || s.chars().anyMatch( cp -> cp == ':' || cp == '*' );
                break;
            default:
                throw new IllegalArgumentException( "Unknown mode " + mode );
        }
    }

    public boolean matches( final String wildcard, T input )
    {
        return createPredicate( wildcard ).test( input );
    }

    public Predicate<T> createPredicate( final String wildcard )
    {
        Pattern pattern = compilePattern( wildcard );

        return t -> predicateFunction.apply( pattern ).test( toNameFunction.apply( t ) );
    }

    private Pattern compilePattern( final String pattern )
    {
        final String resolvedApp = fullExpressionPredicate.test( pattern ) ? pattern : APP_WILDCARD + ":" + pattern;
        final String resolvedWildcard = resolvedApp.replace( APP_WILDCARD, Pattern.quote( applicationKey.getName() ) ).replace( "*", ".*" );

        return Pattern.compile( resolvedWildcard );
    }

    public enum Mode
    {
        LEGACY, MATCH;
    }
}
