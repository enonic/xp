package com.enonic.xp.repo.impl.index;

import java.util.Locale;

import com.google.common.collect.ImmutableMap;

public class IndexLanguageController
{
    // Normalized to lowercase keys. Same 38 languages as search-settings.json ICU filters.
    private static final ImmutableMap<String, String> LANGUAGE_TO_ANALYZER = ImmutableMap.<String, String>builder()
        .put( "ar", "arabic" )
        .put( "hy", "armenian" )
        .put( "eu", "basque" )
        .put( "bn", "bengali" )
        .put( "pt-br", "brazilian" )
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

    private static final ImmutableMap<String, StemmedIndexValueType> STEMMED_VALUE_TYPES;

    static
    {
        final ImmutableMap.Builder<String, StemmedIndexValueType> builder = ImmutableMap.builder();
        LANGUAGE_TO_ANALYZER.keySet().forEach( k -> builder.put( k, new StemmedIndexValueType( k ) ) );
        STEMMED_VALUE_TYPES = builder.build();
    }

    private static String normalize( final String language )
    {
        return language == null ? null : language.toLowerCase( Locale.ROOT );
    }

    public static boolean isSupported( final String language )
    {
        return LANGUAGE_TO_ANALYZER.containsKey( normalize( language ) );
    }

    public static String resolveAnalyzer( final String language )
    {
        return LANGUAGE_TO_ANALYZER.get( normalize( language ) );
    }

    public static IndexValueTypeInterface resolveStemmedIndexValueType( final String language )
    {
        return STEMMED_VALUE_TYPES.get( normalize( language ) );
    }
}
