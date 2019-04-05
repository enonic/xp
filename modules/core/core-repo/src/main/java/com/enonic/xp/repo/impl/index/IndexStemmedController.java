package com.enonic.xp.repo.impl.index;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class IndexStemmedController
{
    private static ImmutableMap<String, String> SUPPORTED_ANALYZERS = ImmutableMap.<String, String>builder().
        put( "ar", "arabic" ).
        put( "hy", "armenian" ).
        put( "eu", "basque" ).
        put( "bn", "bengali" ).
        put( "pt-BR", "brazilian" ).
        put( "bg", "bulgarian" ).
        put( "ca", "catalan" ).
        put( "zh", "cjk" ).
        put( "ja", "cjk" ).
        put( "ko", "cjk" ).
        put( "cs", "czech" ).
        put( "da", "danish" ).
        put( "nl", "dutch" ).
        put( "en", "english" ).
        put( "fi", "finnish" ).
        put( "fr", "french" ).
        put( "gl", "galician" ).
        put( "de", "german" ).
        put( "el", "greek" ).
        put( "hi", "hindi" ).
        put( "hu", "hungarian" ).
        put( "id", "indonesian" ).
        put( "ga", "irish" ).
        put( "it", "italian" ).
        put( "lv", "latvian" ).
        put( "lt", "lithuanian" ).
        put( "no", "norwegian" ).
        put( "nb", "norwegian" ).
        put( "nn", "language_analyzer_nn" ).
        put( "fa", "persian" ).
        put( "pt", "portuguese" ).
        put( "ro", "romanian" ).
        put( "ru", "russian" ).
        put( "ku", "sorani" ).
        put( "es", "spanish" ).
        put( "sv", "swedish" ).
        put( "tr", "turkish" ).
        put( "th", "thai" ).
        build();

    private static Map<String, StemmedIndexValueType> SUPPORTED_INDEX_VALUE_TYPES;

    static
    {
        final ImmutableMap.Builder<String, StemmedIndexValueType> indexValueTypeMap = ImmutableMap.builder();
        SUPPORTED_ANALYZERS.keySet().
            forEach( isoCode -> {
                indexValueTypeMap.put( isoCode, new StemmedIndexValueType( isoCode ) );
            } );
        SUPPORTED_INDEX_VALUE_TYPES = indexValueTypeMap.build();
    }

    public static String resolveAnalyzer( final String language )
    {
        if ( SUPPORTED_ANALYZERS != null && SUPPORTED_ANALYZERS.keySet().contains( language ) )
        {
            return SUPPORTED_ANALYZERS.get( language );
        }

        return null;
    }

    public static IndexValueTypeInterface resolveIndexValueType( final String language )
    {
        if ( SUPPORTED_INDEX_VALUE_TYPES != null && SUPPORTED_INDEX_VALUE_TYPES.keySet().contains( language ) )
        {
            return SUPPORTED_INDEX_VALUE_TYPES.get( language );
        }

        return null;
    }
}
