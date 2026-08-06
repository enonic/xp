package com.enonic.xp.repo.impl.search.dsl;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.aggregation.BucketAggregationQuery;
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
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.storage.spi.SearchDsl;
import com.enonic.xp.util.GeoPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The aggregation half of the wire envelope (Gate E). The slot was rejected outright until this gate,
 * so — like Gate D's suggest/highlight rows — these assertions exist to prove the rejection became a
 * RENDERING and not a silent drop, and that what goes on the wire is XP's own vocabulary with the
 * parameters XP actually sets and no others.
 */
class AggregationDslRendererTest
{
    @Test
    void termsCarriesFieldSizeMinDocCountAndOrderInXpsVocabulary()
    {
        assertEquals( Map.of( "byCategory",
                              Map.of( "terms", Map.of( "field", "category", "size", 2, "minDocCount", 5L, "order",
                                                       Map.of( "type", "DOC_COUNT", "direction", "DESC" ) ) ) ),
                      render( TermsAggregationQuery.create( "byCategory" )
                                  .fieldName( "Category" )
                                  .size( 2 )
                                  .minDoccount( 5L )
                                  .orderType( TermsAggregationQuery.Type.DOC_COUNT )
                                  .orderDirection( TermsAggregationQuery.Direction.DESC )
                                  .build() ) );
    }

    /**
     * XP's builder defaults are always written, and that is not redundancy: a histogram's
     * {@code minDocCount} default is 1 in XP and 0 in the engine, so an omitted value would silently
     * add every empty bucket.
     */
    @Test
    void xpsOwnDefaultsAreAlwaysWritten()
    {
        assertEquals( Map.of( "field", "category", "size", 10, "minDocCount", 1L, "order",
                              Map.of( "type", "TERM", "direction", "ASC" ) ),
                      params( TermsAggregationQuery.create( "a" ).fieldName( "category" ).build(), "terms" ) );

        assertEquals( 1L, params( HistogramAggregationQuery.create( "a" ).fieldName( "p" ).interval( 10L ).build(),
                                  "histogram" ).get( "minDocCount" ) );
        assertEquals( 1L, params( DateHistogramAggregationQuery.create( "a" ).fieldName( "f" ).interval( "1M" ).build(),
                                  "dateHistogram" ).get( "minDocCount" ) );
    }

    @Test
    void theFourMetricTypesCarryNothingButTheirField()
    {
        assertEquals( Map.of( "field", "population" ),
                      params( StatsAggregationQuery.create( "a" ).fieldName( "Population" ).build(), "stats" ) );
        assertEquals( Map.of( "field", "population" ), params( MinAggregationQuery.create( "a" ).fieldName( "population" ).build(), "min" ) );
        assertEquals( Map.of( "field", "population" ), params( MaxAggregationQuery.create( "a" ).fieldName( "population" ).build(), "max" ) );
        assertEquals( Map.of( "field", "population" ),
                      params( ValueCountAggregationQuery.create( "a" ).fieldName( "population" ).build(), "valueCount" ) );
    }

    /** An absent bound is an OPEN bound and stays absent; an absent key lets the engine generate one. */
    @Test
    void numericRangeBoundsAndKeysAreOnlyWrittenWhenSet()
    {
        assertEquals( Map.of( "field", "population", "ranges",
                              List.of( Map.of( "to", 100.0 ), Map.of( "key", "mid", "from", 100.0, "to", 500.0 ),
                                       Map.of( "from", 500.0 ) ) ),
                      params( NumericRangeAggregationQuery.create( "a" )
                                  .fieldName( "population" )
                                  .setRanges( List.of( NumericRange.create().to( 100d ).build(),
                                                       NumericRange.create().key( "mid" ).from( 100d ).to( 500d ).build(),
                                                       NumericRange.create().from( 500d ).build() ) )
                                  .build(), "numericRange" ) );
    }

    /**
     * A {@code DateRange} bound is either an {@link Instant} or ES date math as a raw string, and both
     * ride as strings — {@code Instant.toString()} is exactly what ES 2.4's {@code XContentBuilder}
     * put on the wire for the typed form, since it had no {@code Instant} case and fell through to
     * {@code toString()}.
     */
    @Test
    void dateRangeBoundsRideAsStringsWhicheverFormTheyArrivedIn()
    {
        assertEquals( Map.of( "field", "founded", "ranges",
                              List.of( Map.of( "to", "2021-01-01T00:00:00Z" ), Map.of( "from", "now-5h", "to", "now-3h" ) ) ),
                      params( DateRangeAggregationQuery.create( "a" )
                                  .fieldName( "founded" )
                                  .setRanges( List.of( DateRange.create().to( Instant.parse( "2021-01-01T00:00:00Z" ) ).build(),
                                                       DateRange.create().from( "now-5h" ).to( "now-3h" ).build() ) )
                                  .build(), "dateRange" ) );
    }

