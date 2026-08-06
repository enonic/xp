package com.enonic.xp.repo.impl.search.dsl;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query.ConstraintExpressionBuilder;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The equivalence oracle for the NoQL → DSL renderer: for one NoQL string, build the
 * Elasticsearch query BOTH ways —
 * <pre>
 *   NoQL family: QueryParser.parse(noql) → ConstraintExpressionBuilder
 *   DSL family:  QueryParser.parse(noql) → SearchDslRenderer → DslExpr → ConstraintExpressionBuilder
 * </pre>
 * — and require the two JSON documents to be identical. Where a golden fixture recorded by
 * the pre-existing NoQL-family tests exists, the DSL round-trip is additionally diffed
 * against that file, so the mapping is pinned at byte level to a artifact this gate did not
 * author.
 * <p>
 * Two normalizations apply, both of them properties of the format rather than conveniences:
 * <ul>
 * <li>{@code _name} members are removed. The two builder families disagree about which
 * queries carry a {@code queryName} ({@code range}/{@code wildcard} get one from the DSL
 * builders and not from the expression tree), and nothing in XP reads
 * {@code matched_queries}, so the canonical DSL deliberately carries no query names.</li>
 * <li>Whitespace is stripped, exactly as {@code BaseTestBuilderFactory#cleanString} does for
 * the golden files this test reuses.</li>
 * </ul>
 * Rows that cannot round-trip are exactly the ruled gaps G-1…G-5, and each is asserted
 * explicitly below instead of being skipped.
 */
class NoqlDslRoundTripOracleTest
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final String FIXTURES = "/com/enonic/xp/repo/impl/elasticsearch/query/translator/factory/";

    private static TimeZone originalTimeZone;

    @BeforeEach
    void setUp()
    {
        originalTimeZone = TimeZone.getDefault();
        TimeZone.setDefault( TimeZone.getTimeZone( "UTC" ) );
    }

    @AfterEach
    void tearDown()
    {
        TimeZone.setDefault( originalTimeZone );
    }

    /**
     * Every construct of the Gate 0(a) mapping table that has a form both families can build.
     * {@code golden} is the pre-existing NoQL-family fixture, or {@code null} when the
     * construct was only ever asserted programmatically.
     */
    static Stream<Arguments> constructs()
    {
        return Stream.of(
            // --- 10 comparison operators -------------------------------------------------
            row( "compare EQ string", "myField = 'myValue'", "query/compare_eq_string.json" ),
            row( "compare EQ number", "myField = 1", "query/compare_eq_number.json" ),
            row( "compare EQ dateTime", "myField = instant('2013-11-29T09:42:00Z')", "query/compare_eq_datetime.json" ),
            row( "compare EQ localDateTime", "myField = localDateTime('2015-12-31T23:00:10')", null ),
            row( "compare EQ date", "myField = date('2021-02-26')", null ),
            row( "compare EQ time", "myField = time('11:23')", null ),
            row( "compare NEQ string", "myField != 'myValue'", "query/compare_neq_string.json" ),
            row( "compare NEQ number", "myField != 3", "query/compare_neq_number.json" ),
            row( "compare GT number", "myField > 3", "query/compare_gt_number.json" ),
            row( "compare GT string", "myField > 'myString'", "query/compare_gt_string.json" ),
            row( "compare GT dateTime", "myField > instant('2013-11-29T11:00:00.000Z')", "query/compare_gt_datetime.json" ),
            row( "compare GTE number", "myField >= 3", "query/compare_gte_number.json" ),
            row( "compare LT number", "myField < 3", "query/compare_lt_number.json" ),
            row( "compare LTE number", "myField <= 3", null ),
            row( "compare LIKE", "myField LIKE 'myValue'", "query/compare_like_string.json" ),
            row( "compare LIKE wildcards", "myField LIKE '*fisk?'", null ),
            row( "compare NOT LIKE", "myField NOT LIKE 'myValue'", "query/compare_not_like_string.json" ),
            row( "compare IN string", "myField IN ('myFirstValue', 'mySecondValue')", "query/compare_in_string.json" ),
            row( "compare NOT IN string", "myField NOT IN ('myFirstValue', 'mySecondValue')", "query/compare_not_in_string.json" ),

            // --- logical AND/OR/NOT, nesting preserved pairwise --------------------------
            row( "logical AND", "a = 'x' AND b = 'y'", null ),
            row( "logical OR", "a = 'x' OR b = 'y'", null ),
            row( "logical AND left-associative chain", "a = 'x' AND b = 'y' AND c = 'z'", null ),
            row( "logical OR left-associative chain", "a = 'x' OR b = 'y' OR c = 'z'", null ),
            row( "logical AND/OR precedence", "a = 'x' AND b = 'y' OR c = 'z'", null ),
            row( "logical parenthesised", "a = 'x' AND (b = 'y' OR c = 'z')", null ),
            row( "bare NOT", "NOT (fisk = 'ost')", "query/not_term.json" ),
            row( "NOT over range", "not( myField > 1)", "not_range.json" ),
            row( "double NOT", "not( not( myField > 1  ))", "not_not_range.json" ),
            row( "NOT over logical", "NOT (a = 'x' AND b = 'y')", null ),

            // --- constraint functions ----------------------------------------------------
            row( "fulltext 3 args", "fulltext('myField', 'my search phrase', 'OR')", "fulltext_3_args.json" ),
            row( "fulltext 2 args", "fulltext('myField', 'fisk')", null ),
            row( "fulltext AND operator", "fulltext('myField', 'fisk', 'AND')", null ),
            row( "fulltext weighted fields", "fulltext('displayName^3,_name^5,custom', 'fisk')", null ),
            row( "ngram 2 args", "ngram('fiskebolle', 'Her er teksten min')", "function/ngram_two_arguments.json" ),
            row( "ngram operator override", "ngram('fiskebolle', 'Her er teksten min', 'AND')", "function/ngram_override_operator.json" ),
            row( "stemmed", "stemmed('field', 'organize', 'OR', 'en')", "function/stemmed_function.json" ),
            row( "pathMatch 2 args", "pathMatch('myPath', '/fisk')", "function/pathMatch.json" ),
            row( "pathMatch minimumMatch", "pathMatch('myPath', '/fisk', 3)", "function/pathMatch_minimum_match.json" ),
            row( "range instant no include", "range('MyField', instant('1975-08-01T10:00Z'), instant('1975-08-01T10:00Z'))",
                 "function/range_instant_no_include.json" ),
            row( "range instant includes",
                 "range('MyField', instant('1975-08-01T10:00Z'), instant('1975-08-01T10:00Z'), 'true', 'false')",
                 "function/range_instant_includes.json" ),
            row( "range instant-as-string includes", "range('MyField', '1975-08-01T10:00Z', '1975-08-01T10:00Z', 'true', 'false')",
                 "function/range_instant_includes.json" ),
            row( "range string includes", "range('MyField', '5.1.0', '5.3.0', 'true', 'false')", "function/range_string_includes.json" ),
            row( "range numeric includes", "range('MyField', 2, 3, 'true', 'false')", "function/range_numeric_includes.json" ),
            row( "range numeric no include", "range('MyField', 2, 3)", null ) );
    }

    private static Arguments row( final String name, final String noql, final String golden )
    {
        return Arguments.of( name, noql, golden );
    }

    @ParameterizedTest( name = "{0}" )
    @MethodSource( "constructs" )
    void roundTripsToTheSameElasticsearchQuery( final String name, final String noql, final String golden )
        throws Exception
    {
        final QueryExpr queryExpr = QueryParser.parse( noql );

        final String viaExpressionTree = normalize( buildFromExpressionTree( queryExpr ) );
        final String viaDsl = normalize( buildFromRenderedDsl( queryExpr ) );

        assertEquals( viaExpressionTree, viaDsl, "renderer diverged from the NoQL expression tree for [" + noql + "]" );

        if ( golden != null )
        {
            final String expected = normalize( load( golden ) );
            assertEquals( expected, viaExpressionTree, "golden file no longer describes the NoQL path for [" + noql + "]" );
            assertEquals( expected, viaDsl, "rendered DSL does not reproduce the golden file for [" + noql + "]" );
        }
    }

    @Test
    void coversEveryFixtureItClaims()
    {
        final long anchored = constructs().map( arguments -> arguments.get()[2] ).filter( golden -> golden != null ).count();
        assertEquals( 28, anchored, "the oracle's golden-fixture anchor count changed" );
        assertEquals( 44, constructs().count(), "the oracle's construct count changed" );
    }

    // --- the ruled gaps: asserted, never skipped --------------------------------------

    @Test
    void gapG1_geoPointTermFailsFast()
    {
        final QueryExpr queryExpr = QueryParser.parse( "myField = geoPoint('59.9127300,10.746090')" );

        final DslRenderException e =
            assertThrows( DslRenderException.class, () -> SearchDslRenderer.renderConstraint( queryExpr ) );
        assertTrue( e.getMessage().contains( "geoPoint values are not supported" ), e.getMessage() );
    }

    @Test
    void gapG1_geoPointRangeAndInFailFast()
    {
        assertThrows( DslRenderException.class,
                      () -> SearchDslRenderer.renderConstraint( QueryParser.parse( "myField > geoPoint('1,2')" ) ) );
        assertThrows( DslRenderException.class, () -> SearchDslRenderer.renderConstraint(
            QueryParser.parse( "myField IN (geoPoint('1,2'), geoPoint('3,4'))" ) ) );
    }

    @Test
    void gapG2_datedInResolvesTheDatetimeSubFieldInsteadOfMatchingNothing()
        throws Exception
    {
        final QueryExpr queryExpr = QueryParser.parse( "myField IN (instant('2015-02-26T12:00:00.030Z'))" );

        final Map<String, Object> dsl = SearchDslRenderer.renderConstraint( queryExpr );
        assertEquals( "dateTime", ( (Map<?, ?>) dsl.get( "in" ) ).get( "type" ) );

        final String viaDsl = normalize( buildFromRenderedDsl( queryExpr ) );
        assertTrue( viaDsl.contains( "myfield._datetime" ), viaDsl );

        // The expression tree pins the field to the bare string variant while still converting
        // the value to an Instant — a term that can never match. That is the bug being fixed.
        final String viaExpressionTree = normalize( buildFromExpressionTree( queryExpr ) );
        assertTrue( viaExpressionTree.contains( "\"myfield\"" ), viaExpressionTree );
        assertNotEquals( viaExpressionTree, viaDsl );
    }

    @Test
    void gapG2_numericInResolvesTheNumberSubField()
        throws Exception
    {
        final QueryExpr queryExpr = QueryParser.parse( "myField IN (1, 2)" );

        final String viaDsl = normalize( buildFromRenderedDsl( queryExpr ) );
        assertTrue( viaDsl.contains( "myfield._number" ), viaDsl );
        assertNotEquals( normalize( buildFromExpressionTree( queryExpr ) ), viaDsl );
    }

    @Test
    void gapG2_mixedDatedAndUntypedInFailsFast()
    {
        assertThrows( DslRenderException.class, () -> SearchDslRenderer.renderConstraint(
            QueryParser.parse( "myField IN (instant('2015-02-26T12:00:00.030Z'), 'fisk')" ) ) );
    }

    @Test
    void gapG3_customAnalyzerRidesTheWireAsAnOptionalField()
    {
        final Map<String, Object> dsl = SearchDslRenderer.renderConstraint(
            QueryParser.parse( "ngram('fiskebolle', 'Her er teksten min', 'AND', 'MyNewAnalyzer')" ) );

        assertEquals( "MyNewAnalyzer", ( (Map<?, ?>) dsl.get( "ngram" ) ).get( "analyzer" ) );
    }

    @Test
    void gapG3_theAnalyzerFieldIsNotPartOfXpsOwnDslBuilders()
        throws Exception
    {
        // The wire schema is a superset of the public DSL: XP's own DSL builders ignore
        // `analyzer`, so this row is expected NOT to reproduce the NoQL golden file. Asserting
        // the exact delta keeps that a recorded fact rather than a silent difference.
        final QueryExpr queryExpr = QueryParser.parse( "ngram('fiskebolle', 'Her er teksten min', 'AND', 'MyNewAnalyzer')" );

        assertTrue( normalize( load( "function/ngram_set_analyzer.json" ) ).contains( "\"analyzer\":\"MyNewAnalyzer\"" ) );
        assertTrue( normalize( buildFromRenderedDsl( queryExpr ) ).contains( "\"analyzer\":\"ngram_search_default\"" ) );
    }

    @Test
    void gapG4_rawCaseRangeBoundsAreNormalisedInsteadOfMatchingNothing()
        throws Exception
    {
        final QueryExpr queryExpr = QueryParser.parse( "range('MyField', 'Alpha', 'Omega', 'true', 'true')" );

        final String viaDsl = normalize( buildFromRenderedDsl( queryExpr ) );
        assertTrue( viaDsl.contains( "\"from\":\"alpha\"" ), viaDsl );
        assertTrue( viaDsl.contains( "\"to\":\"omega\"" ), viaDsl );

        // The expression tree ships the bounds raw against a lowercased field: zero hits.
        assertTrue( normalize( buildFromExpressionTree( queryExpr ) ).contains( "\"from\":\"Alpha\"" ) );
    }

    @Test
    void gapG5_rangeWithBothBoundsEmptyBecomesExists()
        throws Exception
    {
        final QueryExpr queryExpr = QueryParser.parse( "range('MyField', '', '')" );

        final Map<String, Object> dsl = SearchDslRenderer.renderConstraint( queryExpr );
        assertEquals( Map.of( "field", "myfield" ), dsl.get( "exists" ) );

        assertEquals( "{\"exists\":{\"field\":\"myfield\"}}", normalize( buildFromRenderedDsl( queryExpr ) ) );
    }

    // --- canonicalization rules -------------------------------------------------------

    @Test
    void anAbsentConstraintBecomesMatchAll()
    {
        assertEquals( Map.of( "matchAll", Map.of() ), SearchDslRenderer.renderConstraint( QueryParser.parse( "" ) ) );
    }

    @Test
    void logicalNestingIsPreservedPairwiseAndNeverFlattened()
    {
        final Map<String, Object> dsl = SearchDslRenderer.renderConstraint( QueryParser.parse( "a = 'x' AND b = 'y' AND c = 'z'" ) );

        final List<?> outer = (List<?>) ( (Map<?, ?>) dsl.get( "boolean" ) ).get( "must" );
        assertEquals( 2, outer.size(), "a three-term AND must render as two nested pairs, not a flat must[3]" );
        assertTrue( ( (Map<?, ?>) outer.get( 0 ) ).containsKey( "boolean" ), "left-associative: the left operand is the nested pair" );
        assertTrue( ( (Map<?, ?>) outer.get( 1 ) ).containsKey( "term" ) );
    }

    @Test
    void fieldNamesAreEmittedPostIndexPathWithoutSubFieldPostfixes()
    {
        final Map<String, Object> dsl = SearchDslRenderer.renderConstraint( QueryParser.parse( "MyField.Sub = instant('2013-11-29T09:42:00Z')" ) );

        final Map<?, ?> term = (Map<?, ?>) dsl.get( "term" );
        assertEquals( "myfield.sub", term.get( "field" ) );
        assertEquals( "dateTime", term.get( "type" ) );
    }

    @Test
    void numericsAreJsonNumbersWithDoubleSemantics()
    {
        final Map<?, ?> term = (Map<?, ?>) SearchDslRenderer.renderConstraint( QueryParser.parse( "myField = 1" ) ).get( "term" );
        assertEquals( Double.valueOf( 1.0 ), term.get( "value" ) );
    }

    @Test
    void noQueryNameIsCarried()
    {
        assertTrue( MAPPER.valueToTree( SearchDslRenderer.renderConstraint( QueryParser.parse( "myField = 'x'" ) ) )
                        .findValues( "queryName" )
                        .isEmpty() );
    }

    @Test
    void unsupportedFunctionsFailFast()
    {
        assertThrows( DslRenderException.class,
                      () -> SearchDslRenderer.renderConstraint( QueryParser.parse( "unsupported('a', 'b')" ) ) );
    }

    // --- order by ---------------------------------------------------------------------

    @Test
    void ordersRenderWithAnAlwaysExplicitDirection()
    {
        assertEquals( Map.of( "field", "myfield", "direction", "ASC" ),
                      SearchDslRenderer.renderOrder( QueryParser.parseOrderExpressions( "myField" ).get( 0 ) ) );
        assertEquals( Map.of( "field", "myfield", "direction", "DESC" ),
                      SearchDslRenderer.renderOrder( QueryParser.parseOrderExpressions( "myField DESC" ).get( 0 ) ) );
    }

    @Test
    void collateRendersAsALanguageTag()
    {
        assertEquals( Map.of( "field", "myfield", "language", "no", "direction", "ASC" ),
                      SearchDslRenderer.renderOrder( QueryParser.parseOrderExpressions( "myField COLLATE no" ).get( 0 ) ) );
    }

    @Test
    void geoDistanceOrderRendersLocationAndUnit()
    {
        assertEquals( Map.of( "field", "myfield", "type", "geoDistance", "location", Map.of( "lat", -50.0, "lon", 40.0 ), "unit", "km",
                              "direction", "DESC" ), SearchDslRenderer.renderOrder(
            QueryParser.parseOrderExpressions( "geoDistance('myField', '-50,40', 'km') DESC" ).get( 0 ) ) );
    }

    @Test
    void compoundOrderRendersEveryElement()
    {
        final List<com.enonic.xp.query.expr.OrderExpr> orders =
            QueryParser.parseOrderExpressions( "myField DESC, geoDistance('geo', '1,2') ASC, other COLLATE en" );

        assertEquals( 3, orders.size() );
        assertEquals( "myfield", SearchDslRenderer.renderOrder( orders.get( 0 ) ).get( "field" ) );
        assertEquals( "geoDistance", SearchDslRenderer.renderOrder( orders.get( 1 ) ).get( "type" ) );
        assertEquals( "en", SearchDslRenderer.renderOrder( orders.get( 2 ) ).get( "language" ) );
    }

    @Test
    void deadScoreOrderFailsFast()
    {
        assertThrows( DslRenderException.class,
                      () -> SearchDslRenderer.renderOrder( QueryParser.parseOrderExpressions( "score()" ).get( 0 ) ) );
    }

    // --- plumbing --------------------------------------------------------------------

    private static String buildFromExpressionTree( final QueryExpr queryExpr )
    {
        return ConstraintExpressionBuilder.build( queryExpr.getConstraint(), SearchQueryFieldNameResolver.INSTANCE ).toString();
    }

    private static String buildFromRenderedDsl( final QueryExpr queryExpr )
    {
        final PropertyTree dsl = PropertyTree.fromMap( SearchDslRenderer.renderConstraint( queryExpr ) );
        final QueryBuilder builder = ConstraintExpressionBuilder.build( DslExpr.from( dsl ), SearchQueryFieldNameResolver.INSTANCE );
        return builder.toString();
    }

    private static String normalize( final String json )
        throws Exception
    {
        final JsonNode node = MAPPER.readTree( json );
        stripQueryNames( node );
        return MAPPER.writeValueAsString( node ).replaceAll( "\\s+", "" );
    }

    /**
     * Removes {@code _name} and collapses the long forms Elasticsearch only emits in order to
     * attach one — {@code {"term":{"f":{"value":x}}}} and {@code {"wildcard":{"f":{"wildcard":x}}}}
     * are the engine's own synonyms for the scalar forms, and the two builder families disagree
     * about the query name rather than about the query.
     */
    private static void stripQueryNames( final JsonNode node )
    {
        if ( node.isObject() )
        {
            ( (ObjectNode) node ).remove( "_name" );
        }
        for ( final Iterator<JsonNode> it = node.elements(); it.hasNext(); )
        {
            stripQueryNames( it.next() );
        }
        if ( node.isObject() )
        {
            collapse( (ObjectNode) node, "term", "value" );
            collapse( (ObjectNode) node, "wildcard", "wildcard" );
        }
    }

    private static void collapse( final ObjectNode object, final String queryName, final String valueName )
    {
        final JsonNode query = object.get( queryName );
        if ( query == null || !query.isObject() || query.size() != 1 )
        {
            return;
        }
        final String field = query.fieldNames().next();
        final JsonNode body = query.get( field );
        if ( body.isObject() && body.size() == 1 && body.has( valueName ) )
        {
            ( (ObjectNode) query ).set( field, body.get( valueName ) );
        }
    }

    private static String load( final String name )
    {
        try (InputStream stream = NoqlDslRoundTripOracleTest.class.getResourceAsStream( FIXTURES + name ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( "Cannot load fixture [" + FIXTURES + name + "]", e );
        }
    }
}
