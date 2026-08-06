package com.enonic.xp.storage.nodb;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.DateHistogramBucket;
import com.enonic.xp.aggregation.DateRangeBucket;
import com.enonic.xp.aggregation.GeoDistanceRangeBucket;
import com.enonic.xp.aggregation.NumericRangeBucket;
import com.enonic.xp.aggregation.SingleValueMetricAggregation;
import com.enonic.xp.aggregation.StatsAggregation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The nodb response-side decode path (Gate E / D9): the tagged wire document → XP's
 * {@code Aggregations} object graph, built from TAGS and never from type identity.
 * <p>
 * The rows that matter most are the ones where two different XP types share one wire SHAPE, because
 * those are precisely the cases the Elasticsearch path answered with {@code instanceof} on internal
 * classes and a JSON wire cannot answer at all.
 */
class NodbAggregationsFactoryTest
{
    /** {@code terms}: XP's plain {@code Bucket}, keyed by the term. */
    @Test
    void aTermsAggregationBecomesABucketAggregationOfPlainBuckets()
    {
        final BucketAggregation aggregation = (BucketAggregation) one(
            "{\"name\":\"byCategory\",\"type\":\"terms\",\"keyType\":\"string\",\"buckets\":[" +
                "{\"key\":\"c1\",\"docCount\":4,\"aggregations\":[]},{\"key\":\"c2\",\"docCount\":3,\"aggregations\":[]}]}" );

        assertEquals( "byCategory", aggregation.getName() );
        assertEquals( 2, aggregation.getBuckets().getSize() );
        assertEquals( "c1", aggregation.getBuckets().first().getKey() );
        assertEquals( 4, aggregation.getBuckets().first().getDocCount() );
        assertEquals( Bucket.class, aggregation.getBuckets().first().getClass() );
    }

    /**
     * <b>The row D9 exists for.</b> The two documents below differ in ONE character of tag and are
     * otherwise identical, and they must produce different XP classes: a {@code DateHistogramBucket}
     * carrying an {@code Instant}, and a plain {@code Bucket} that has no instant at all. On the ES
     * path this distinction was {@code instanceof InternalHistogram} being tested before
     * {@code instanceof Histogram} — same interface, same shape, same numeric key.
     */
    @Test
    void aDateHistogramAndANumericHistogramDifferOnlyByTagAndYieldDifferentBucketTypes()
    {
        final String buckets = "\"buckets\":[{\"key\":\"2020-01-01T00:00:00.000Z\",\"keyMillis\":1577836800000,\"docCount\":2," +
            "\"aggregations\":[]}]}";

        final Bucket dated = ( (BucketAggregation) one(
            "{\"name\":\"h\",\"type\":\"dateHistogram\",\"keyType\":\"instant\"," + buckets ) ).getBuckets().first();
        final Bucket numeric = ( (BucketAggregation) one(
            "{\"name\":\"h\",\"type\":\"histogram\",\"keyType\":\"double\"," + buckets ) ).getBuckets().first();

        assertInstanceOf( DateHistogramBucket.class, dated );
        assertEquals( Instant.parse( "2020-01-01T00:00:00Z" ), ( (DateHistogramBucket) dated ).getKeyAsInstant() );
        assertEquals( "2020-01-01T00:00:00.000Z", dated.getKey(), "the formatted key is what XP's getKey() has always been" );

        assertEquals( Bucket.class, numeric.getClass(), "a numeric histogram bucket carries no instant" );
        assertEquals( "2020-01-01T00:00:00.000Z", numeric.getKey() );
    }

    /** The same story for the three range families: one wire shape, three XP bucket types. */
    @Test
    void theThreeRangeFamiliesYieldThreeBucketTypesFromTheTagAlone()
    {
        assertInstanceOf( NumericRangeBucket.class, rangeBucket( "numericRange" ) );
        assertInstanceOf( GeoDistanceRangeBucket.class, rangeBucket( "geoDistance" ) );

        final Bucket dated = ( (BucketAggregation) one(
            "{\"name\":\"r\",\"type\":\"dateRange\",\"keyType\":\"string\",\"buckets\":[{\"key\":\"k\"," +
                "\"fromMillis\":1577836800000,\"toMillis\":1609459200000,\"docCount\":1,\"aggregations\":[]}]}" ) ).getBuckets().first();

        assertInstanceOf( DateRangeBucket.class, dated );
        assertEquals( Instant.parse( "2020-01-01T00:00:00Z" ), ( (DateRangeBucket) dated ).getFrom() );
        assertEquals( Instant.parse( "2021-01-01T00:00:00Z" ), ( (DateRangeBucket) dated ).getTo() );
    }

