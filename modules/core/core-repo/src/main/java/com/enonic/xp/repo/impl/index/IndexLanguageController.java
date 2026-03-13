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
        List.of( "ar", "hy", "bn", "bg", "ca", "zh", "ja", "ko", "cs", "da", "nl", "en", "fi", "fr", "gl", "de", "el", "hi", "hu", "id",
                 "ga", "it", "lv", "lt", "nb", "nn", "fa", "pt", "ro", "ru", "es", "sv", "tr", "th" );

    private static final ImmutableMap<String, StemmedIndexValueType> STEMMED_VALUE_TYPES;

    private static final ImmutableMap<String, OrderByIndexValueType> ORDERBY_VALUE_TYPES;

    static
    {
        final ImmutableMap.Builder<String, StemmedIndexValueType> stemmedBuilder = ImmutableMap.builder();
        LANGUAGE_TO_ANALYZER.keySet().forEach( k -> stemmedBuilder.put( k, new StemmedIndexValueType( k ) ) );
        STEMMED_VALUE_TYPES = stemmedBuilder.build();

        final ImmutableMap.Builder<String, OrderByIndexValueType> orderbyBuilder = ImmutableMap.builder();
        SORT_LANGUAGES.forEach( k -> orderbyBuilder.put( k, new OrderByIndexValueType( k ) ) );
        ORDERBY_VALUE_TYPES = orderbyBuilder.build();
    }

    private static String normalize( final Locale language )
    {
        return language == null ? null : language.toLanguageTag();
    }

    public static boolean isSupported( final Locale language )
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

    public static OrderByIndexValueType resolveOrderByIndexValueType( final Locale language )
    {
        final OrderByIndexValueType type = ORDERBY_VALUE_TYPES.get( normalize( language ) );
        if ( type == null )
        {
            throw new IllegalArgumentException( "Unsupported language for sort indexing: " + language );
        }
        return type;
    }

    static void main()
    {
        boolean[] available = new boolean[1];
        for ( String s : LANGUAGE_TO_ANALYZER.keySet() )
        {
            ULocale l = new ULocale(s);
            ULocale eql = Collator.getFunctionalEquivalent("collation", l, available);
            System.out.printf("%s = %s → %s (available: %b)%n", s, l, eql, available[0] );
        }
        ULocale l1 = new ULocale("pt-BR");
        ULocale l2 = new ULocale("en-GB");


        ULocale eq1 = Collator.getFunctionalEquivalent("collation", l1, available);
        ULocale eq2 = Collator.getFunctionalEquivalent( "collation", l2, available);

        System.out.println(eq1);  // often "en"
        System.out.println(eq2);  // often "en"

        System.out.println(eq1.equals(eq2)); // true → same collation behavior
    }
}
