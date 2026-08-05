package com.enonic.nodb.engine.search;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The mapping/analyzer port, asserted against the resource (the 13 breaking constructs from Gate
 * 0(b)+(c) plus the two blockers).
 *
 * <p>This is the nodb counterpart of core-repo's {@code IcuSortConfigConsistencyTest}, which stays
 * green and untouched: XP's own {@code search-settings.json}/{@code search-mapping.json} are not
 * modified by this gate, so its 44-filter assertions still describe reality on the ES path. The
 * port lives entirely in a new resource, and this class is what keeps that resource honest.
 *
 * <p>Structural assertions only — {@link OpenSearchMappingPortTest} proves the same file is
 * ACCEPTED by a real engine and that the templates actually fire.
 */
class IndexTemplatePortTest
{
    private static final IndexTemplate TEMPLATE = IndexTemplate.load();

    private static JsonNode settings()
    {
        return TEMPLATE.raw().path( "settings" );
    }

    private static JsonNode mappings()
    {
        return TEMPLATE.raw().path( "mappings" );
    }

    private static List<ObjectNode> templates()
    {
        List<ObjectNode> found = new ArrayList<>();
        for ( JsonNode entry : mappings().path( "dynamic_templates" ) )
        {
            found.add( (ObjectNode) entry );
        }
        return found;
    }

    private static String templateName( ObjectNode entry )
    {
        return entry.fieldNames().next();
    }

    private static JsonNode templateBody( ObjectNode entry )
    {
        return entry.path( templateName( entry ) );
    }

    // ------------------------------------------------------------------------------ blocker 2

    /**
     * BLOCKER 2, the one that fails with zero hits and no error: {@code match} matches the LEAF
     * name after dot expansion, so {@code "*._analyzed"} never fires. Every template must use
     * {@code path_match}.
     */
    @Test
    void everyDynamicTemplateUsesPathMatchNeverMatch()
    {
        for ( ObjectNode entry : templates() )
        {
            JsonNode body = templateBody( entry );
            assertTrue( body.has( "path_match" ), templateName( entry ) + " must use path_match" );
            assertFalse( body.has( "match" ), templateName( entry ) + " must NOT use match (it silently never fires)" );
        }
    }

    // ---------------------------------------------------------------------------- D1 / D1b / D8

    @Test
    void theTextPostfixHasItsOwnTemplate()
    {
        JsonNode mapping = templateFor( "*._text" );
        assertEquals( "keyword", mapping.path( "type" ).asText() );
        assertEquals( "keywordlowercase", mapping.path( "normalizer" ).asText() );
        assertEquals( 3072, mapping.path( "ignore_above" ).asInt(),
                      "XP's own template_metadata_strings used 3072; Gate 0(e) measured that behaviour" );
    }

    @Test
    void fulltextReplacesAnalyzedAndNothingStillMatchesAnalyzed()
    {
        JsonNode mapping = templateFor( "*._fulltext" );
        assertEquals( "text", mapping.path( "type" ).asText() );
        assertEquals( "document_index_default", mapping.path( "analyzer" ).asText() );
        assertTrue( templates().stream().noneMatch( entry -> templateBody( entry ).path( "path_match" ).asText().endsWith( "_analyzed" ) ) );
    }

    /**
     * Preserved deliberately (and flagged by Gate 0(d) as a behaviour change if it were not):
     * search-mapping.json sets {@code search_analyzer} ONLY on {@code *._ngram}. Everything else
     * falls back to the index-level default search analyzer, which is the analyzer literally named
     * {@code default_search}.
     */
    @Test
    void onlyTheNgramTemplateSetsAnExplicitSearchAnalyzer()
    {
        assertEquals( "document_index_default", templateFor( "*._ngram" ).path( "search_analyzer" ).asText() );
        assertFalse( templateFor( "*._fulltext" ).has( "search_analyzer" ) );
        assertFalse( templateFor( "*._path" ).has( "search_analyzer" ) );
        assertNotNull( settings().path( "analysis" ).path( "analyzer" ).get( "default_search" ),
                       "default_search is a RESERVED name: it IS the index-level default search analyzer" );
    }