    @Test
    void numericAndGeoRangeBoundsAreNumbers()
    {
        final NumericRangeBucket numeric = (NumericRangeBucket) rangeBucket( "numericRange" );
        assertEquals( 100.0, numeric.getFrom() );
        assertEquals( 500.0, numeric.getTo() );

        final GeoDistanceRangeBucket geo = (GeoDistanceRangeBucket) rangeBucket( "geoDistance" );
        assertEquals( 100.0, geo.getFrom() );
        assertEquals( 500.0, geo.getTo() );
    }

    /**
     * The sentinels a JSON number cannot carry, and the second place the tag is load-bearing: an
     * OPEN numeric or geo bound is {@code ∓Infinity} — what {@code InternalRange.Bucket} handed XP —
     * while an open DATE bound is {@code null}, which is what {@code InternalDateRange.Bucket}
     * returned and why {@code AggregationsFactory}'s {@code toInstant} is null-safe.
     */
    @Test
    void anAbsentBoundBecomesInfinityForNumbersAndNullForDates()
    {
        final NumericRangeBucket open = (NumericRangeBucket) ( (BucketAggregation) one(
            "{\"name\":\"r\",\"type\":\"numericRange\",\"keyType\":\"string\"," +
                "\"buckets\":[{\"key\":\"*-100.0\",\"to\":100.0,\"docCount\":1,\"aggregations\":[]}]}" ) ).getBuckets().first();
        assertEquals( Double.NEGATIVE_INFINITY, open.getFrom() );
        assertEquals( 100.0, open.getTo() );

        final DateRangeBucket openDate = (DateRangeBucket) ( (BucketAggregation) one(
            "{\"name\":\"r\",\"type\":\"dateRange\",\"keyType\":\"string\"," +
                "\"buckets\":[{\"key\":\"*-x\",\"toMillis\":1609459200000,\"docCount\":1,\"aggregations\":[]}]}" ) ).getBuckets().first();
        assertNull( openDate.getFrom() );
        assertEquals( Instant.parse( "2021-01-01T00:00:00Z" ), openDate.getTo() );
    }

    @Test
    void statsCarriesEveryMetric()
    {
        final StatsAggregation stats = (StatsAggregation) one(
            "{\"name\":\"s\",\"type\":\"stats\",\"keyType\":\"none\",\"count\":4,\"min\":2.0,\"max\":8.0,\"avg\":5.0,\"sum\":20.0}" );

        assertEquals( 4d, stats.getCount() );
        assertEquals( 2d, stats.getMin() );
        assertEquals( 8d, stats.getMax() );
        assertEquals( 5d, stats.getAvg() );
        assertEquals( 20d, stats.getSum() );
    }

    @Test
    void minMaxAndValueCountAllBecomeSingleValueMetricAggregations()
    {
        assertEquals( 45000d, ( (SingleValueMetricAggregation) one(
            "{\"name\":\"a\",\"type\":\"min\",\"keyType\":\"none\",\"value\":45000.0}" ) ).getValue() );
        assertEquals( 8d, ( (SingleValueMetricAggregation) one(
            "{\"name\":\"a\",\"type\":\"valueCount\",\"keyType\":\"none\",\"value\":8}" ) ).getValue() );
    }

    /**
     * The empty-bucket sentinels XP has always reported, restored per TAG — which is the whole reason
     * {@code min} and {@code max} need separate tags at all: identical JSON, identical XP class,
     * opposite sentinel.
     */
    @Test
    void anAbsentMetricBecomesTheSentinelElasticsearchReported()
    {
        assertEquals( Double.POSITIVE_INFINITY,
                      ( (SingleValueMetricAggregation) one( "{\"name\":\"a\",\"type\":\"min\",\"keyType\":\"none\"}" ) ).getValue() );
        assertEquals( Double.NEGATIVE_INFINITY,
                      ( (SingleValueMetricAggregation) one( "{\"name\":\"a\",\"type\":\"max\",\"keyType\":\"none\"}" ) ).getValue() );
        assertEquals( 0d,
                      ( (SingleValueMetricAggregation) one( "{\"name\":\"a\",\"type\":\"valueCount\",\"keyType\":\"none\"}" ) ).getValue() );

        final StatsAggregation empty =
            (StatsAggregation) one( "{\"name\":\"s\",\"type\":\"stats\",\"keyType\":\"none\",\"count\":0,\"sum\":0.0}" );
        assertEquals( Double.POSITIVE_INFINITY, empty.getMin() );
        assertEquals( Double.NEGATIVE_INFINITY, empty.getMax() );
        assertTrue( Double.isNaN( empty.getAvg() ), "InternalStats computed avg as sum/count, i.e. NaN for an empty bucket" );
        assertEquals( 0d, empty.getCount() );
    }

