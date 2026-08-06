package com.enonic.nodb.engine.search;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 4 Gate C translation batch 1 (nodb/BUILD-PHASE-4.md): the structured families, the
 * six field-name-resolution rules, and — just as load-bearing — the Gate D/E FENCES.
 *
 * <p>These are JSON→JSON assertions with no OpenSearch in sight, which is the point: the Gate
 * 0(b)+(c) inventory's instruction was to unit-test the field-resolution matrix exhaustively
 * BEFORE the corpus diff makes a resolution bug expensive to find. The corpus proves the
 * results; this proves the requests.
 */
class QueryDslTranslatorTest
{
    private final QueryDslTranslator translator = new QueryDslTranslator( List.of( "draft" ) );

    // ---- rule 2/3: value type -> sub-field ------------------------------------------

    @Test
    void anUntypedStringTermLandsOnTheBaseTextVariant()
    {
        assertEquals( "{\"term\":{\"data.title._text\":{\"value\":\"bergen\"}}}", query( "{\"field\":\"data.title\",\"value\":\"Bergen\"}" ) );
    }

    @Test
    void aJsonNumberReachesTheNumberSubFieldAndIsAlwaysADouble()
    {
        // Rule 4: the ._number mapping is `double`, and XP's own ValueHelper always widened.
        // An integer here would be a different sort key on a mixed-type multi-index sort.
        assertEquals( "{\"term\":{\"data.population._number\":{\"value\":280000.0}}}",
                      query( "{\"field\":\"data.population\",\"value\":280000}" ) );
    }

    @Test
    void anExplicitDateTimeTypeReachesTheDatetimeSubFieldAsEpochMillis()
    {
        assertEquals( "{\"term\":{\"data.founded._datetime\":{\"value\":1579046400000}}}",
                      query( "{\"field\":\"data.founded\",\"type\":\"dateTime\",\"value\":\"2020-01-15T00:00:00Z\"}" ) );
    }

    /**
     * The rule-3 divergence, pinned: the SAME value without the explicit type lands on the base
     * text variant. The renderer must emit {@code type} for every dated AST value or results
     * change — this asserts that the backend really does behave differently, so the renderer's
     * obligation is not theoretical.
     */
    @Test
    void theSameDateWithoutAnExplicitTypeLandsOnTheBaseTextVariant()
    {
        assertTrue( query( "{\"field\":\"data.founded\",\"value\":\"2020-01-15T00:00:00Z\"}" ).contains( "data.founded._text" ) );
    }

