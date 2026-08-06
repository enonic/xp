package com.enonic.nodb.engine.search;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link IndexLanguages} is a hand-copied port of XP's {@code IndexLanguageController}, and a copy
 * that drifts by one row produces a query against a field no document has: zero hits, no error.
 * This test is what makes the copy safe, and it does it by CROSS-CHECKING the table against the
 * index template rather than by restating it — a test that repeated the same 56 rows would only
 * prove the file compiles.
 *
 * <p>The three checks that would actually catch drift:
 * <ol>
 * <li>every stemmer analyzer the table names must EXIST in the template (as a built-in Lucene
 * analyzer or an authored one) — a typo'd analyzer name is an OpenSearch error at query time;</li>
 * <li>every {@code ._stemmed_<lang>} postfix the table produces must have a dynamic template — a
 * missing one means the field was indexed as something else entirely;</li>
 * <li>every order-by locale must be in the template's own recorded locale list, which is the same
 * list the indexer's collation keys were computed for.</li>
 * </ol>
 */
class IndexLanguagesPortTest
{
    /**
     * Lucene's built-in language analyzers, which the template references by name without
     * declaring. Everything else must be declared in {@code settings.analysis.analyzer}.
     */
    private static final Set<String> BUILT_IN_ANALYZERS =
        Set.of( "arabic", "armenian", "basque", "bengali", "brazilian", "bulgarian", "catalan", "cjk", "czech", "danish", "dutch",
                "english", "finnish", "french", "galician", "german", "greek", "hindi", "hungarian", "indonesian", "irish", "italian",
                "latvian", "lithuanian", "norwegian", "persian", "portuguese", "romanian", "russian", "sorani", "spanish", "swedish",
                "turkish", "thai" );

    /** Every language tag the table is keyed by, as the language codes XP writes them. */
    private static final List<String> STEMMED_LANGUAGES =
        List.of( "en", "ar", "hy", "eu", "bn", "pt", "pt-BR", "bg", "ca", "zh", "ja", "ko", "cs", "da", "nl", "fi", "fr", "gl", "de", "el",
                 "hi", "hu", "id", "ga", "it", "lv", "lt", "nb", "nn", "fa", "ro", "ru", "ku", "es", "sv", "tr", "th" );

    private static final List<String> COLLATED_LANGUAGES =
        List.of( "ar", "hy", "bn", "bg", "zh", "ja", "ko", "cs", "da", "fi", "gl", "hi", "hu", "lv", "lt", "nb", "nn", "fa", "ro", "ru",
                 "es", "sv", "tr", "th", "af", "az", "be", "bs", "et", "fo", "he", "hr", "is", "kk", "mk", "pl", "sk", "sl", "sq", "sr",
                 "uk", "ur", "vi" );

    private final JsonNode template = readTemplate();

    @Test
    void everyStemmerAnalyzerTheTableNamesIsResolvable()
    {
        JsonNode declared = template.path( "settings" ).path( "analysis" ).path( "analyzer" );

        List<String> unresolved = new ArrayList<>();
        for ( String language : STEMMED_LANGUAGES )
        {
            String analyzer = IndexLanguages.stemmedAnalyzer( language );
            if ( !BUILT_IN_ANALYZERS.contains( analyzer ) && !declared.has( analyzer ) )
            {
                unresolved.add( language + " -> " + analyzer );
            }
        }
        assertEquals( List.of(), unresolved, "these analyzers exist in the language table but nowhere in the index template" );
    }

    /**
     * The one language whose analyzer is neither built in nor obvious: Nynorsk has no Lucene
     * analyzer, so the template authors one from a {@code light_nynorsk} stemmer. Asserted by name
     * because it is the row a careless port would collapse into {@code norwegian}, silently
     * stemming Nynorsk as Bokmål.
     */
    @Test
    void nynorskHasItsOwnAuthoredAnalyzerRatherThanFallingBackToBokmal()
    {
        assertEquals( "language_analyzer_nn", IndexLanguages.stemmedAnalyzer( "nn" ) );
        assertEquals( "norwegian", IndexLanguages.stemmedAnalyzer( "nb" ) );
        assertTrue( template.path( "settings" ).path( "analysis" ).path( "analyzer" ).has( "language_analyzer_nn" ) );
    }

