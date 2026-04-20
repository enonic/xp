package com.enonic.xp.repo.impl.index;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public class IndexLanguageController
{
    record Data(@Nullable String analyzer, @Nullable IndexValueType stemmedType, IndexValueType orderByType)
    {
    }

    public static final IndexValueType DUCET = IndexValueType.orderBy( "ducet" );

    private static final Map<String, Data> LANGUAGE_DATA = buildLanguageData();

    private static Map<String, Data> buildLanguageData()
    {
        final Map<String, Data> map = new HashMap<>();
        put( map, "en", "english", null );
        put( map, "ar", "arabic", "ar" );
        put( map, "hy", "armenian", "hy" );
        put( map, "eu", "basque", null );
        put( map, "bn", "bengali", "bn" );
        put( map, "pt", "portuguese", null );
        put( map, "pt-BR", "brazilian", null );
        put( map, "bg", "bulgarian", "bg" );
        put( map, "ca", "catalan", null );
        put( map, "zh", "cjk", "zh" );
        put( map, "ja", "cjk", "ja" );
        put( map, "ko", "cjk", "ko" );
        put( map, "cs", "czech", "cs" );
        put( map, "da", "danish", "da" );
        put( map, "nl", "dutch", null );
        put( map, "fi", "finnish", "fi" );
        put( map, "fr", "french", null );
        put( map, "gl", "galician", "gl" );
        put( map, "de", "german", null );
        put( map, "el", "greek", null );
        put( map, "hi", "hindi", "hi" );
        put( map, "hu", "hungarian", "hu" );
        put( map, "id", "indonesian", null );
        put( map, "ga", "irish", null );
        put( map, "it", "italian", null );
        put( map, "lv", "latvian", "lv" );
        put( map, "lt", "lithuanian", "lt" );
        put( map, "nb", "norwegian", "nb" );
        put( map, "nn", "language_analyzer_nn", "nn" );
        put( map, "fa", "persian", "fa" );
        put( map, "ro", "romanian", "ro" );
        put( map, "ru", "russian", "ru" );
        put( map, "ku", "sorani", null );
        put( map, "es", "spanish", "es" );
        put( map, "sv", "swedish", "sv" );
        put( map, "tr", "turkish", "tr" );
        put( map, "th", "thai", "th" );
        put( map, "af", null, "af" );
        put( map, "az", null, "az" );
        put( map, "be", null, "be" );
        put( map, "bs", null, "bs" );
        put( map, "et", null, "et" );
        put( map, "fo", null, "fo" );
        put( map, "he", null, "he" );
        put( map, "hr", null, "hr" );
        put( map, "is", null, "is" );
        put( map, "kk", null, "kk" );
        put( map, "mk", null, "mk" );
        put( map, "pl", null, "pl" );
        put( map, "sk", null, "sk" );
        put( map, "sl", null, "sl" );
        put( map, "sq", null, "sq" );
        put( map, "sr", null, "sr" );
        put( map, "uk", null, "uk" );
        put( map, "ur", null, "ur" );
        put( map, "vi", null, "vi" );
        return Map.copyOf( map );
    }

    private static void put( final Map<String, Data> map, final String key, @Nullable final String analyzer,
                             final @Nullable String orderBy )
    {
        final IndexValueType stemmedType = analyzer != null ? IndexValueType.stemmed( key.toLowerCase( Locale.ROOT ) ) : null;
        final IndexValueType orderByType = orderBy != null ? IndexValueType.orderBy( orderBy ) : DUCET;
        map.put( key, new Data( analyzer, stemmedType, orderByType ) );
    }

    private static String normalizeBase( final Locale language )
    {
        final String lang = requireNonNull( language ).getLanguage();
        if ( "no".equals( lang ) )
        {
            return "nb";
        }
        return lang;
    }

    private static String normalize( final Locale language )
    {
        final Locale locale = requireNonNull( language );
        if ( "pt".equals( locale.getLanguage() ) && "BR".equals( language.getCountry() ) )
        {
            return "pt-BR";
        }
        return normalizeBase( language );
    }

    public static String resolveAnalyzer( final @NonNull Locale language )
    {
        final Data data = LANGUAGE_DATA.get( normalize( language ) );
        return data != null ? data.analyzer() : null;
    }

    public static IndexValueType resolveStemmedIndexValueType( final @NonNull Locale language )
    {
        final Data data = LANGUAGE_DATA.get( normalize( language ) );
        return data != null ? data.stemmedType() : null;
    }

    public static IndexValueType resolveOrderByIndexValueType( final @NonNull Locale language )
    {
        final Data data = LANGUAGE_DATA.get( normalizeBase( language ) );
        return data != null ? data.orderByType() : DUCET;
    }
}