    /** D8: 44 locale templates collapse into one plain keyword template, sort-only. */
    @Test
    void theFortyFourOrderByLocaleTemplatesCollapseIntoOneKeywordTemplate()
    {
        List<ObjectNode> localised = templates().stream()
            .filter( entry -> templateBody( entry ).path( "path_match" ).asText().startsWith( "*._orderby_" ) )
            .toList();
        assertEquals( 1, localised.size(), "one template, not 44" );

        JsonNode mapping = templateBody( localised.get( 0 ) ).path( "mapping" );
        assertEquals( "keyword", mapping.path( "type" ).asText() );
        assertFalse( mapping.path( "index" ).asBoolean( true ), "collation keys are sorted on, never term-queried" );
        assertTrue( mapping.path( "doc_values" ).asBoolean( false ) );
        assertTrue( mapping.path( "ignore_above" ).asInt() >= CollationKeyResolver.MAX_HEX_LENGTH,
                    "ignore_above must sit above the resolver's own cap so a key is never silently dropped" );
        assertFalse( mapping.has( "normalizer" ), "hex keys must not be normalized" );
    }

    /** D8: with the filters gone, analysis-icu is needed for nothing — hence the stock image. */
    @Test
    void noIcuCollationFilterOrAnalyzerSurvives()
    {
        String serialized = settings().toString();
        assertFalse( serialized.contains( "icu_collation" ), "the 44 icu_collation filters are deleted (D8)" );
        assertFalse( serialized.contains( "icu_sort_" ), "the 44 icu_sort_* analyzers are deleted (D8)" );
        assertFalse( TEMPLATE.raw().path( "mappings" ).toString().contains( "icu_collation_keyword" ),
                     "not the icu_collation_keyword field type either -- XP-side keys supersede it" );
    }

    // -------------------------------------------------------------------- removed ES 2.4-isms

    @Test
    void noEs24MappingConstructsRemain()
    {
        String serialized = TEMPLATE.raw().path( "mappings" ).toString();
        assertFalse( serialized.contains( "\"type\":\"string\"" ), "type:string has no handler in OpenSearch" );
        assertFalse( serialized.contains( "\"index\":\"analyzed\"" ) );
        assertFalse( serialized.contains( "\"index\":\"not_analyzed\"" ) );
        assertFalse( serialized.contains( "include_in_all" ), "_all is gone, so include_in_all is an unknown parameter" );
        assertFalse( mappings().has( "_all" ) );
        assertFalse( TEMPLATE.raw().has( "_default_" ) );
        assertFalse( mappings().has( "_default_" ), "mapping types are gone; branch is a keyword field instead" );
    }

    @Test
    void theRemovedStandardTokenFilterIsGoneFromEveryChain()
    {
        JsonNode analyzers = settings().path( "analysis" ).path( "analyzer" );
        analyzers.properties().forEach( entry -> {
            for ( JsonNode filter : entry.getValue().path( "filter" ) )
            {
                assertFalse( "standard".equals( filter.asText() ),
                             "analyzer " + entry.getKey() + " still lists the REMOVED standard token filter" );
            }
        } );
    }

    /** {@code all_field_analyzer} was {@code _all}'s only consumer (Gate 0(d) decision 6). */
    @Test
    void theDeadAllFieldAnalyzerIsDeleted()
    {
        assertFalse( settings().path( "analysis" ).path( "analyzer" ).has( "all_field_analyzer" ) );
    }

    @Test
    void edgeNgramIsRenamedAndTheInertSideParamIsDropped()
    {
        JsonNode frontNgram = settings().path( "analysis" ).path( "filter" ).path( "front_ngram" );
        assertEquals( "edge_ngram", frontNgram.path( "type" ).asText() );
        assertEquals( 1, frontNgram.path( "min_gram" ).asInt() );
        assertEquals( 25, frontNgram.path( "max_gram" ).asInt() );
        assertFalse( frontNgram.has( "side" ), "\"side\":\"front\" is silently accepted and INERT in Lucene 10" );
    }