    @Test
    void anOptionalFormatIsAbsentWhenUnsetAndPresentWhenSet()
    {
        assertFalse( params( DateRangeAggregationQuery.create( "a" )
                                 .fieldName( "f" )
                                 .addRange( DateRange.create().to( "now" ).build() )
                                 .build(), "dateRange" ).containsKey( "format" ) );
        assertEquals( "HH:mm", params( DateHistogramAggregationQuery.create( "a" )
                                           .fieldName( "f" )
                                           .interval( "1h" )
                                           .format( "HH:mm" )
                                           .build(), "dateHistogram" ).get( "format" ) );
    }

    /**
     * The interval is passed through VERBATIM and is not classified here: {@code DateHistogramInterval}
     * and {@code .interval()} are gone from the engine's API, so calendar-vs-fixed is a property of the
     * target engine and belongs with the backend, exactly like a field postfix.
     */
    @Test
    void aDateHistogramIntervalIsPassedThroughUntouched()
    {
        for ( final String interval : List.of( "1M", "1m", "1d", "90m", "week" ) )
        {
            assertEquals( interval, params( DateHistogramAggregationQuery.create( "a" )
                                                .fieldName( "f" )
                                                .interval( interval )
                                                .build(), "dateHistogram" ).get( "interval" ) );
        }
    }

    @Test
    void aNumericHistogramCarriesItsIntervalBoundsAndOrder()
    {
        assertEquals( Map.of( "field", "population", "interval", 100L, "minDocCount", 0L, "extendedBounds",
                              Map.of( "min", 0L, "max", 900L ), "order", "KEY_ASC" ),
                      params( HistogramAggregationQuery.create( "a" )
                                  .fieldName( "population" )
                                  .interval( 100L )
                                  .minDocCount( 0L )
                                  .extendedBoundMin( 0L )
                                  .extendedBoundMax( 900L )
                                  .order( HistogramAggregationQuery.Order.KEY_ASC )
                                  .build(), "histogram" ) );
    }

    /** XP writes extendedBounds only when BOTH ends are set — {@code setExtendedBounds()} says so. */
    @Test
    void halfAnExtendedBoundIsNotWrittenAtAll()
    {
        assertFalse( params( HistogramAggregationQuery.create( "a" )
                                 .fieldName( "p" )
                                 .interval( 1L )
                                 .extendedBoundMin( 0L )
                                 .build(), "histogram" ).containsKey( "extendedBounds" ) );
    }

    @Test
    void geoDistanceCarriesItsOriginAsLatLonAndItsUnitWhenSet()
    {
        assertEquals( Map.of( "field", "location", "origin", Map.of( "lat", 59.9127300, "lon", 10.7460900 ), "unit", "km", "ranges",
                              List.of( Map.of( "to", 100.0 ) ) ),
                      params( GeoDistanceAggregationQuery.create( "a" )
                                  .fieldName( "location" )
                                  .origin( GeoPoint.from( "59.9127300,10.7460900" ) )
                                  .unit( "km" )
                                  .addRange( DistanceRange.create().to( 100d ).build() )
                                  .build(), "geoDistance" ) );

        assertFalse( params( GeoDistanceAggregationQuery.create( "a" )
                                 .fieldName( "location" )
                                 .origin( GeoPoint.from( "1,2" ) )
                                 .addRange( DistanceRange.create().to( 1d ).build() )
                                 .build(), "geoDistance" ).containsKey( "unit" ) );
    }

    // ---- THE SUB-AGGREGATION PER-TYPE RULE ------------------------------------------

    @Test
    void aSubAggregationNestsUnderAnAggregationsMember()
    {
        assertEquals( Map.of( "byCategory",
                              Map.of( "terms", Map.of( "field", "category", "size", 10, "minDocCount", 1L, "order",
                                                       Map.of( "type", "TERM", "direction", "ASC" ) ), "aggregations",
                                      Map.of( "popStats", Map.of( "stats", Map.of( "field", "population" ) ) ) ) ),
                      render( TermsAggregationQuery.create( "byCategory" )
                                  .fieldName( "category" )
                                  .addSubQuery( StatsAggregationQuery.create( "popStats" ).fieldName( "population" ).build() )
                                  .build() ) );
    }