    @Test
    void aGeoPointTypedValueFailsFastRatherThanResolving()
    {
        // Decision D3: measured to ERROR in Elasticsearch 2.4 too, so failing fast MATCHES
        // today's behaviour. The message mirrors the DSL family's own wording.
        QueryDslTranslator.UnsupportedQueryException e = assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                                                                       () -> query(
                                                                           "{\"field\":\"data.location\",\"type\":\"geoPoint\",\"value\":\"59.9,10.7\"}" ) );
        assertTrue( e.getMessage().contains( "geoPoint" ) );
    }

    // ---- rule 1: names are lowercased and trimmed -----------------------------------

    @Test
    void aFieldNameIsLoweredAndTrimmed()
    {
        assertEquals( "{\"term\":{\"data.mixedcase._text\":{\"value\":\"v\"}}}", query( "{\"field\":\"  data.MixedCase \",\"value\":\"v\"}" ) );
    }

    // ---- in / like / exists ---------------------------------------------------------

    /** N should-term clauses, never a {@code terms} query: a terms query scores differently. */
    @Test
    void inFansOutIntoShouldTermClausesPerValueType()
    {
        assertEquals( "{\"bool\":{\"should\":[{\"term\":{\"data.c._text\":{\"value\":\"c1\"}}}," +
                          "{\"term\":{\"data.c._text\":{\"value\":\"c3\"}}}]}}",
                      query( "{\"field\":\"data.c\",\"values\":[\"c1\",\"C3\"]}", "in" ) );
    }

    /**
     * Decision D4's ruling that gap G-2 be FIXED: a dated {@code in} resolves to
     * {@code ._datetime} instead of silently matching nothing on the base field. The corpus's
     * {@code GAP-G2-in-dated} row records the old ES behaviour (0 hits) and is therefore an
     * EXPECTED delta, not a regression.
     */
    @Test
    void aDatedInResolvesPerValueTypeWhichIsTheGapG2Fix()
    {
        assertTrue( query( "{\"field\":\"data.founded\",\"type\":\"dateTime\",\"values\":[\"2020-01-15T00:00:00Z\"]}", "in" ).contains(
            "data.founded._datetime" ) );
    }

    /** {@code like} forces the base text field: a wildcard pattern is text by definition. */
    @Test
    void likeForcesTheBaseTextFieldAndNormalizesItsPattern()
    {
        assertEquals( "{\"wildcard\":{\"data.title._text\":{\"value\":\"ber*\"}}}",
                      query( "{\"field\":\"data.title\",\"value\":\"BER*\"}", "like" ) );
    }

    @Test
    void existsForcesTheBaseTextFieldRegardlessOfValueType()
    {
        assertEquals( "{\"exists\":{\"field\":\"data.location._text\"}}", query( "{\"field\":\"data.location\"}", "exists" ) );
    }

    // ---- range / boolean / matchAll -------------------------------------------------

    @Test
    void rangeTypesEveryBoundFromTheSameReference()
    {
        assertEquals( "{\"range\":{\"data.population._number\":{\"gte\":200000.0,\"lte\":700000.0}}}",
                      query( "{\"field\":\"data.population\",\"gte\":200000,\"lte\":700000}", "range" ) );
    }

    @Test
    void aRangeWithNoBoundsIsRejectedRatherThanMatchingEverything()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> query( "{\"field\":\"data.x\"}", "range" ) );
    }

    /**
     * Pairwise nesting is preserved, never flattened: {@code must:[a,{must:[b,c]}]} and
     * {@code must:[a,b,c]} are set-equivalent but not score-equivalent, and the acceptance rule
     * forbids order drift on deterministic sorts.
     */
    @Test
    void logicalNestingIsPreservedPairwise()
    {
        String dsl = "{\"boolean\":{\"must\":[{\"term\":{\"field\":\"a\",\"value\":\"1\"}}," +
            "{\"boolean\":{\"must\":[{\"term\":{\"field\":\"b\",\"value\":\"2\"}},{\"term\":{\"field\":\"c\",\"value\":\"3\"}}]}}]}}";
        assertEquals( "{\"bool\":{\"must\":[{\"term\":{\"a._text\":{\"value\":\"1\"}}},{\"bool\":{\"must\":[" +
                          "{\"term\":{\"b._text\":{\"value\":\"2\"}}},{\"term\":{\"c._text\":{\"value\":\"3\"}}}]}}]}}",
                      translator.translateQuery( parse( dsl ) ).toString() );
    }

    /** A bare {@code NOT} is a boolean with only a mustNot clause — there is no separate query type. */
    @Test
    void aBareNotIsABooleanWithOnlyAMustNotClause()
    {
        assertEquals( "{\"bool\":{\"must_not\":[{\"term\":{\"a._text\":{\"value\":\"1\"}}}]}}", translator.translateQuery(
            parse( "{\"boolean\":{\"mustNot\":{\"term\":{\"field\":\"a\",\"value\":\"1\"}}}}" ) ).toString() );
    }

    @Test
    void matchAllCarriesItsBoost()
    {
        assertEquals( "{\"match_all\":{\"boost\":2.0}}", translator.translateQuery( parse( "{\"matchAll\":{\"boost\":2.0}}" ) ).toString() );
    }

    // ---- the composite-id rewrite ---------------------------------------------------

    /**
     * {@code _id} is not a field: OpenSearch rejects it as a document field, and the metadata
     * {@code _id} is the composite {@code <nodeId>@<branch>} (D10). An {@code _id} predicate
     * therefore becomes an {@code ids} query over composite ids, expanded across the request's
     * source branches — and NOT normalized, because a node id is an opaque exact token.
     */
    @Test
    void anIdTermBecomesAnIdsQueryOverCompositeDocumentIds()
    {
        assertEquals( "{\"ids\":{\"values\":[\"City-Oslo@draft\"]}}", query( "{\"field\":\"_id\",\"value\":\"City-Oslo\"}" ) );
    }

    @Test
    void anIdsFilterExpandsAcrossEverySourceBranch()
    {
        QueryDslTranslator multi = new QueryDslTranslator( List.of( "draft", "master" ) );
        assertEquals( "{\"ids\":{\"values\":[\"a@draft\",\"a@master\",\"b@draft\",\"b@master\"]}}",
                      multi.translateFilter( parse( "{\"ids\":{\"field\":\"_id\",\"values\":[\"a\",\"b\"]}}" ) ).toString() );
    }

    @Test
    void anIdPredicateThatCannotBeAnExactIdMatchFailsLoudly()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> query( "{\"field\":\"_id\",\"value\":\"oslo*\"}", "like" ) );
    }

    // ---- filters --------------------------------------------------------------------

    @Test
    void aValuesFilterIsATermsQueryOnTheResolvedSubField()
    {
        assertEquals( "{\"terms\":{\"data.c._text\":[\"c2\"]}}",
                      translator.translateFilter( parse( "{\"values\":{\"field\":\"data.c\",\"values\":[\"C2\"]}}" ) ).toString() );
    }

    /** An {@code ids} filter on a real field matches VERBATIM: no postfix, no lowercasing. */
    @Test
    void anIdsFilterOnARealFieldMatchesVerbatim()
    {
        assertEquals( "{\"terms\":{\"_branch\":[\"MyBranch\"]}}",
                      translator.translateFilter( parse( "{\"ids\":{\"field\":\"_branch\",\"values\":[\"MyBranch\"]}}" ) ).toString() );
    }

    @Test
    void aBooleanFilterTranslatesItsClausesAsFiltersNotAsQueries()
    {
        assertEquals( "{\"bool\":{\"must\":[{\"terms\":{\"a._text\":[\"1\"]}}],\"must_not\":[{\"exists\":{\"field\":\"b._text\"}}]}}",
                      translator.translateFilter( parse( "{\"boolean\":{\"must\":[{\"values\":{\"field\":\"a\",\"values\":[\"1\"]}}]," +
                                                             "\"mustNot\":[{\"exists\":{\"field\":\"b\"}}]}}" ) ).toString() );
    }

    // ---- rule 5: sorts --------------------------------------------------------------

    /**
     * {@code unmapped_type: keyword}, not {@code long} (Gate 0 item 4). Every {@code _orderby}
     * field holds a lexi-encoded ASCII string; the hardcoded {@code long} was harmless while the
     * field was mapped everywhere and fatal on a multi-index sort where one repo never had the
     * property — which is exactly Gate C's multi-source fan-out.
     */
    @Test
    void aFieldSortResolvesToOrderbyWithAKeywordUnmappedType()
    {
        assertEquals( "{\"data.title._orderby\":{\"order\":\"desc\",\"unmapped_type\":\"keyword\"}}",
                      translator.translateSort( parse( "{\"field\":\"data.title\",\"direction\":\"DESC\"}" ) ).toString() );
    }

    /** The pseudo-fields pass through unmodified and get NO unmapped_type — they are always mapped. */
    @Test
    void thePseudoFieldsPassThroughWithNoUnmappedType()
    {
        for ( String pseudo : List.of( "_score", "_id", "_doc" ) )
        {
            assertEquals( "{\"" + pseudo + "\":{\"order\":\"asc\"}}",
                          translator.translateSort( parse( "{\"field\":\"" + pseudo + "\",\"direction\":\"ASC\"}" ) ).toString() );
        }
    }

    // ---- Gate D: the text family ----------------------------------------------------

    @Test
    void fulltextTargetsTheFulltextSubFieldWithItsOwnAnalyzer()
    {
        assertEquals( "{\"simple_query_string\":{\"query\":\"fisk laks\",\"fields\":[\"data.description._fulltext\"]," +
                          "\"analyzer\":\"fulltext_search_default\",\"default_operator\":\"and\",\"analyze_wildcard\":true}}",
                      query( "{\"fields\":[\"data.description\"],\"query\":\"fisk laks\",\"operator\":\"AND\"}", "fulltext" ) );
    }

    @Test
    void anAbsentOperatorDefaultsToOrLowerCased()
    {
        assertTrue( query( "{\"fields\":[\"a\"],\"query\":\"x\"}", "fulltext" ).contains( "\"default_operator\":\"or\"" ) );
    }

    /**
     * {@code FulltextFunction} returns {@code MatchAllQueryBuilder} before it even resolves the
     * field names. The renderer already applies the rule for the NoQL form, so this covers the DSL
     * form — where XP's own builder would emit {@code "query": null} instead, which OpenSearch
     * rejects outright.
     */
    @Test
    void anEmptyFulltextQueryStringIsMatchAll()
    {
        assertEquals( "{\"match_all\":{}}", query( "{\"fields\":[\"a\"],\"query\":\"\"}", "fulltext" ) );
    }

    /** …and ngram/stemmed deliberately do NOT short-circuit, because neither XP function does. */
    @Test
    void anEmptyNgramQueryStringIsNotMatchAll()
    {
        assertTrue( query( "{\"fields\":[\"a\"],\"query\":\"\"}", "ngram" ).startsWith( "{\"simple_query_string\"" ) );
    }

    @Test
    void perFieldWeightsSurviveThePostfixInsertion()
    {
        // The postfix goes BEFORE the caret: `title._fulltext^5.0`, never `title^5._fulltext`.
        assertEquals( "{\"simple_query_string\":{\"query\":\"bergen\",\"fields\":[\"title._fulltext^5.0\",\"description._fulltext\"]," +
                          "\"analyzer\":\"fulltext_search_default\",\"default_operator\":\"or\",\"analyze_wildcard\":true}}",
                      query( "{\"fields\":[\"title^5\",\"description\"],\"query\":\"bergen\"}", "fulltext" ) );
    }

    @Test
    void aFieldWildcardKeepsTheStarLast()
    {
        // `descri*._fulltext` would match nothing: the star already swallowed the rest of the name.
        assertTrue( query( "{\"fields\":[\"descri*\"],\"query\":\"fisk\"}", "fulltext" ).contains( "\"descri*._fulltext\"" ) );
    }

    @Test
    void anInvalidWeightIsRejectedRatherThanReinterpretedByTheEngine()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> query( "{\"fields\":[\"a^-1\"],\"query\":\"x\"}", "fulltext" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> query( "{\"fields\":[\"a^abc\"],\"query\":\"x\"}", "fulltext" ) );
    }

    @Test
    void theG3AnalyzerOverrideWins()
    {
        assertTrue( query( "{\"fields\":[\"a\"],\"query\":\"x\",\"analyzer\":\"norwegian\"}", "fulltext" ).contains(
            "\"analyzer\":\"norwegian\"" ) );
    }

    @Test
    void ngramTargetsTheNgramSubFieldWithItsOwnAnalyzer()
    {
        assertEquals( "{\"simple_query_string\":{\"query\":\"fis lak\",\"fields\":[\"description._ngram\"]," +
                          "\"analyzer\":\"ngram_search_default\",\"default_operator\":\"and\",\"analyze_wildcard\":true}}",
                      query( "{\"fields\":[\"description\"],\"query\":\"fis lak\",\"operator\":\"AND\"}", "ngram" ) );
    }

    /**
     * The 4th {@code stemmed} argument is a LANGUAGE, and both the field postfix and the analyzer
     * are derived from it — {@code no} folds onto Bokmål for the field ({@code ._stemmed_nb}) and
     * onto the {@code norwegian} analyzer.
     */
    @Test
    void stemmedDerivesBothTheFieldAndTheAnalyzerFromTheLanguage()
    {
        assertEquals( "{\"simple_query_string\":{\"query\":\"fisker\",\"fields\":[\"description._stemmed_nb\"]," +
                          "\"analyzer\":\"norwegian\",\"default_operator\":\"or\",\"analyze_wildcard\":true}}",
                      query( "{\"fields\":[\"description\"],\"query\":\"fisker\",\"language\":\"no\"}", "stemmed" ) );
    }

    @Test
    void stemmedRejectsALanguageWithNoStemmer()
    {
        // `pl` has a collation but no analyzer, so XP raises rather than querying an unanalyzed field.
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> query( "{\"fields\":[\"a\"],\"query\":\"x\",\"language\":\"pl\"}", "stemmed" ) );
    }

    @Test
    void pathMatchIsAMatchOnThePathSubField()
    {
        assertEquals( "{\"match\":{\"_path._path\":{\"query\":\"/tree/a/b/c/d\",\"analyzer\":\"path_analyzer\"}}}",
                      query( "{\"field\":\"_path\",\"path\":\"/tree/a/b/c/d\"}", "pathMatch" ) );
    }

    /**
     * {@code minimumMatch} adds a hard prefix floor. The {@code + 1} in the truncation is not an
     * off-by-one: {@code "/tree/a/b/c/d".split("/")} has an empty leading element.
     */
    @Test
    void pathMatchWithAMinimumMatchAddsTheTruncatedPathTerm()
    {
        assertEquals( "{\"bool\":{\"must\":[{\"term\":{\"_path._path\":{\"value\":\"/tree/a/b\"}}}," +
                          "{\"match\":{\"_path._path\":{\"query\":\"/tree/a/b/c/d\",\"analyzer\":\"path_analyzer\"}}}]}}",
                      query( "{\"field\":\"_path\",\"path\":\"/tree/a/b/c/d\",\"minimumMatch\":3.0}", "pathMatch" ) );
    }

    @Test
    void pathMatchWithAMinimumMatchLongerThanThePathTruncatesToThePath()
    {
        assertTrue( query( "{\"field\":\"_path\",\"path\":\"/tree\",\"minimumMatch\":3.0}", "pathMatch" ).contains(
            "\"value\":\"/tree\"" ) );
    }

    // ---- Gate D: geo-distance and COLLATE sorts --------------------------------------

    @Test
    void aGeoDistanceSortTargetsTheGeopointSubField()
    {
        assertEquals( "{\"_geo_distance\":{\"location._geopoint\":[{\"lat\":59.91273,\"lon\":10.74609}],\"order\":\"desc\"}}",
                      translator.translateSort( parse(
                          "{\"field\":\"location\",\"type\":\"geoDistance\",\"location\":{\"lat\":59.91273,\"lon\":10.74609}," +
                              "\"direction\":\"DESC\"}" ) ).toString() );
    }

    @Test
    void aGeoDistanceSortCarriesTheUnitWhenGiven()
    {
        assertTrue( translator.translateSort( parse(
            "{\"field\":\"location\",\"type\":\"geoDistance\",\"location\":{\"lat\":1.0,\"lon\":2.0},\"unit\":\"km\"," +
                "\"direction\":\"ASC\"}" ) ).toString().contains( "\"unit\":\"km\"" ) );
    }

    @Test
    void aGeoDistanceSortWithoutALocationFailsLoudly()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> translator.translateSort(
            parse( "{\"field\":\"location\",\"type\":\"geoDistance\",\"direction\":\"ASC\"}" ) ) );
    }

    @Test
    void anUnknownSortTypeIsStillRejected()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> translator.translateSort( parse( "{\"field\":\"a\",\"type\":\"somethingElse\",\"direction\":\"ASC\"}" ) ) );
    }

    /**
     * D8, and the whole point of it: a COLLATE sort is an ORDINARY KEYWORD SORT on a field holding
     * a precomputed hex ICU collation key. No collation feature is configured here, because there
     * is none to configure.
     */
    @Test
    void aCollateSortIsAPlainKeywordSortOnTheLocaleQualifiedOrderByField()
    {
        assertEquals( "{\"word._orderby_no\":{\"order\":\"asc\",\"unmapped_type\":\"keyword\"}}",
                      translator.translateSort( parse( "{\"field\":\"word\",\"language\":\"nb\",\"direction\":\"ASC\"}" ) ).toString() );
    }

    @Test
    void collateNoAndNbResolveToTheSameField()
    {
        assertEquals( translator.translateSort( parse( "{\"field\":\"word\",\"language\":\"nb\",\"direction\":\"ASC\"}" ) ).toString(),
                      translator.translateSort( parse( "{\"field\":\"word\",\"language\":\"no\",\"direction\":\"ASC\"}" ) ).toString() );
    }

    /** A language with no collation entry sorts by DUCET — {@code de} is the recorded example. */
    @Test
    void aLanguageWithNoCollationSortsByDucet()
    {
        assertTrue( translator.translateSort( parse( "{\"field\":\"word\",\"language\":\"de\",\"direction\":\"ASC\"}" ) ).toString()
                        .contains( "word._orderby_ducet" ) );
        assertTrue( translator.translateSort( parse( "{\"field\":\"word\",\"language\":\"xx\",\"direction\":\"ASC\"}" ) ).toString()
                        .contains( "word._orderby_ducet" ) );
    }

    // ---- the Gate E fence -----------------------------------------------------------

    @Test
    void anUnknownQueryOrFilterTypeIsNeverSilentlyDropped()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> translator.translateQuery( parse( "{\"somethingNew\":{}}" ) ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> translator.translateFilter( parse( "{\"somethingNew\":{}}" ) ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> translator.translateQuery( parse( "{\"term\":{},\"in\":{}}" ) ) );
    }

    // ---- helpers -------------------------------------------------------------------

    private String query( String expression )
    {
        return query( expression, "term" );
    }

    private String query( String expression, String type )
    {
        return translator.translateQuery( parse( "{\"" + type + "\":" + expression + "}" ) ).toString();
    }

    private static JsonNode parse( String json )
    {
        try
        {
            return OpenSearchClient.mapper().readTree( json );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( json, e );
        }
    }
}