    /** Item 5: {@code ignore_above} is illegal on text, so the keyword ports need a normalizer that did not exist. */
    @Test
    void theLowercaseNormalizerIsAuthored()
    {
        JsonNode normalizer = settings().path( "analysis" ).path( "normalizer" ).path( "keywordlowercase" );
        assertEquals( "custom", normalizer.path( "type" ).asText() );
        assertEquals( "lowercase", normalizer.path( "filter" ).get( 0 ).asText() );
        assertEquals( 1, normalizer.path( "filter" ).size(), "no asciifolding: Gate 0(e) measured 0 hits for folded values" );
    }

    @Test
    void ignoreAboveOnlyAppearsOnKeywordFields()
    {
        for ( ObjectNode entry : templates() )
        {
            JsonNode mapping = templateBody( entry ).path( "mapping" );
            if ( mapping.has( "ignore_above" ) )
            {
                assertEquals( "keyword", mapping.path( "type" ).asText(),
                              templateName( entry ) + ": ignore_above is illegal on text" );
            }
        }
    }

    @Test
    void datesCarryAnExplicitFormatAndNumbersHaveNoIndexFlag()
    {
        JsonNode date = templateFor( "*._datetime" );
        assertEquals( "date", date.path( "type" ).asText() );
        assertTrue( date.path( "format" ).asText().startsWith( "epoch_millis" ), "the wire ships epoch millis" );

        assertEquals( "double", templateFor( "*._number" ).path( "type" ).asText() );
        assertFalse( templateFor( "*._number" ).has( "index" ) );
        assertEquals( "geo_point", templateFor( "*._geopoint" ).path( "type" ).asText() );
        assertFalse( templateFor( "*._geopoint" ).has( "index" ) );
    }

    /** Item 12: OpenSearch's 1000-field default plus object mappers counting = a random Gate F failure. */
    @Test
    void limitsAreRaisedExplicitly()
    {
        JsonNode index = settings().path( "index" );
        assertEquals( 10_000, index.path( "mapping" ).path( "total_fields" ).path( "limit" ).asInt() );
        assertEquals( 50, index.path( "mapping" ).path( "depth" ).path( "limit" ).asInt() );
        assertEquals( 10_000, index.path( "max_result_window" ).asInt() );
        assertEquals( 1, index.path( "number_of_shards" ).asInt() );
    }

    @Test
    void detectionIsOffAndSourceIsEnabled()
    {
        assertFalse( mappings().path( "date_detection" ).asBoolean( true ) );
        assertFalse( mappings().path( "numeric_detection" ).asBoolean( true ) );
        assertTrue( mappings().path( "_source" ).path( "enabled" ).asBoolean( false ),
                    "_source carries returnFields and the rebuild story" );
    }

    /** Port item 3: branch stopped being a mapping type, so it is an explicitly mapped keyword. */
    @Test
    void identityFieldsAreExplicitKeywordProperties()
    {
        JsonNode properties = mappings().path( "properties" );
        assertEquals( "keyword", properties.path( IndexFields.BRANCH ).path( "type" ).asText() );
        assertEquals( "keyword", properties.path( IndexFields.REPO ).path( "type" ).asText() );
        assertEquals( "keyword", properties.path( IndexFields.DOCUMENT_ANALYZER ).path( "type" ).asText() );
    }

    /**
     * Dynamic templates are evaluated in order, first match wins, and the catch-all matches
     * everything — so it has to be last or it eats every postfix template. This assertion is the
     * only thing standing between a reordering edit and a silent regression to blocker 2's symptom.
     */
    @Test
    void theCatchAllTemplateIsLast()
    {
        List<ObjectNode> all = templates();
        for ( int i = 0; i < all.size() - 1; i++ )
        {
            assertFalse( "*".equals( templateBody( all.get( i ) ).path( "path_match" ).asText() ),
                         templateName( all.get( i ) ) + " matches everything but is not last" );
        }
        assertEquals( "*", templateBody( all.get( all.size() - 1 ) ).path( "path_match" ).asText() );
    }