    /**
     * The rule that replaces the vanished {@code AbstractAggregationBuilder}-vs-{@code AggregationBuilder}
     * discriminator, asserted positively: exactly the six BUCKET types accept sub-aggregations. The
     * four metric types cannot even express one — {@code MetricAggregationQuery} has no
     * {@code getSubQueries} — which is why the OTHER half of that rule is asserted at the wire level
     * (see the backend's {@code AggregationTranslatorTest#noMetricTypeAcceptsSubAggregations}).
     */
    @Test
    void allSixBucketTypesAcceptSubAggregations()
    {
        // Each builder is a local rather than a chain because addSubQuery is inherited through a RAW
        // parameterization of BucketAggregationQuery.Builder in the range and histogram hierarchies,
        // so it does not return the concrete builder type. XP's own generics, not this test's shape.
        final NumericRangeAggregationQuery.Builder numericRange =
            NumericRangeAggregationQuery.create( "a" ).fieldName( "p" ).addRange( NumericRange.create().to( 1d ).build() );
        numericRange.addSubQuery( sub() );

        final DateRangeAggregationQuery.Builder dateRange =
            DateRangeAggregationQuery.create( "a" ).fieldName( "f" ).addRange( DateRange.create().to( "now" ).build() );
        dateRange.addSubQuery( sub() );

        final GeoDistanceAggregationQuery.Builder geoDistance = GeoDistanceAggregationQuery.create( "a" )
            .origin( GeoPoint.from( "1,2" ) )
            .fieldName( "l" )
            .addRange( DistanceRange.create().to( 1d ).build() );
        geoDistance.addSubQuery( sub() );

        final HistogramAggregationQuery.Builder histogram = HistogramAggregationQuery.create( "a" ).fieldName( "p" ).interval( 1L );
        histogram.addSubQuery( sub() );

        final DateHistogramAggregationQuery.Builder dateHistogram =
            DateHistogramAggregationQuery.create( "a" ).fieldName( "f" ).interval( "1M" );
        dateHistogram.addSubQuery( sub() );

        final List<BucketAggregationQuery> parents =
            List.of( TermsAggregationQuery.create( "a" ).fieldName( "c" ).addSubQuery( sub() ).build(), numericRange.build(),
                     dateRange.build(), geoDistance.build(), histogram.build(), dateHistogram.build() );

        for ( final BucketAggregationQuery parent : parents )
        {
            @SuppressWarnings("unchecked")
            final Map<String, Object> rendered = (Map<String, Object>) render( parent ).get( "a" );
            assertTrue( rendered.containsKey( "aggregations" ), parent.getClass().getSimpleName() );
        }
    }

    @Test
    void subAggregationsNestArbitrarilyDeep()
    {
        final SearchDsl dsl = dsl( TermsAggregationQuery.create( "outer" )
                                       .fieldName( "c" )
                                       .addSubQuery( TermsAggregationQuery.create( "middle" )
                                                         .fieldName( "d" )
                                                         .addSubQuery( MinAggregationQuery.create( "inner" ).fieldName( "p" ).build() )
                                                         .build() )
                                       .build() );

        assertEquals( Map.of( "inner", Map.of( "min", Map.of( "field", "p" ) ) ),
                      at( dsl.getAggregations(), "outer", "aggregations", "middle", "aggregations" ) );
    }

    // ---- ordering and the remaining fence -------------------------------------------

    /**
     * Aggregations arrive in a set-shaped collection, so the map is keyed and SORTED by name — Gate
     * 0(c) item 5's nondeterministic-request-JSON hazard, and the reason the response can be paired
     * back up by name at all.
     */
    @Test
    void aggregationsAreOrderedByName()
    {
        final SearchDsl dsl = dsl( MinAggregationQuery.create( "zulu" ).fieldName( "p" ).build(),
                                   MaxAggregationQuery.create( "alpha" ).fieldName( "p" ).build(),
                                   StatsAggregationQuery.create( "mike" ).fieldName( "p" ).build() );

        assertEquals( List.of( "alpha", "mike", "zulu" ), List.copyOf( dsl.getAggregations().keySet() ) );
    }

