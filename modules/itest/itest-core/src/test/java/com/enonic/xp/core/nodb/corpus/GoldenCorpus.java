package com.enonic.xp.core.nodb.corpus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.DateRange;
import com.enonic.xp.query.aggregation.DateRangeAggregationQuery;
import com.enonic.xp.query.aggregation.DistanceRange;
import com.enonic.xp.query.aggregation.GeoDistanceAggregationQuery;
import com.enonic.xp.query.aggregation.HistogramAggregationQuery;
import com.enonic.xp.query.aggregation.NumericRange;
import com.enonic.xp.query.aggregation.NumericRangeAggregationQuery;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.query.aggregation.metric.MaxAggregationQuery;
import com.enonic.xp.query.aggregation.metric.MinAggregationQuery;
import com.enonic.xp.query.aggregation.metric.StatsAggregationQuery;
import com.enonic.xp.query.aggregation.metric.ValueCountAggregationQuery;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.query.highlight.HighlightPropertySettings;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryProperty;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.query.suggester.TermSuggestionQuery;

import static com.enonic.xp.core.nodb.corpus.Acceptance.EXACT;
import static com.enonic.xp.core.nodb.corpus.Acceptance.ICU_DOCUMENTED;
import static com.enonic.xp.core.nodb.corpus.Acceptance.SET;
import static com.enonic.xp.core.nodb.corpus.SourceKind.ADMIN;
import static com.enonic.xp.core.nodb.corpus.SourceKind.DEFAULT_USER;
import static com.enonic.xp.core.nodb.corpus.SourceKind.EMPTY_PRINCIPALS;
import static com.enonic.xp.core.nodb.corpus.SourceKind.MULTI_REPO_BOTH_ALLOWED;
import static com.enonic.xp.core.nodb.corpus.SourceKind.MULTI_REPO_ONE_DENIED;
import static com.enonic.xp.core.nodb.corpus.SourceKind.SECRET_USER;
import static com.enonic.xp.core.nodb.corpus.CorpusDsl.dsl;

/**
 * The golden-query corpus: one data table, no per-query test methods, so adding a row later costs
 * one line. Rows span every family the Gate 0(c) translator-surface inventory enumerated and each
 * carries the acceptance rule it is diffed under (see {@link Acceptance}).
 * <p>
 * Rows whose id starts with {@code GAP-} exist purely to PIN a behaviour the team still has to
 * rule on (gaps G-1..G-5 of Gate 0(a)); whichever way the ruling goes, the artifact says what
 * ES 2.4 does today.
 */
final class GoldenCorpus
{
    /** Big enough that every corpus query returns all of its hits unless it is a paging row. */
    private static final int SIZE = 50;

    private static final String CITIES = "_parentPath = '/cities'";

    private GoldenCorpus()
    {
    }