    @Test
    void everyStemmedPostfixTheTableProducesHasADynamicTemplate()
    {
        Set<String> mapped = new LinkedHashSet<>();
        for ( JsonNode entry : template.path( "mappings" ).path( "dynamic_templates" ) )
        {
            entry.properties().forEach( e -> mapped.add( e.getValue().path( "path_match" ).asText() ) );
        }

        Set<String> missing = new TreeSet<>();
        for ( String language : STEMMED_LANGUAGES )
        {
            String pathMatch = "*." + IndexLanguages.stemmedPostfix( language );
            if ( !mapped.contains( pathMatch ) )
            {
                missing.add( language + " -> " + pathMatch );
            }
        }
        assertEquals( Set.of(), missing, "these stemmed sub-fields would fall through to the catch-all template" );
    }

    /** {@code pt-BR} is the one region-qualified row, and its postfix is the KEY lower-cased. */
    @Test
    void brazilianPortugueseKeepsItsRegionInTheStemmedPostfixButNotInTheCollation()
    {
        assertEquals( "_stemmed_pt-br", IndexLanguages.stemmedPostfix( "pt-BR" ) );
        assertEquals( "brazilian", IndexLanguages.stemmedAnalyzer( "pt-BR" ) );
        // Collation is country-BLIND: pt-BR sorts as pt, which has no collation, so DUCET.
        assertEquals( "_orderby_ducet", IndexLanguages.orderByPostfix( "pt-BR" ) );
        assertEquals( "portuguese", IndexLanguages.stemmedAnalyzer( "pt" ) );
    }

    @Test
    void everyOrderByLocaleIsOneTheTemplateRecords()
    {
        Set<String> supported = new LinkedHashSet<>();
        for ( JsonNode locale : template.path( "_doc" ).path( "icu_locales_supported_by_orderby" ) )
        {
            supported.add( locale.asText() );
        }

        Set<String> unknown = new TreeSet<>();
        for ( String language : COLLATED_LANGUAGES )
        {
            String locale = IndexLanguages.orderByLocale( language );
            if ( !supported.contains( locale ) )
            {
                unknown.add( language + " -> " + locale );
            }
        }
        assertEquals( Set.of(), unknown, "these collation locales are not in the template's recorded locale list" );
    }

    @Test
    void norwegianCollatesUnderTheSingleNoLocaleWhicheverCodeArrives()
    {
        assertEquals( "_orderby_no", IndexLanguages.orderByPostfix( "nb" ) );
        assertEquals( "_orderby_no", IndexLanguages.orderByPostfix( "no" ) );
        assertEquals( "_orderby_no", IndexLanguages.orderByPostfix( "nn" ) );
    }

    @Test
    void anUnmappedLocaleFallsBackToDucetRatherThanFailing()
    {
        assertEquals( "ducet", IndexLanguages.orderByLocale( "xx" ) );
        assertEquals( "ducet", IndexLanguages.orderByLocale( "" ) );
        assertEquals( "ducet", IndexLanguages.orderByLocale( null ) );
    }

    /**
     * A language with a collation but no stemmer must FAIL for {@code stemmed()} — XP raises
     * {@code "Unsupported language for stemmed function"} rather than querying an unanalyzed field,
     * and there are 20 such languages.
     */
    @Test
    void aCollationOnlyLanguageHasNoStemmer()
    {
        for ( String language : List.of( "pl", "he", "vi", "uk", "af", "sr" ) )
        {
            assertEquals( "_orderby_" + language, IndexLanguages.orderByPostfix( language ) );
            assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> IndexLanguages.stemmedPostfix( language ) );
        }
    }

    @Test
    void anUnknownLanguageHasNoStemmerEither()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> IndexLanguages.stemmedAnalyzer( "xx" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> IndexLanguages.stemmedAnalyzer( null ) );
    }

    private static JsonNode readTemplate()
    {
        try ( InputStream in = IndexLanguagesPortTest.class.getResourceAsStream( "/nodb/opensearch/index-template.json" ) )
        {
            return OpenSearchClient.mapper().readTree( in );
        }
        catch ( IOException e )
        {
            throw new IllegalStateException( "index-template.json is not on the test classpath", e );
        }
    }
}