    /**
     * The third blocker, found at Gate A by indexing a real document (see the resource's
     * {@code catch_all_must_exclude_objects} note). ES 2.4's {@code match: "*"} matched LEAF names;
     * {@code path_match: "*"} matches every PATH, including the object nodes dot expansion creates
     * — so without {@code match_mapping_type} the catch-all maps {@code data} as a keyword and EVERY
     * document fails with {@code Can't get text on a START_OBJECT}.
     */
    @Test
    void theCatchAllExcludesObjectsViaMatchMappingType()
    {
        List<ObjectNode> all = templates();
        JsonNode catchAll = templateBody( all.get( all.size() - 1 ) );
        assertEquals( "string", catchAll.path( "match_mapping_type" ).asText(),
                      "a single value, not an array: OpenSearch 3.7 rejects the array form ES 8 accepts" );
    }

    /** 91 ES templates become 48: 38 stemmed + 8 postfix + _text + the catch-all. */
    @Test
    void theStemmedLanguageTemplatesAreAllPortedWithTheirAnalyzers()
    {
        List<ObjectNode> stemmed = templates().stream()
            .filter( entry -> templateBody( entry ).path( "path_match" ).asText().startsWith( "*._stemmed_" ) )
            .toList();
        assertEquals( 38, stemmed.size() );

        assertEquals( "english", templateFor( "*._stemmed_en" ).path( "analyzer" ).asText() );
        assertEquals( "language_analyzer_nn", templateFor( "*._stemmed_nn" ).path( "analyzer" ).asText() );
        assertEquals( "norwegian", templateFor( "*._stemmed_nb" ).path( "analyzer" ).asText() );
        assertEquals( "brazilian", templateFor( "*._stemmed_pt-br" ).path( "analyzer" ).asText() );
        // ja/ko/zh on core cjk is why no analysis plugin beyond ICU was ever needed.
        assertEquals( "cjk", templateFor( "*._stemmed_ja" ).path( "analyzer" ).asText() );
        assertEquals( "cjk", templateFor( "*._stemmed_ko" ).path( "analyzer" ).asText() );
        assertEquals( "cjk", templateFor( "*._stemmed_zh" ).path( "analyzer" ).asText() );

        assertEquals( 48, templates().size() );
    }

    /** The 44 supported `_orderby_<loc>` codes survive as a recorded inventory even though the templates collapsed. */
    @Test
    void theSupportedOrderByLocalesAreRecordedAndResolvable()
    {
        JsonNode locales = TEMPLATE.raw().path( "_doc" ).path( "icu_locales_supported_by_orderby" );
        assertEquals( 44, locales.size() );
        for ( JsonNode locale : locales )
        {
            assertNotNull( CollationKeyResolver.collationKey( locale.asText(), "æøå" ),
                           "locale " + locale.asText() + " must resolve to a collator" );
        }
    }

    @Test
    void theTemplateVersionIsStampedIntoTheMappingMeta()
    {
        assertEquals( 1, TEMPLATE.templateVersion() );
        assertEquals( 1, mappings().path( "_meta" ).path( "templateVersion" ).asInt() );
    }

    /** The documentation block must not reach OpenSearch, and the two env knobs must be substituted. */
    @Test
    void createIndexBodyStripsDocsSubstitutesEnvKnobsAndAttachesTheAlias()
    {
        ObjectNode body = TEMPLATE.createIndexBody( "acme-repo", OpenSearchConfig.of( "http://x" ).withRefreshInterval( "-1" ) );

        assertFalse( body.has( "_doc" ) );
        assertEquals( "-1", body.path( "settings" ).path( "index" ).path( "refresh_interval" ).asText() );
        assertEquals( 0, body.path( "settings" ).path( "index" ).path( "number_of_replicas" ).asInt() );
        assertTrue( body.path( "aliases" ).has( "acme-repo" ) );
        // A composable template cannot carry a per-index alias, which is why the alias is here.
        assertTrue( body.path( "mappings" ).path( "dynamic_templates" ).isArray() );
    }

    private static JsonNode templateFor( String pathMatch )
    {
        return templates().stream()
            .filter( entry -> pathMatch.equals( templateBody( entry ).path( "path_match" ).asText() ) )
            .findFirst()
            .map( entry -> templateBody( entry ).path( "mapping" ) )
            .orElseThrow( () -> new AssertionError( "No dynamic template for path_match " + pathMatch ) );
    }
}