    static List<GoldenQuery> all()
    {
        final List<GoldenQuery> rows = new ArrayList<>();

        // ---------------------------------------------------------------- term / compare
        q( rows, "TERM-01-string", "term", EXACT, "term on a string field", //
           () -> noql( "category = 'c1'" ) );
        q( rows, "TERM-02-value-mixed-case", "term", EXACT,
           "term whose VALUE carries upper case against a mixed-case indexed title (rule 4: trim+lowercase both sides)",
           () -> noql( "title = 'BERGEN'" ) );
        q( rows, "TERM-03-field-mixed-case", "term", EXACT,
           "term whose FIELD NAME carries upper case (rule 1: IndexPath lowercases and trims every path)",
           () -> noql( "MixedCaseField = 'mixedcasevalue'" ) );
        q( rows, "TERM-04-number", "term", EXACT, "numeric term -> ._number sub-field, value always Double",
           () -> noql( "population = 280000" ) );
        q( rows, "TERM-05-instant", "term", EXACT, "dated term -> ._datetime sub-field",
           () -> noql( "founded = instant('2020-01-15T00:00:00Z')" ) );
        q( rows, "TERM-06-boolean", "term", EXACT,
           "boolean term. NoQL has NO bare boolean literal (an unquoted `true` is parsed as a function name), so the value must be quoted; the base string field holds \"true\"/\"false\"",
           () -> noql( "active = 'true'" ) );
        q( rows, "TERM-07-neq", "term", EXACT, "!= renders as bool.mustNot on both families", //
           () -> noql( "category != 'c1'" ) );
        q( rows, "TERM-08-gt-number", "compare", EXACT, "numeric > (range on ._number)", //
           () -> noql( "population > 500000" ) );
        q( rows, "TERM-09-lte-instant", "compare", EXACT, "dated <= (range on ._datetime)",
           () -> noql( "founded <= instant('2020-06-01T00:00:00Z')" ) );
        q( rows, "TERM-10-special-name", "term", EXACT, "_name is an ordinary compare, no special casing", //
           () -> noql( "_name = 'city-bergen'" ) );
        q( rows, "TERM-11-special-path", "term", EXACT, "_path is an ordinary compare", //
           () -> noql( "_path = '/cities/city-bergen'" ) );
        q( rows, "TERM-12-special-id", "term", EXACT, "_id compare (ids exists as a query only via _id, never as a query type)",
           () -> noql( "_id = 'city-oslo'" ) );
        q( rows, "TERM-13-special-parentpath", "term", EXACT, "_parentPath compare, the shape almost every XP query uses",
           () -> noql( CITIES ) );
        q( rows, "TERM-14-numish-as-string", "term", EXACT,
           "same logical field queried as STRING: matches both the string \"42\" and the long 42 (both land on the base field)",
           () -> noql( "numish = '42'" ) );
        q( rows, "TERM-15-numish-as-number", "term", EXACT,
           "same logical field queried as NUMBER: only the node that stored a long has a ._number sub-field",
           () -> noql( "numish = 42" ) );

        // ---------------------------------------------------------------- in / like
        q( rows, "IN-01-string", "in", EXACT, "IN over strings: N should-term clauses, NOT a terms query", //
           () -> noql( "category IN ('c1','c3')" ) );
        q( rows, "GAP-G2-in-number", "in", EXACT,
           "G-2, the widest case: IN with NUMERIC values. IN resolves to the base STRING field but ValueHelper still converts the value to a Double, so the ES term is 700000.0 against a string field -- matches NOTHING. The gap is NOT limited to dates and geo points",
           true, () -> noql( "population IN (280000, 700000)" ) );
        q( rows, "IN-02b-number-as-string", "in", EXACT,
           "the same IN written with STRING literals DOES match: proof that only the value conversion is broken, not the field resolution",
           () -> noql( "population IN ('280000', '700000')" ) );
        q( rows, "GAP-G2-in-dated", "in", EXACT,
           "G-2: IN with a dated value. IN forces the base string field, so this is expected to match NOTHING today -- a latent bug the team must rule on",
           true, () -> noql( "founded IN (instant('2020-01-15T00:00:00Z'))" ) );
        q( rows, "GAP-G2-in-geo", "in", EXACT,
           "G-2 contrast: IN with geo points written as STRING literals. These DO match, because a quoted NoQL literal never becomes a geo-typed Value -- which is exactly why the existing geoPoint itest passes and the gap stayed invisible. Note the literal must be GeoPoint#toString's canonical form (trailing zeros stripped)",
           () -> noql( "location IN ('59.91273,10.74609','60.39299,5.32415')" ) );
        q( rows, "IN-03-not-in", "in", EXACT, "NOT IN -- present in the expression tree, absent from the DSL family", //
           () -> noql( "category NOT IN ('c1')" ) );
        q( rows, "LIKE-01-prefix", "like", EXACT, "LIKE with a trailing wildcard on the base string field", //
           () -> noql( "title LIKE 'ber*'" ) );
        q( rows, "LIKE-02-value-mixed-case", "like", EXACT, "LIKE value carrying upper case -- pins LIKE's own value normalization",
           () -> noql( "title LIKE 'BER*'" ) );
        q( rows, "LIKE-03-not-like", "like", EXACT, "NOT LIKE -- expression-tree only", //
           () -> noql( "_name NOT LIKE 'city-*'" ) );

        // ---------------------------------------------------------------- range() function
        q( rows, "RANGE-01-number", "range", EXACT, "range() inferring numeric bounds", //
           () -> noql( "range('population', 200000, 700000)" ) );
        q( rows, "RANGE-02-instant", "range", EXACT, "range() with instant bounds", //
           () -> noql( "range('founded', instant('2020-01-01T00:00:00Z'), instant('2021-01-01T00:00:00Z'))" ) );
        q( rows, "RANGE-03-string-lowercase", "range", EXACT, "range() over lowercase string bounds (what today's itests all do)",
           () -> noql( "range('_name', 'city-a', 'city-z')" ) );
        q( rows, "GAP-G4-range-raw-case", "range", EXACT,
           "G-4: range() string bounds are used RAW while the DSL normalizes them. Upper-case bounds are invisible in current itests -- this row pins whichever behavior ships",
           true, () -> noql( "range('title', 'Bergen', 'Oslo')" ) );
        q( rows, "RANGE-04-open-upper", "range", EXACT, "range() with an empty upper bound (half-open)", //
           () -> noql( "range('population', 500000, '')" ) );
        q( rows, "GAP-G5-range-both-empty", "range", EXACT,
           "G-5: range() with BOTH bounds empty; the renderer is expected to rewrite this to exists", //
           () -> noql( "range('title', '', '')" ) );
        q( rows, "RANGE-05-exclusive-bounds", "range", EXACT, "range() 5-arg form with explicit inclusive/exclusive flags",
           () -> noql( "range('population', 280000, 700000, 'false', 'false')" ) );

        // ---------------------------------------------------------------- boolean / not / matchAll
        q( rows, "BOOL-01-and", "boolean", EXACT, "AND", //
           () -> noql( CITIES + " AND category = 'c1'" ) );
        q( rows, "BOOL-02-or", "boolean", EXACT, "OR", //
           () -> noql( "category = 'c3' OR category = 'c2'" ) );
        q( rows, "BOOL-03-bare-not", "boolean", EXACT, "bare NOT over an expression -> bool.mustNot", //
           () -> noql( CITIES + " AND NOT category = 'c1'" ) );
        q( rows, "BOOL-04-left-assoc-chain", "boolean", EXACT,
           "a AND b AND c: pins PAIRWISE LEFT-ASSOCIATIVE nesting. Flattening to must:[a,b,c] is set-equivalent but not score-equivalent",
           () -> noql( CITIES + " AND active = 'true' AND population > 100000" ) );
        q( rows, "BOOL-05-parens-mixed", "boolean", EXACT, "explicit parens mixing OR inside AND NOT", //
           () -> noql( "(category = 'c1' OR category = 'c2') AND NOT active = 'false'" ) );
        q( rows, "MATCHALL-01", "matchAll", EXACT, "no query at all -> matchAll, GET_ALL size", //
           () -> NodeQuery.create().size( -1 ).build() );
        q( rows, "MATCHALL-02-dsl", "matchAll", EXACT, "explicit DSL matchAll", //
           () -> NodeQuery.create().query( QueryExpr.from( dsl( "{\"matchAll\":{}}" ) ) ).size( SIZE ).build() );

        // ---------------------------------------------------------------- filters
        q( rows, "FILTER-01-ids", "filter", EXACT, "ids exists only as a FILTER, never as a query type", //
           () -> NodeQuery.create()
               .addQueryFilter( IdFilter.create().values( NodeIds.from( "city-oslo", "city-berlin", "city-malmo" ) ).build() )
               .size( SIZE )
               .build() );
        q( rows, "FILTER-02-value", "filter", EXACT, "ValueFilter (lowercases its values, unlike IdFilter)", //
           () -> NodeQuery.create()
               .addQueryFilter( ValueFilter.create().fieldName( "category" ).addValues( "c2" ).build() )
               .size( SIZE )
               .build() );
        q( rows, "FILTER-03-exists", "filter", EXACT, "ExistsFilter forces the base STRING field regardless of value type",
           () -> NodeQuery.create().addQueryFilter( ExistsFilter.create().fieldName( "location" ).build() ).size( SIZE ).build() );
        q( rows, "FILTER-04-boolean", "filter", EXACT, "BooleanFilter must + mustNot", //
           () -> NodeQuery.create()
               .addQueryFilter( BooleanFilter.create()
                                    .must( ValueFilter.create().fieldName( "_parentPath" ).addValues( "/cities" ).build() )
                                    .mustNot( ValueFilter.create().fieldName( "category" ).addValues( "c1" ).build() )
                                    .build() )
               .size( SIZE )
               .build() );
        q( rows, "FILTER-05-range", "filter", EXACT, "RangeFilter over the numeric sub-field", //
           () -> NodeQuery.create()
               .addQueryFilter( RangeFilter.create()
                                    .fieldName( "population" )
                                    .from( com.enonic.xp.data.ValueFactory.newDouble( 200000d ) )
                                    .to( com.enonic.xp.data.ValueFactory.newDouble( 400000d ) )
                                    .build() )
               .size( SIZE )
               .build() );
        q( rows, "FILTER-06-post-filter", "filter", EXACT,
           "postFilter must stay in its own setPostFilter slot: it narrows the hits but NOT the aggregation buckets",
           () -> NodeQuery.create()
               .query( QueryParser.parse( CITIES ) )
               .addPostFilter( ValueFilter.create().fieldName( "category" ).addValues( "c1" ).build() )
               .addAggregationQuery( TermsAggregationQuery.create( "byCategory" )
                                         .fieldName( "category" )
                                         .orderDirection( TermsAggregationQuery.Direction.DESC )
                                         .orderType( TermsAggregationQuery.Type.DOC_COUNT )
                                         .build() )
               .size( SIZE )
               .build() );

        // ---------------------------------------------------------------- DSL family / field resolution
        q( rows, "DSL-01-term-number", "dsl", EXACT, "DSL term with a JSON number -> ._number (the DSL's only implicit typing rule)",
           () -> dslQuery( "{\"term\":{\"field\":\"population\",\"value\":280000}}" ) );
        q( rows, "DSL-02-term-explicit-datetime", "dsl", EXACT,
           "DSL term with an EXPLICIT type:dateTime -> ._datetime. The NoQL->DSL renderer MUST emit this for every dated AST value",
           () -> dslQuery( "{\"term\":{\"field\":\"founded\",\"type\":\"dateTime\",\"value\":\"2020-01-15T00:00:00Z\"}}" ) );
        q( rows, "DSL-03-term-datetime-without-type", "dsl", EXACT,
           "the SAME value WITHOUT type:dateTime: hits the base string field instead. This row is the rule-3 divergence made visible",
           true, () -> dslQuery( "{\"term\":{\"field\":\"founded\",\"value\":\"2020-01-15T00:00:00Z\"}}" ) );
        q( rows, "GAP-G1-dsl-term-geopoint", "dsl", EXACT,
           "G-1: geoPoint-typed value in a term query (unit-tested today, no NoQL form) -> ._geopoint",
           true, () -> dslQuery( "{\"term\":{\"field\":\"location\",\"type\":\"geoPoint\",\"value\":\"59.9127300,10.7460900\"}}" ) );
        q( rows, "GAP-G1-dsl-in-geopoint", "dsl", EXACT,
           "G-1 again, in the in form: also rejected outright. ExpressionQueryBuilder's type switch accepts ONLY dateTime and time",
           true, () -> dslQuery(
               "{\"in\":{\"field\":\"location\",\"type\":\"geoPoint\",\"values\":[{\"lat\":59.91273,\"lon\":10.74609}]}}" ) );
        q( rows, "GAP-G1-noql-term-geopoint", "term", EXACT,
           "G-1, the decisive row: the EXPRESSION-TREE family DOES resolve a geo-typed Value to ._geopoint and builds term{location._geopoint:{lat,lon}} -- and ELASTICSEARCH ITSELF REJECTS IT. So a geoPoint term query works in neither family today; G-1 is not 'mechanical', it is unimplemented end to end",
           true, () -> NodeQuery.create()
               .query( QueryExpr.from( com.enonic.xp.query.expr.CompareExpr.eq(
                   com.enonic.xp.query.expr.FieldExpr.from( "location" ),
                   com.enonic.xp.query.expr.ValueExpr.geoPoint( "59.91273,10.74609" ) ) ) )
               .size( SIZE )
               .withPath( true )
               .build() );
        q( rows, "GAP-G2-dsl-in-dated", "dsl", EXACT,
           "G-2 in DSL form: in + type:dateTime. Compare with GAP-G2-in-dated -- the two families disagree",
           true, () -> dslQuery(
               "{\"in\":{\"field\":\"founded\",\"type\":\"dateTime\",\"values\":[\"2020-01-15T00:00:00Z\",\"2020-02-20T00:00:00Z\"]}}" ) );
        q( rows, "DSL-04-exists", "dsl", EXACT, "DSL exists (no NoQL form exists at all)", //
           () -> dslQuery( "{\"exists\":{\"field\":\"numish\"}}" ) );
        q( rows, "DSL-05-boolean-filter-clause", "dsl", EXACT, "the DSL family's own filter clause -- the expression tree has no equivalent",
           () -> dslQuery( "{\"boolean\":{\"must\":[{\"term\":{\"field\":\"_parentPath\",\"value\":\"/cities\"}}]," +
                               "\"filter\":[{\"term\":{\"field\":\"category\",\"value\":\"c2\"}}]}}" ) );
        q( rows, "DSL-06-range-normalized-bounds", "dsl", EXACT,
           "DSL range with upper-case string bounds. Contrast with GAP-G4-range-raw-case: the DSL normalizes, range() does not",
           () -> dslQuery( "{\"range\":{\"field\":\"title\",\"gte\":\"Bergen\",\"lte\":\"Oslo\"}}" ) );

        // ---------------------------------------------------------------- fulltext / ngram / stemmed
        q( rows, "TEXT-01-fulltext-and", "fulltext", SET, "fulltext AND over one field", //
           () -> noql( "fulltext('description', 'fisk laks', 'AND')" ) );
        q( rows, "TEXT-02-fulltext-or", "fulltext", SET, "fulltext OR over one field", //
           () -> noql( "fulltext('description', 'laks reker', 'OR')" ) );
        q( rows, "TEXT-03-fulltext-weighted", "fulltext", SET, "weighted multi-field fulltext (title^5, description)", //
           () -> noql( "fulltext('title^5, description', 'bergen fisk', 'OR')" ) );
        q( rows, "TEXT-04-fulltext-weighted-flipped", "fulltext", SET,
           "the same two fields with the weight moved -- changes ORDER only, which is exactly what the SET rule tolerates",
           () -> noql( "fulltext('title, description^5', 'bergen fisk', 'OR')" ) );
        q( rows, "TEXT-05-fulltext-alltext", "fulltext", SET, "_allText fulltext (needs the all-text index config)", //
           () -> noql( "fulltext('_allText', 'grønnsaker', 'OR')" ) );
        q( rows, "TEXT-06-fulltext-ascii-folding", "fulltext", SET, "ASCII folding: 'gronnsaker' must find 'grønnsaker'", //
           () -> noql( "fulltext('_allText', 'gronnsaker', 'OR')" ) );
        q( rows, "TEXT-07-fulltext-wildcard-field", "fulltext", SET, "wildcard field expression in fulltext", //
           () -> noql( "fulltext('descri*', 'fisk', 'OR')" ) );
        q( rows, "GAP-G3-fulltext-4arg-analyzer", "fulltext", SET,
           "G-3: the 4-argument fulltext form (custom analyzer). Tested in unit tests, ZERO call sites in the repo -- pin it or declare it unsupported",
           true, () -> noql( "fulltext('description', 'fisk', 'OR', 'norwegian')" ) );
        q( rows, "TEXT-08-ngram-and", "ngram", SET, "edge-ngram AND", //
           () -> noql( "ngram('description', 'fis lak', 'AND')" ) );
        q( rows, "TEXT-09-ngram-one-char", "ngram", SET, "single-character ngram -- the min_gram boundary", //
           () -> noql( "ngram('title', 'b', 'AND')" ) );
        q( rows, "TEXT-10-ngram-fuzzy", "ngram", SET, "fuzziness inside an ngram query", //
           () -> noql( "ngram('description', 'grunnsaker~2', 'OR')" ) );
        q( rows, "TEXT-11-stemmed-alltext-no", "stemmed", SET, "Norwegian stemmer over _allText with a wildcard", //
           () -> noql( "stemmed('_allText', 'grønnsake*', 'AND', 'no')" ) );
        q( rows, "TEXT-12-stemmed-field-no", "stemmed", SET, "Norwegian stemmer over a single field (._stemmed_no)", //
           () -> noql( "stemmed('description', 'fisker', 'OR', 'no')" ) );
        q( rows, "TEXT-13-ngram-and-not-fulltext", "boolean", SET, "mixed families in one boolean: ngram AND NOT fulltext", //
           () -> noql( "ngram('description', 'fis', 'AND') AND NOT fulltext('description', 'laks', 'OR')" ) );
        q( rows, "TEXT-14-score-order-explicit", "fulltext", SET, "explicit ORDER BY _score DESC (pass-through order field)", //
           () -> noql( "fulltext('description', 'fisk grønnsaker', 'OR') ORDER BY _score DESC" ) );

        // ---------------------------------------------------------------- pathMatch
        q( rows, "PATH-01-pathmatch", "pathMatch", SET, "pathMatch without minimumMatch: score order, most-matching first", //
           () -> noql( "pathMatch('_path', '/tree/a/b/c/d')" ) );
        q( rows, "PATH-02-pathmatch-min", "pathMatch", SET, "pathMatch with minimumMatch = 3", //
           () -> noql( "pathMatch('_path', '/tree/a/b/c/d', 3)" ) );
        q( rows, "PATH-03-like-path", "like", EXACT, "_path LIKE prefix -- the deterministic alternative to pathMatch", //
           () -> noql( "_path LIKE '/tree/a*' ORDER BY _path ASC" ) );

        // ---------------------------------------------------------------- sorts
        q( rows, "SORT-01-number-desc", "sort", EXACT, "numeric _orderby (17-char hex lexi-sortable encoding)", //
           () -> noql( CITIES + " ORDER BY population DESC" ) );
        q( rows, "SORT-02-instant-asc", "sort", EXACT, "date _orderby (fixed UTC pattern)", //
           () -> noql( CITIES + " ORDER BY founded ASC" ) );
        q( rows, "SORT-03-string-asc", "sort", EXACT, "plain string _orderby, NO collation -- lowercased, truncated at 1024", //
           () -> noql( "_parentPath = '/collation' ORDER BY word ASC" ) );
        q( rows, "SORT-04-path-desc", "sort", EXACT, "_path DESC -- the shape DeleteNodeCommand's cascade uses", //
           () -> noql( "_path LIKE '/tree*' ORDER BY _path DESC" ) );
        q( rows, "SORT-05-multi-three-keys", "sort", EXACT, "three sort keys of three different types", //
           () -> noql( CITIES + " ORDER BY category ASC, active DESC, population ASC" ) );
        q( rows, "SORT-06-manual", "sort", EXACT, "_manualOrderValue -- the manual/child-order sort", //
           () -> noql( "_parentPath = '/page' ORDER BY _manualOrderValue ASC" ) );
        q( rows, "SORT-07-timestamp", "sort", EXACT, "_ts ordering (values are NOT recorded as sort keys are; ids are)", //
           () -> noql( CITIES + " ORDER BY _name ASC" ) );
        q( rows, "SORT-08-geodistance", "sort", EXACT, "geoDistance sort -- deterministic, so EXACT despite being a dynamic order",
           () -> noql( CITIES + " ORDER BY geoDistance('location', '59.9127300,10.7460900')" ) );
        q( rows, "SORT-09-geodistance-desc", "sort", EXACT, "geoDistance sort, descending", //
           () -> noql( CITIES + " ORDER BY geoDistance('location', '59.9127300,10.7460900') DESC" ) );
        q( rows, "SORT-10-dsl-order", "sort", EXACT, "DSL sort form (field + direction as a PropertyTree)", //
           () -> NodeQuery.create()
               .query( QueryExpr.from( QueryParser.parseCostraintExpression( CITIES ),
                                       CorpusDsl.dslOrder( "{\"field\":\"population\",\"direction\":\"ASC\"}" ) ) )
               .size( SIZE )
               .build() );

        // ---------------------------------------------------------------- ICU collation sorts
        q( rows, "ICU-01-collate-nb", "icuSort", ICU_DOCUMENTED, "COLLATE nb -> ._orderby_no (æ < ø < å)", //
           () -> noql( "_parentPath = '/collation' ORDER BY word COLLATE nb ASC" ) );
        q( rows, "ICU-02-collate-no", "icuSort", ICU_DOCUMENTED,
           "COLLATE no -> the SAME ._orderby_no field (rule 5's no->nb normalization). Must equal ICU-01 exactly",
           () -> noql( "_parentPath = '/collation' ORDER BY word COLLATE no ASC" ) );
        q( rows, "ICU-03-collate-sv", "icuSort", ICU_DOCUMENTED, "COLLATE sv -> ._orderby_sv (å < ä < ö)", //
           () -> noql( "_parentPath = '/collation' ORDER BY word COLLATE sv ASC" ) );
        q( rows, "ICU-04-collate-da-desc", "icuSort", ICU_DOCUMENTED, "COLLATE da descending", //
           () -> noql( "_parentPath = '/collation' ORDER BY word COLLATE da DESC" ) );
        q( rows, "ICU-05-collate-de-ducet", "icuSort", ICU_DOCUMENTED,
           "COLLATE de: IndexLanguageController maps German's order-by to DUCET, so this resolves to ._orderby_ducet, NOT ._orderby_de",
           () -> noql( "_parentPath = '/collation' ORDER BY word COLLATE de ASC" ) );
        q( rows, "ICU-06-collate-unknown-locale", "icuSort", ICU_DOCUMENTED, "an unmapped locale must fall back to DUCET, not fail",
           () -> noql( "_parentPath = '/collation' ORDER BY word COLLATE xx ASC" ) );

        // ---------------------------------------------------------------- paging
        q( rows, "PAGE-01-from0-size5", "paging", EXACT, "first page", //
           () -> pageQuery( 0, 5 ) );
        q( rows, "PAGE-02-from5-size5", "paging", EXACT, "second page -- from is honoured", //
           () -> pageQuery( 5, 5 ) );
        q( rows, "PAGE-03-from20-size10", "paging", EXACT, "last, partial page", //
           () -> pageQuery( 20, 10 ) );
        q( rows, "PAGE-04-size0-count-only", "paging", EXACT, "size = 0: totalHits only, zero hits returned", //
           true, () -> pageQuery( 0, 0 ) );
        q( rows, "PAGE-05-size-all-sorted", "paging", EXACT,
           "size = -1 (GET_ALL) WITH a sort: takes the scroll/search_after path, batchSize 7, no _doc sort injected. Records the maxScore-from-the-final-empty-page quirk",
           () -> NodeQuery.create()
               .query( QueryParser.parse( "_parentPath = '/page' ORDER BY seq ASC" ) )
               .size( -1 )
               .batchSize( 7 )
               .build() );
        q( rows, "PAGE-06-size-all-unsorted", "paging", EXACT,
           "size = -1 with NO caller sort: the scroll path adds sort:_doc itself. maxScore is expected to be NaN",
           () -> NodeQuery.create().query( QueryParser.parse( "_parentPath = '/page'" ) ).size( -1 ).batchSize( 7 ).build() );

        // ---------------------------------------------------------------- aggregations
        q( rows, "AGG-01-terms", "aggregation", EXACT, "terms aggregation ordered by doc count DESC", //
           () -> agg( TermsAggregationQuery.create( "byCategory" )
                          .fieldName( "category" )
                          .orderDirection( TermsAggregationQuery.Direction.DESC )
                          .orderType( TermsAggregationQuery.Type.DOC_COUNT )
                          .build() ) );
        q( rows, "AGG-02-terms-key-asc-size", "aggregation", EXACT, "terms ordered by TERM ASC with an explicit size", //
           () -> agg( TermsAggregationQuery.create( "byCategoryKey" )
                          .fieldName( "category" )
                          .orderDirection( TermsAggregationQuery.Direction.ASC )
                          .orderType( TermsAggregationQuery.Type.TERM )
                          .size( 2 )
                          .build() ) );
        q( rows, "AGG-03-terms-sub-stats", "aggregation", EXACT,
           "SUB-AGGREGATION: stats nested under terms. AbstractAggregationBuilder (today's sub-agg discriminator) is gone in OpenSearch, so sub-agg support becomes an explicit per-type rule",
           () -> agg( TermsAggregationQuery.create( "byCategory" )
                          .fieldName( "category" )
                          .orderDirection( TermsAggregationQuery.Direction.ASC )
                          .orderType( TermsAggregationQuery.Type.TERM )
                          .addSubQuery( StatsAggregationQuery.create( "popStats" ).fieldName( "population" ).build() )
                          .build() ) );
        q( rows, "AGG-04-stats", "aggregation", EXACT, "stats metric aggregation", //
           () -> agg( StatsAggregationQuery.create( "popStats" ).fieldName( "population" ).build() ) );
        q( rows, "AGG-05-min", "aggregation", EXACT, "min metric aggregation", //
           () -> agg( MinAggregationQuery.create( "popMin" ).fieldName( "population" ).build() ) );
        q( rows, "AGG-06-max", "aggregation", EXACT, "max metric aggregation", //
           () -> agg( MaxAggregationQuery.create( "popMax" ).fieldName( "population" ).build() ) );
        q( rows, "AGG-07-value-count", "aggregation", EXACT, "value-count metric aggregation", //
           () -> agg( ValueCountAggregationQuery.create( "popCount" ).fieldName( "population" ).build() ) );
        q( rows, "AGG-08-numeric-range", "aggregation", EXACT, "numeric-range aggregation, three ranges incl. two open-ended",
           () -> agg( NumericRangeAggregationQuery.create( "popRanges" )
                          .fieldName( "population" )
                          .addRange( NumericRange.create().to( 100000d ).build() )
                          .addRange( NumericRange.create().from( 100000d ).to( 500000d ).build() )
                          .addRange( NumericRange.create().from( 500000d ).build() )
                          .build() ) );
        q( rows, "AGG-09-date-range", "aggregation", EXACT, "date-range aggregation over the pre-encoded ._datetime field", //
           () -> agg( DateRangeAggregationQuery.create( "foundedRanges" )
                          .fieldName( "founded" )
                          .addRange( DateRange.create().to( "2021-01-01T00:00:00Z" ).build() )
                          .addRange( DateRange.create().from( "2021-01-01T00:00:00Z" ).build() )
                          .build() ) );
        q( rows, "AGG-10-histogram", "aggregation", EXACT, "numeric histogram, interval 100000, minDocCount 1", //
           () -> agg( HistogramAggregationQuery.create( "popHistogram" )
                          .fieldName( "population" )
                          .interval( 100000L )
                          .minDocCount( 1L )
                          .order( HistogramAggregationQuery.Order.KEY_ASC )
                          .build() ) );
        q( rows, "AGG-11-date-histogram-1M", "aggregation", EXACT,
           "date-histogram with interval string \"1M\". DateHistogramInterval/.interval() are gone in OpenSearch, so the interval string recorded here is what the calendar_interval-vs-fixed_interval rule must reproduce",
           () -> agg( DateHistogramAggregationQuery.create( "foundedByMonth" )
                          .fieldName( "founded" )
                          .interval( "1M" )
                          .minDocCount( 1L )
                          .build() ) );
        q( rows, "AGG-12-date-histogram-1d-fixed", "aggregation", EXACT,
           "date-histogram with interval \"1d\" -- a FIXED interval in OpenSearch terms, unlike \"1M\". Two rows so the rule can be validated in both directions",
           () -> agg( DateHistogramAggregationQuery.create( "foundedByDay" )
                          .fieldName( "founded" )
                          .interval( "1d" )
                          .minDocCount( 1L )
                          .build() ) );
        q( rows, "AGG-13-geo-distance", "aggregation", EXACT, "geo-distance aggregation from Oslo, three km ranges", //
           () -> agg( GeoDistanceAggregationQuery.create( "distanceFromOslo" )
                          .fieldName( "location" )
                          .origin( CorpusFixture.ORIGIN )
                          .unit( "km" )
                          .addRange( DistanceRange.create().to( 100d ).build() )
                          .addRange( DistanceRange.create().from( 100d ).to( 1000d ).build() )
                          .addRange( DistanceRange.create().from( 1000d ).build() )
                          .build() ) );

        // ---------------------------------------------------------------- suggest / highlight
        q( rows, "SUGGEST-01-term", "suggest", SET,
           "term suggester. Note \"jarowinkler\" was renamed jaro_winkler in OpenSearch -- an XP-API-visible wire value needing a compat mapping",
           () -> NodeQuery.create()
               .query( QueryParser.parse( CITIES ) )
               .addSuggestionQuery( TermSuggestionQuery.create( "descSuggest" ).field( "description" ).text( "fsk" ).build() )
               .size( SIZE )
               .build() );
        q( rows, "SUGGEST-02-term-freq-sorted", "suggest", SET, "term suggester with explicit sort/suggestMode/stringDistance",
           () -> NodeQuery.create()
               .query( QueryParser.parse( CITIES ) )
               .addSuggestionQuery( TermSuggestionQuery.create( "descSuggest" )
                                        .field( "description" )
                                        .text( "laksx" )
                                        .sort( TermSuggestionQuery.Sort.FREQUENCY )
                                        .suggestMode( TermSuggestionQuery.SuggestMode.ALWAYS )
                                        .stringDistance( TermSuggestionQuery.StringDistance.JAROWINKLER )
                                        .build() )
               .size( SIZE )
               .build() );
        q( rows, "HIGHLIGHT-01-raw-field", "highlight", EXACT,
           "highlight requested on 'title'. XP expands it to THREE fields (title, title._analyzed, title._ngram) and forces type:plain; the response strips the postfix so all three collapse onto ONE property name",
           true, () -> NodeQuery.create()
               .query( QueryParser.parse( "title = 'oslo'" ) )
               .highlight( HighlightQuery.create()
                               .property( HighlightQueryProperty.create( "title" )
                                              .settings( HighlightPropertySettings.create().numOfFragments( 3 ).build() )
                                              .build() )
                               .build() )
               .size( SIZE )
               .build() );
        q( rows, "HIGHLIGHT-02-analyzed-variant", "highlight", SET,
           "the same highlight against a FULLTEXT query, i.e. the match is on description._analyzed/._ngram. OpenSearch flips require_field_match false->true while XP only sends it when non-null",
           true, () -> NodeQuery.create()
               .query( QueryParser.parse( "fulltext('description', 'fisk', 'OR')" ) )
               .highlight( HighlightQuery.create()
                               .property( HighlightQueryProperty.create( "description" )
                                              .settings( HighlightPropertySettings.create()
                                                             .numOfFragments( 2 )
                                                             .requireFieldMatch( false )
                                                             .build() )
                                              .build() )
                               .build() )
               .size( SIZE )
               .build() );

        // ------------------------------------------- the bare/untyped STRING field (Gate 0(d) decision 2)
        // ES's term query NEVER analyses its argument, so these six rows discriminate every
        // candidate mapping for the bare field. Under a keyword-style analyzer (whole value = one
        // lowercased token) UNTYPED-01/03/04 match and UNTYPED-02 does not; under a word-splitting
        // analyzer it is exactly the other way round. Whatever the baseline says is the evidence
        // Gate A must reproduce -- a bare `keyword` with no lowercase normalizer cannot.
        q( rows, "UNTYPED-01-exact-multiword", "untypedString", EXACT,
           "exact term match on a MULTI-WORD untyped string value: matches only if the whole value is one token",
           true, () -> noql( "phrase = 'Oslo Sentrum Vest'" ) );
        q( rows, "UNTYPED-02-single-token-of-multiword", "untypedString", EXACT,
           "single-token term against the same multi-word value: matches only if the value was split into words",
           true, () -> noql( "phrase = 'oslo'" ) );
        q( rows, "UNTYPED-03-case-differing", "untypedString", EXACT,
           "term differing from the stored value ONLY in case: matches only if lowercasing happens on BOTH sides",
           true, () -> noql( "phrase = 'mixed case value'" ) );
        q( rows, "UNTYPED-04-punctuation-hyphenation", "untypedString", EXACT,
           "exact term on a value with hyphenation and punctuation: matches only if punctuation is preserved verbatim",
           true, () -> noql( "phrase = 'levenshtein-algoritme (v2.1)'" ) );
        q( rows, "UNTYPED-05-ascii-folding", "untypedString", EXACT,
           "ASCII-folded term against a non-ASCII value: shows whether folding is in play on the bare field (it is in default_search but not in keywordlowercase)",
           true, () -> noql( "phrase = 'alesund sentrum'" ) );
        q( rows, "UNTYPED-06-single-word-control", "untypedString", EXACT,
           "control row: a single-word value matched exactly. This must match under EVERY candidate mapping",
           true, () -> noql( "phrase = 'singleword'" ) );
        q( rows, "UNTYPED-07-ignore-above", "untypedString", EXACT,
           "a 4000-character value against the bare field's ignore_above (3072): expected NOT to be indexed there at all",
           true, () -> noql( "phrase = '" + "z".repeat( 4000 ) + "'" ) );
        q( rows, "UNTYPED-08-analyzed-sibling", "untypedString", SET,
           "the same field via fulltext, i.e. phrase._analyzed. Contrast with UNTYPED-02: the analyzed sibling has no ignore_above and does split words",
           () -> noql( "fulltext('phrase', 'oslo', 'OR')" ) );
        q( rows, "UNTYPED-09-like-prefix", "untypedString", EXACT,
           "LIKE prefix on the bare field: wildcard queries see the stored TOKEN, so this pins tokenization too",
           true, () -> noql( "phrase LIKE 'oslo*'" ) );
        q( rows, "UNTYPED-10-order-by", "untypedString", EXACT,
           "sorting the same field uses ._orderby, never the bare field -- the control that separates the two concerns",
           () -> noql( "_parentPath = '/untyped' ORDER BY phrase ASC" ) );

        // ---------------------------------------------------------------- ACL / sources
        acl( rows, "ACL-01-default-user", DEFAULT_USER, EXACT,
             "the ordinary test user sees /acl/acl-public only -- acl-secret and acl-nobody are filtered out", false );
        acl( rows, "ACL-02-admin-sees-all", ADMIN, EXACT,
             "role:system.admin. TODAY AclFilterBuilderFactory returns null and applies NO filter, so admin sees all three. DESIGN §7.2 replaces that with an injected read-key -- this is the \"admin sees everything ES-admin saw\" row", false );
        acl( rows, "ACL-03-empty-principals", EMPTY_PRINCIPALS, EXACT,
             "an EMPTY principal set must stay fail-closed (resolve to user:system:anonymous), never match-all", true );
        acl( rows, "ACL-04-secret-user", SECRET_USER, EXACT, "the corpus secret user sees only /acl/acl-secret", false );

        q( rows, "SOURCE-01-multi-repo", "multiRepo", EXACT,
           "two sources over two repositories with PER-SOURCE principals; hits must carry repo/branch attribution",
           MULTI_REPO_BOTH_ALLOWED, false, () -> NodeQuery.create().parent( com.enonic.xp.node.NodePath.ROOT ).size( SIZE ).build() );
        q( rows, "SOURCE-02-multi-repo-one-denied", "multiRepo", EXACT,
           "same two sources, but source 2's principals cannot read in repo B: proves the ACL filter is applied PER INDEX, not globally",
           MULTI_REPO_ONE_DENIED, false, () -> NodeQuery.create().parent( com.enonic.xp.node.NodePath.ROOT ).size( SIZE ).build() );

        return List.copyOf( rows );
    }

