package com.enonic.nodb.engine.search;

import java.util.Locale;
import java.util.Map;

/**
 * The language table, server-side: language tag → (stemmer analyzer, {@code _stemmed_<lang>}
 * postfix, {@code _orderby_<loc>} postfix). A faithful port of XP's
 * {@code IndexLanguageController}, which is the authority for BOTH sides of the contract — it
 * decides the physical field names the indexer writes AND the ones a query must target. A
 * translator that disagreed with it by one row would produce a query against a field no document
 * has: zero hits, no error.
 *
 * <p><b>Why a copy rather than a shared library.</b> The alternative is exporting core-repo's
 * class through the client bundle, i.e. making NoDB depend on XP. The table is data, it is
 * stable (it enumerates Lucene's built-in language analyzers), and the divergence risk is
 * covered by {@link IndexLanguagesPortTest}, which asserts every row against the same evidence
 * the XP file carries: the analyzer names in the index template.
 *
 * <h2>Two normalizations, and they are NOT the same one</h2>
 * XP resolves the stemmer with a COUNTRY-AWARE normalization ({@code pt-BR} is a distinct row,
 * because Brazilian Portuguese has its own analyzer) and resolves the order-by locale with a
 * COUNTRY-BLIND one ({@code pt-BR} collates as {@code pt}). Both fold {@code no} onto
 * {@code nb} — Norwegian's ISO code is ambiguous and XP picks Bokmål as the base. Getting these
 * two the same way round is exactly the bug that would make {@code COLLATE pt-BR} sort by a
 * field the indexer never wrote.
 *
 * <h2>The order-by locale is not the language</h2>
 * A language with no collation entry falls back to {@code ducet} (the Unicode default), NOT to
 * its own code: {@code COLLATE de} resolves to {@code ._orderby_ducet}, not
 * {@code ._orderby_de}, because German sorts by the default table. That is what corpus row
 * {@code ICU-05-collate-de-ducet} records, and it is why an unknown locale is a fallback rather
 * than an error.
 */
public final class IndexLanguages
{
    /** DUCET — the Unicode default collation, and the order-by fallback for every unmapped language. */
    public static final String DUCET_LOCALE = CollationKeyResolver.DUCET;

    private record Language(String analyzer, String stemmedKey, String orderByLocale)
    {
    }

    /**
     * The table, keyed exactly as XP keys it: the normalized language tag. A {@code null}
     * analyzer means the language has a collation but no stemmer (so {@code stemmed()} must fail
     * for it); a {@code null} order-by locale means DUCET.
     */
    private static final Map<String, Language> LANGUAGES = Map.ofEntries( row( "en", "english", null ), row( "ar", "arabic", "ar" ),
                                                                          row( "hy", "armenian", "hy" ), row( "eu", "basque", null ),
                                                                          row( "bn", "bengali", "bn" ), row( "pt", "portuguese", null ),
                                                                          row( "pt-BR", "brazilian", null ), row( "bg", "bulgarian", "bg" ),
                                                                          row( "ca", "catalan", null ), row( "zh", "cjk", "zh" ),
                                                                          row( "ja", "cjk", "ja" ), row( "ko", "cjk", "ko" ),
                                                                          row( "cs", "czech", "cs" ), row( "da", "danish", "da" ),
                                                                          row( "nl", "dutch", null ), row( "fi", "finnish", "fi" ),
                                                                          row( "fr", "french", null ), row( "gl", "galician", "gl" ),
                                                                          row( "de", "german", null ), row( "el", "greek", null ),
                                                                          row( "hi", "hindi", "hi" ), row( "hu", "hungarian", "hu" ),
                                                                          row( "id", "indonesian", null ), row( "ga", "irish", null ),
                                                                          row( "it", "italian", null ), row( "lv", "latvian", "lv" ),
                                                                          row( "lt", "lithuanian", "lt" ), row( "nb", "norwegian", "no" ),
                                                                          row( "nn", "language_analyzer_nn", "no" ),
                                                                          row( "fa", "persian", "fa" ), row( "ro", "romanian", "ro" ),
                                                                          row( "ru", "russian", "ru" ), row( "ku", "sorani", null ),
                                                                          row( "es", "spanish", "es" ), row( "sv", "swedish", "sv" ),
                                                                          row( "tr", "turkish", "tr" ), row( "th", "thai", "th" ),
                                                                          row( "af", null, "af" ), row( "az", null, "az" ),
                                                                          row( "be", null, "be" ), row( "bs", null, "bs" ),
                                                                          row( "et", null, "et" ), row( "fo", null, "fo" ),
                                                                          row( "he", null, "he" ), row( "hr", null, "hr" ),
                                                                          row( "is", null, "is" ), row( "kk", null, "kk" ),
                                                                          row( "mk", null, "mk" ), row( "pl", null, "pl" ),
                                                                          row( "sk", null, "sk" ), row( "sl", null, "sl" ),
                                                                          row( "sq", null, "sq" ), row( "sr", null, "sr" ),
                                                                          row( "uk", null, "uk" ), row( "ur", null, "ur" ),
                                                                          row( "vi", null, "vi" ) );