    @Test
    void subAggregationsHangOffTheirBucketAndNestArbitrarilyDeep()
    {
        final BucketAggregation outer = (BucketAggregation) one(
            "{\"name\":\"outer\",\"type\":\"terms\",\"keyType\":\"string\",\"buckets\":[{\"key\":\"c1\",\"docCount\":4," +
                "\"aggregations\":[{\"name\":\"middle\",\"type\":\"histogram\",\"keyType\":\"double\"," +
                "\"buckets\":[{\"key\":\"0\",\"docCount\":2,\"aggregations\":[" +
                "{\"name\":\"inner\",\"type\":\"min\",\"keyType\":\"none\",\"value\":1.0}]}]}]}]}" );

        final Aggregations subs = outer.getBuckets().first().getSubAggregations();
        assertEquals( 1, subs.getSize() );

        final BucketAggregation middle = (BucketAggregation) subs.get( "middle" );
        final Aggregation inner = middle.getBuckets().first().getSubAggregations().get( "inner" );
        assertEquals( 1d, ( (SingleValueMetricAggregation) inner ).getValue() );
    }

    @Test
    void severalAggregationsKeepTheOrderTheyArriveIn()
    {
        final Aggregations aggregations = NodbAggregationsFactory.create(
            "[{\"name\":\"a\",\"type\":\"min\",\"keyType\":\"none\",\"value\":1.0}," +
                "{\"name\":\"b\",\"type\":\"max\",\"keyType\":\"none\",\"value\":2.0}]" );

        assertEquals( List.of( "a", "b" ), aggregations.stream().map( Aggregation::getName ).toList() );
    }

    @Test
    void nothingRequestedDecodesToEmptyAggregations()
    {
        assertEquals( 0, NodbAggregationsFactory.create( "" ).getSize() );
        assertEquals( 0, NodbAggregationsFactory.create( null ).getSize() );
        assertEquals( 0, NodbAggregationsFactory.create( "[]" ).getSize() );
    }

    /**
     * An unknown tag is a version skew between XP and NoDB, and the whole point of decoding by tag is
     * that there is nothing to fall back to: a plausible-looking result would hide exactly that.
     */
    @Test
    void anUnknownOrMissingTagFailsLoudly()
    {
        assertThrows( NodbClientException.class,
                      () -> NodbAggregationsFactory.create( "[{\"name\":\"a\",\"type\":\"cardinality\",\"keyType\":\"none\"}]" ) );
        assertThrows( NodbClientException.class, () -> NodbAggregationsFactory.create( "[{\"name\":\"a\",\"keyType\":\"none\"}]" ) );
        assertThrows( NodbClientException.class, () -> NodbAggregationsFactory.create( "[{\"type\":\"min\",\"keyType\":\"none\"}]" ) );
        assertThrows( NodbClientException.class, () -> NodbAggregationsFactory.create( "not json" ) );
    }

    /**
     * The two tags are redundant by construction, and the redundancy is checked rather than trusted:
     * a disagreement means the two sides were built from different rules, which is worth one loud
     * message now instead of a wrong bucket class later.
     */
    @Test
    void aKeyTypeThatContradictsTheTypeFailsLoudly()
    {
        assertThrows( NodbClientException.class, () -> NodbAggregationsFactory.create(
            "[{\"name\":\"h\",\"type\":\"dateHistogram\",\"keyType\":\"double\",\"buckets\":[]}]" ) );
        assertThrows( NodbClientException.class, () -> NodbAggregationsFactory.create(
            "[{\"name\":\"m\",\"type\":\"min\",\"keyType\":\"string\",\"value\":1.0}]" ) );
        assertThrows( NodbClientException.class, () -> NodbAggregationsFactory.create(
            "[{\"name\":\"t\",\"type\":\"terms\",\"keyType\":\"none\",\"buckets\":[]}]" ) );
    }

    private static Bucket rangeBucket( final String type )
    {
        return ( (BucketAggregation) one( "{\"name\":\"r\",\"type\":\"" + type + "\",\"keyType\":\"string\"," +
                                              "\"buckets\":[{\"key\":\"100.0-500.0\",\"from\":100.0,\"to\":500.0,\"docCount\":1," +
                                              "\"aggregations\":[]}]}" ) ).getBuckets().first();
    }

    private static Aggregation one( final String json )
    {
        final Aggregations aggregations = NodbAggregationsFactory.create( "[" + json + "]" );
        assertEquals( 1, aggregations.getSize() );
        return aggregations.get( 0 );
    }
}