    // ------------------------------------------------------------------ table helpers

    private static void q( final List<GoldenQuery> rows, final String id, final String family, final Acceptance acceptance,
                           final String intent, final Supplier<NodeQuery> query )
    {
        q( rows, id, family, acceptance, intent, DEFAULT_USER, false, query );
    }

    private static void q( final List<GoldenQuery> rows, final String id, final String family, final Acceptance acceptance,
                           final String intent, final boolean allowEmpty, final Supplier<NodeQuery> query )
    {
        q( rows, id, family, acceptance, intent, DEFAULT_USER, allowEmpty, query );
    }

    private static void q( final List<GoldenQuery> rows, final String id, final String family, final Acceptance acceptance,
                           final String intent, final SourceKind source, final boolean allowEmpty, final Supplier<NodeQuery> query )
    {
        rows.add( new GoldenQuery( id, family, acceptance, source, intent, allowEmpty, query ) );
    }

    private static void acl( final List<GoldenQuery> rows, final String id, final SourceKind source, final Acceptance acceptance,
                             final String intent, final boolean allowEmpty )
    {
        q( rows, id, "acl", acceptance, intent, source, allowEmpty,
           () -> noql( "_parentPath = '/acl' ORDER BY _name ASC" ) );
    }

    private static NodeQuery noql( final String noql )
    {
        return NodeQuery.create().query( QueryParser.parse( noql ) ).size( SIZE ).withPath( true ).build();
    }

    private static NodeQuery dslQuery( final String json )
    {
        return NodeQuery.create().query( QueryExpr.from( dsl( json ) ) ).size( SIZE ).withPath( true ).build();
    }

    private static NodeQuery pageQuery( final int from, final int size )
    {
        return NodeQuery.create().query( QueryParser.parse( "_parentPath = '/page' ORDER BY seq ASC" ) ).from( from ).size( size ).build();
    }

    private static NodeQuery agg( final com.enonic.xp.query.aggregation.AggregationQuery aggregation )
    {
        return NodeQuery.create().query( QueryParser.parse( CITIES ) ).addAggregationQuery( aggregation ).size( 0 ).build();
    }
}