    private static Map.Entry<String, Language> row( String key, String analyzer, String orderByLocale )
    {
        // XP: `stemmedType = analyzer != null ? stemmed(key.toLowerCase(ROOT)) : null` — the
        // postfix is the KEY lowercased, not the analyzer name, which is why pt-BR becomes
        // `._stemmed_pt-br` and nn becomes `._stemmed_nn` despite sharing no analyzer name.
        return Map.entry( key, new Language( analyzer, analyzer == null ? null : key.toLowerCase( Locale.ROOT ), orderByLocale ) );
    }

    private IndexLanguages()
    {
    }

    /**
     * The search-time analyzer for {@code stemmed(...)}, e.g. {@code norwegian} for {@code nb}.
     * Throws for a language with no stemmer — matching XP, which raises
     * {@code "Unsupported language for stemmed function"} rather than silently querying an
     * unanalyzed field.
     */
    public static String stemmedAnalyzer( String languageTag )
    {
        return stemmed( languageTag ).analyzer();
    }

    /** The {@code _stemmed_<lang>} postfix (without the leading dot) for {@code stemmed(...)}. */
    public static String stemmedPostfix( String languageTag )
    {
        return "_stemmed_" + stemmed( languageTag ).stemmedKey();
    }

    private static Language stemmed( String languageTag )
    {
        Language language = languageTag == null ? null : LANGUAGES.get( normalize( languageTag ) );
        if ( language == null || language.analyzer() == null )
        {
            throw new QueryDslTranslator.UnsupportedQueryException( "Unsupported language for stemmed function: " + languageTag );
        }
        return language;
    }

    /**
     * The {@code _orderby_<loc>} postfix (without the leading dot) for a {@code COLLATE} sort,
     * falling back to {@code _orderby_ducet}. Never throws: the indexer wrote a
     * {@code ._orderby_ducet} field for exactly this case, so an unmapped locale has somewhere
     * real to sort by (corpus row {@code ICU-06-collate-unknown-locale}).
     */
    public static String orderByPostfix( String languageTag )
    {
        return "_orderby_" + orderByLocale( languageTag );
    }

    /** The collation locale code a language sorts under — the {@code <loc>} the indexer used. */
    public static String orderByLocale( String languageTag )
    {
        if ( languageTag == null || languageTag.isBlank() )
        {
            return DUCET_LOCALE;
        }
        Language language = LANGUAGES.get( normalizeBase( languageTag ) );
        return language == null || language.orderByLocale() == null ? DUCET_LOCALE : language.orderByLocale();
    }

    /** Country-aware (XP's {@code normalize}): only {@code pt-BR} survives as a region-qualified key. */
    private static String normalize( String languageTag )
    {
        Locale locale = Locale.forLanguageTag( languageTag );
        if ( "pt".equals( locale.getLanguage() ) && "BR".equals( locale.getCountry() ) )
        {
            return "pt-BR";
        }
        return normalizeBase( languageTag );
    }

    /** Country-blind (XP's {@code normalizeBase}), with Norwegian folded onto Bokmål. */
    private static String normalizeBase( String languageTag )
    {
        String language = Locale.forLanguageTag( languageTag ).getLanguage();
        return "no".equals( language ) ? "nb" : language;
    }
}
