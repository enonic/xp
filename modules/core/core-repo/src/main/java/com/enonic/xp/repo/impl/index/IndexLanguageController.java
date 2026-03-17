package com.enonic.xp.repo.impl.index;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.ibm.icu.text.Collator;
import com.ibm.icu.util.ULocale;

public class IndexLanguageController
{
    private static final ImmutableMap<String, String> LANGUAGE_TO_ANALYZER = ImmutableMap.<String, String>builder()
        .put( "ar", "arabic" )
        .put( "hy", "armenian" )
        .put( "eu", "basque" )
        .put( "bn", "bengali" )
        .put( "pt-BR", "brazilian" )
        .put( "bg", "bulgarian" )
        .put( "ca", "catalan" )
        .put( "zh", "cjk" )
        .put( "ja", "cjk" )
        .put( "ko", "cjk" )
        .put( "cs", "czech" )
        .put( "da", "danish" )
        .put( "nl", "dutch" )
        .put( "en", "english" )
        .put( "fi", "finnish" )
        .put( "fr", "french" )
        .put( "gl", "galician" )
        .put( "de", "german" )
        .put( "el", "greek" )
        .put( "hi", "hindi" )
        .put( "hu", "hungarian" )
        .put( "id", "indonesian" )
        .put( "ga", "irish" )
        .put( "it", "italian" )
        .put( "lv", "latvian" )
        .put( "lt", "lithuanian" )
        .put( "nb", "norwegian" )
        .put( "nn", "language_analyzer_nn" )
        .put( "fa", "persian" )
        .put( "pt", "portuguese" )
        .put( "ro", "romanian" )
        .put( "ru", "russian" )
        .put( "ku", "sorani" )
        .put( "es", "spanish" )
        .put( "sv", "swedish" )
        .put( "tr", "turkish" )
        .put( "th", "thai" )
        .build();

    private static final Map<String, IndexValueType> STEMMED_VALUE_TYPES = LANGUAGE_TO_ANALYZER.keySet()
        .stream()
        .collect( Collectors.toUnmodifiableMap( k -> k, k -> IndexValueType.stemmed( k.toLowerCase( Locale.ROOT ) ) ) );

    private static final Map<String, IndexValueType> ORDER_BY_VALUE_TYPES =
        List.of( "ar", "hy", "bn", "bg", "zh", "ja", "ko", "cs", "da", "fi", "gl", "el", "hi", "hu", "lv", "lt", "nb", "nn",
                 "fa", "ro", "ru", "es", "sv", "tr", "th" )
            .stream()
            .collect( Collectors.toUnmodifiableMap( k -> k, k -> IndexValueType.orderBy( k.toLowerCase( Locale.ROOT ) ) ) );

    private static String normalizeBase( final Locale language )
    {
        final String base = Objects.requireNonNullElse( language, Locale.ROOT ).getLanguage();
        return "no".equals( base ) ? "nb" : base;
    }

    private static String normalize( final Locale language )
    {
        final Locale locale = Objects.requireNonNullElse( language, Locale.ROOT );
        if ( "pt-BR".equals( locale.toLanguageTag() ) )
        {
            return "pt-BR";
        }
        return normalizeBase( language );
    }

    public static String resolveAnalyzer( final Locale language )
    {
        return LANGUAGE_TO_ANALYZER.get( normalize( language ) );
    }

    public static IndexValueType resolveStemmedIndexValueType( final Locale language )
    {
        return STEMMED_VALUE_TYPES.get( normalize( language ) );
    }

    public static IndexValueType resolveOrderByIndexValueType( final Locale language )
    {
        final IndexValueType type = ORDER_BY_VALUE_TYPES.get( normalizeBase( language ) );
        if ( type == null )
        {
            return StaticIndexValueType.ORDERBY;
        }
        return type;
    }

    static void main()
    {
        for ( String s : LANGUAGE_TO_ANALYZER.keySet() )
        {
            boolean[] available = new boolean[1];
            ULocale l = new ULocale( s );
            ULocale eql = Collator.getFunctionalEquivalent( "collation", l, available );
            System.out.printf( "%s = %s → %s (available: %b)%n", s, l, eql, available[0] );
        }

        System.out.println();
        for ( ULocale base : Collator.getAvailableULocales() )
        {
            boolean[] available = new boolean[1];
            ULocale eql = Collator.getFunctionalEquivalent( "collation", base, available );
            System.out.printf( "%s → %s (available: %b)%n", base, eql, available[0] );
        }
    }
}
