package com.enonic.xp.repo.impl.index;

import java.util.List;
import java.util.Locale;

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
        .put( "no", "norwegian" )
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

    private static final List<String> SORT_LANGUAGES =
        List.of( "ar", "hy", "bn", "bg", "zh", "ja", "ko", "cs", "da", "fi", "gl", "el", "hi", "hu", "id", "lv", "lt", "no", "nb", "nn",
                 "fa", "ro", "ru", "es", "sv", "tr", "th" );

    private static final ImmutableMap<String, StemmedIndexValueType> STEMMED_VALUE_TYPES;

    private static final ImmutableMap<String, OrderByIndexValueType> ORDER_BY_VALUE_TYPES;

    static
    {
        final ImmutableMap.Builder<String, StemmedIndexValueType> stemmedBuilder = ImmutableMap.builder();
        LANGUAGE_TO_ANALYZER.keySet().forEach( k -> stemmedBuilder.put( k, new StemmedIndexValueType( k.toLowerCase( Locale.ROOT ) ) ) );
        STEMMED_VALUE_TYPES = stemmedBuilder.build();

        final ImmutableMap.Builder<String, OrderByIndexValueType> orderbyBuilder = ImmutableMap.builder();
        SORT_LANGUAGES.forEach( k -> orderbyBuilder.put( k, new OrderByIndexValueType( k ) ) );
        ORDER_BY_VALUE_TYPES = orderbyBuilder.build();
    }

    private static String normalize( final Locale language )
    {
        return language == null ? null : language.toLanguageTag();
    }

    public static boolean stemmingSupported( final Locale language )
    {
        return LANGUAGE_TO_ANALYZER.containsKey( normalize( language ) );
    }

    public static String resolveAnalyzer( final Locale language )
    {
        return LANGUAGE_TO_ANALYZER.get( normalize( language ) );
    }

    public static StemmedIndexValueType resolveStemmedIndexValueType( final Locale language )
    {
        final StemmedIndexValueType type = STEMMED_VALUE_TYPES.get( normalize( language ) );
        if ( type == null )
        {
            throw new IllegalArgumentException( "Unsupported language for stemmed indexing: " + language );
        }
        return type;
    }

    public static IndexValueTypeInterface resolveOrderByIndexValueType( final Locale language )
    {
        final OrderByIndexValueType type = ORDER_BY_VALUE_TYPES.get( normalize( language ) );
        if ( type == null )
        {
            return IndexValueType.ORDERBY;
        }
        return type;
    }

    static void main()
    {
        for ( String s : SORT_LANGUAGES )
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