    /**
     * Item 5 one level deeper, and it was a LIVE BUG: {@code AbstractRangeAggregationQuery} holds its
     * ranges in an {@code ImmutableSet} copied from a {@code HashSet} of objects that never override
     * {@code hashCode}, so range order is identity-hash order and varies per JVM run — and a real
     * {@code geo_distance} aggregation returns its buckets in the order the request listed them
     * (measured; {@code range} and {@code date_range} re-sort by the resolved {@code from}). Corpus
     * row {@code AGG-13} came back with the same keys and counts in a different order on a repeat run
     * because of exactly this. So the ranges are sorted here, open ends outermost.
     */
    @Test
    void rangesAreSortedIntoATotalOrderSoBucketOrderCannotVaryPerRun()
    {
        assertEquals( List.of( Map.of( "to", 100.0 ), Map.of( "from", 100.0, "to", 500.0 ), Map.of( "from", 500.0 ) ),
                      params( NumericRangeAggregationQuery.create( "a" )
                                  .fieldName( "p" )
                                  .setRanges( List.of( NumericRange.create().from( 500d ).build(),
                                                       NumericRange.create().to( 100d ).build(),
                                                       NumericRange.create().from( 100d ).to( 500d ).build() ) )
                                  .build(), "numericRange" ).get( "ranges" ) );

        final GeoDistanceAggregationQuery.Builder geo = GeoDistanceAggregationQuery.create( "a" )
            .origin( GeoPoint.from( "1,2" ) )
            .fieldName( "l" )
            .setRanges( List.of( DistanceRange.create().from( 1000d ).build(), DistanceRange.create().to( 100d ).build(),
                                 DistanceRange.create().from( 100d ).to( 1000d ).build() ) );
        assertEquals( List.of( Map.of( "to", 100.0 ), Map.of( "from", 100.0, "to", 1000.0 ), Map.of( "from", 1000.0 ) ),
                      params( geo.build(), "geoDistance" ).get( "ranges" ) );

        // Date bounds may be date math, which only the engine can evaluate, so their order is
        // lexicographic — arbitrary but STABLE, which is all it has to be: the engine re-sorts
        // date_range buckets by the resolved `from` anyway.
        assertEquals( List.of( Map.of( "to", "2020-01-01T00:00:00Z" ), Map.of( "from", "2020-01-01T00:00:00Z" ) ),
                      params( DateRangeAggregationQuery.create( "a" )
                                  .fieldName( "f" )
                                  .setRanges( List.of( DateRange.create().from( Instant.parse( "2020-01-01T00:00:00Z" ) ).build(),
                                                       DateRange.create().to( Instant.parse( "2020-01-01T00:00:00Z" ) ).build() ) )
                                  .build(), "dateRange" ).get( "ranges" ) );
    }

    @Test
    void twoRangesWithIdenticalBoundsAreOrderedByTheirKey()
    {
        assertEquals( List.of( Map.of( "key", "aaa", "to", 1.0 ), Map.of( "key", "bbb", "to", 1.0 ) ),
                      params( NumericRangeAggregationQuery.create( "a" )
                                  .fieldName( "p" )
                                  .setRanges( List.of( NumericRange.create().key( "bbb" ).to( 1d ).build(),
                                                       NumericRange.create().key( "aaa" ).to( 1d ).build() ) )
                                  .build(), "numericRange" ).get( "ranges" ) );
    }

    @Test
    void twoAggregationsSharingOneNameFailLoudly()
    {
        assertThrows( DslRenderException.class, () -> dsl( MinAggregationQuery.create( "same" ).fieldName( "p" ).build(),
                                                           MaxAggregationQuery.create( "same" ).fieldName( "p" ).build() ) );
    }

    @Test
    void aQueryWithNoAggregationsCarriesNone()
    {
        assertTrue( dsl().getAggregations().isEmpty() );
    }

    @Test
    void anAggregationWithoutAFieldNameFailsLoudly()
    {
        assertThrows( DslRenderException.class, () -> dsl( MinAggregationQuery.create( "a" ).build() ) );
    }

    /**
     * The fence that remains after this gate: not "aggregations have no wire form" but "an aggregation
     * TYPE this renderer does not know has no wire form". Rendering an unknown subtype as the nearest
     * known one would return plausible-looking wrong buckets, which is the failure mode the whole
     * phase is built to exclude.
     */
    @Test
    void anUnknownAggregationSubtypeStillHasNoWireForm()
    {
        final AggregationQuery unknown = new BucketAggregationQuery( new BucketAggregationQuery.Builder<>( "cardinality" ) )
        {
        };

        final DslRenderException e = assertThrows( DslRenderException.class, () -> dsl( unknown ) );
        assertTrue( e.getMessage().contains( "Unexpected aggregation type" ), e.getMessage() );
    }

    // ---- helpers --------------------------------------------------------------------

    private static AggregationQuery sub()
    {
        return MinAggregationQuery.create( "sub" ).fieldName( "p" ).build();
    }

    private static Map<String, Object> render( final AggregationQuery query )
    {
        return dsl( query ).getAggregations();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> params( final AggregationQuery query, final String type )
    {
        return (Map<String, Object>) ( (Map<String, Object>) render( query ).get( "a" ) ).get( type );
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> at( final Map<String, Object> root, final String... path )
    {
        Map<String, Object> node = root;
        for ( final String step : path )
        {
            node = (Map<String, Object>) node.get( step );
        }
        return node;
    }

    private static SearchDsl dsl( final AggregationQuery... queries )
    {
        final NodeQuery.Builder builder = NodeQuery.create().query( QueryParser.parse( "_path LIKE '/x*'" ) );
        for ( final AggregationQuery query : queries )
        {
            builder.addAggregationQuery( query );
        }
        return SearchDslRenderer.render( builder.build() );
    }
}
