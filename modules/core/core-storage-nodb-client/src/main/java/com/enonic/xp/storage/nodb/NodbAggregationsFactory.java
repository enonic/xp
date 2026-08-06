package com.enonic.xp.storage.nodb;

import java.time.Instant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.DateHistogramBucket;
import com.enonic.xp.aggregation.DateRangeBucket;
import com.enonic.xp.aggregation.GeoDistanceRangeBucket;
import com.enonic.xp.aggregation.NumericRangeBucket;
import com.enonic.xp.aggregation.SingleValueMetricAggregation;
import com.enonic.xp.aggregation.StatsAggregation;

/**
 * Builds XP's {@link Aggregations} object graph from the TAGGED aggregation document NoDB returns —
 * the nodb-mode counterpart of {@code AggregationsFactory}, which stays untouched on the
 * Elasticsearch path (the byte-identical rule).
 *
 * <h2>Why this class exists at all (D9)</h2>
 * {@code AggregationsFactory} decides which XP type to build by testing {@code instanceof} against
 * Elasticsearch's INTERNAL result classes, and two of those tests carry information that is nowhere
 * in a JSON response:
 * <ul>
 * <li>a DATE histogram is distinguished from a NUMERIC one only by {@code instanceof
 * InternalHistogram} being tested before {@code instanceof Histogram} — same interface, same
 * response shape, same numeric bucket key;</li>
 * <li>a DATE range from a NUMERIC one only by {@code InternalDateRange} preceding {@code Range},
 * and a GEO range only by {@code InternalGeoDistance} preceding both.</li>
 * </ul>
 * On the wire all four are {@code {"buckets": [{"key": .., "doc_count": ..}]}}. So NoDB tags each
 * aggregation with its {@code type} and its {@code keyType}, taken from the REQUEST it still had in
 * hand, and this decoder is a <b>table lookup on those tags with no type identity in it anywhere</b>
 * — not even a fallback that sniffs the shape, because a wrong guess here does not fail, it returns
 * a plausible {@code Bucket} where a {@code DateRangeBucket} was expected and a
 * {@code ClassCastException} four layers away.
 *
 * <h2>The sentinels a JSON number cannot carry</h2>
 * XP reads Elasticsearch's own "no documents" sentinels straight out of the result objects, and they
 * are not zero: {@code InternalStats}/{@code InternalMin} initialise min to {@code +Infinity}, max to
 * {@code -Infinity}, and {@code getAvg()} computes {@code sum/count} = {@code NaN} for an empty
 * bucket. JSON has no literal for any of those, so NoDB OMITS an absent metric and the sentinel is
 * restored here — <b>per tag</b>, which is the second place the {@code type} tag is load-bearing:
 * {@code min} and {@code max} are the same JSON shape and the same XP class, and their sentinels have
 * opposite signs. The range bounds work the same way: an absent numeric or geo bound is
 * {@code ∓Infinity} (what {@code InternalRange.Bucket} returned), while an absent DATE bound is
 * {@code null} (what {@code InternalDateRange.Bucket} returned).
 */
final class NodbAggregationsFactory
{
    private static final ObjectMapper JSON = new ObjectMapper();

    private NodbAggregationsFactory()
    {
    }

    static Aggregations create( final String json )
    {
        if ( json == null || json.isEmpty() )
        {
            return Aggregations.empty();
        }

        final JsonNode root;
        try
        {
            root = JSON.readTree( json );
        }
        catch ( JsonProcessingException e )
        {
            throw new NodbClientException( "Cannot decode the aggregations section of a search response", e );
        }
        return aggregations( root );
    }

    private static Aggregations aggregations( final JsonNode array )
    {
        if ( array == null || !array.isArray() || array.isEmpty() )
        {
            return Aggregations.empty();
        }

        final Aggregations.Builder builder = Aggregations.create();
        for ( final JsonNode node : array )
        {
            builder.add( aggregation( node ) );
        }
        return builder.build();
    }

    private static Aggregation aggregation( final JsonNode node )
    {
        final String name = text( node, "name" );
        final String type = text( node, "type" );
        final String keyType = text( node, "keyType" );
        if ( name == null || type == null || keyType == null )
        {
            throw new NodbClientException( "An aggregation in the response carries no name/type/keyType tag" );
        }

        switch ( type )
        {
            case "terms":
                return buckets( name, node, NodbAggregationsFactory::plainBucket, keyType, "string" );
            case "histogram":
                return buckets( name, node, NodbAggregationsFactory::plainBucket, keyType, "double" );
            case "dateHistogram":
                return buckets( name, node, NodbAggregationsFactory::dateHistogramBucket, keyType, "instant" );
            case "numericRange":
                return buckets( name, node, NodbAggregationsFactory::numericRangeBucket, keyType, "string" );
            case "dateRange":
                return buckets( name, node, NodbAggregationsFactory::dateRangeBucket, keyType, "string" );
            case "geoDistance":
                return buckets( name, node, NodbAggregationsFactory::geoDistanceBucket, keyType, "string" );
            case "stats":
                return stats( name, node, keyType );
            case "min":
                return singleValue( name, node, keyType, Double.POSITIVE_INFINITY );
            case "max":
                return singleValue( name, node, keyType, Double.NEGATIVE_INFINITY );
            case "valueCount":
                return singleValue( name, node, keyType, 0 );
            default:
                // The mirror image of AggregationsFactory's "translator for X not implemented": an
                // unrecognised tag is a version skew between XP and NoDB, and returning something
                // plausible for it would hide exactly that.
                throw new NodbClientException( "Aggregation '" + name + "' carries unknown type tag '" + type + "'" );
        }
    }

    /**
     * @param expectedKeyType the bucket-key type this aggregation kind must carry. Asserted rather
     *                        than read: the pair is redundant by construction, so a disagreement
     *                        means the two sides were built from different rules, and that is worth
     *                        a loud failure while it is still one message rather than a wrong bucket.
     */
    private static BucketAggregation buckets( final String name, final JsonNode node, final BucketFactory factory,
                                              final String keyType, final String expectedKeyType )
    {
        if ( !expectedKeyType.equals( keyType ) )
        {
            throw new NodbClientException(
                "Aggregation '" + name + "' is tagged keyType '" + keyType + "' but its type requires '" + expectedKeyType + "'" );
        }

        final Buckets.Builder buckets = Buckets.create();
        for ( final JsonNode bucket : node.path( "buckets" ) )
        {
            buckets.add( factory.create( bucket ) );
        }
        return Aggregation.bucketAggregation( name ).buckets( buckets.build() ).build();
    }

    private interface BucketFactory
    {
        Bucket create( JsonNode bucket );
    }

    /** {@code terms} and the NUMERIC histogram: the plain {@code Bucket} the ES path builds too. */
    private static Bucket plainBucket( final JsonNode bucket )
    {
        final Bucket.Builder<?> builder = Bucket.create();
        common( builder, bucket );
        return builder.build();
    }

    private static Bucket dateHistogramBucket( final JsonNode bucket )
    {
        final DateHistogramBucket.Builder builder = DateHistogramBucket.create();
        // The formatted key AND the instant, which is the whole content of the InternalHistogram-vs-
        // Histogram distinction: XP's DateHistogramBucket carries both, and only this type does.
        builder.keyAsInstant( instant( bucket, "keyMillis" ) );
        common( builder, bucket );
        return builder.build();
    }

    private static Bucket numericRangeBucket( final JsonNode bucket )
    {
        final NumericRangeBucket.Builder builder = NumericRangeBucket.create();
        builder.from( number( bucket, "from", Double.NEGATIVE_INFINITY ) );
        builder.to( number( bucket, "to", Double.POSITIVE_INFINITY ) );
        common( builder, bucket );
        return builder.build();
    }

    private static Bucket geoDistanceBucket( final JsonNode bucket )
    {
        final GeoDistanceRangeBucket.Builder builder = GeoDistanceRangeBucket.create();
        builder.from( number( bucket, "from", Double.NEGATIVE_INFINITY ) );
        builder.to( number( bucket, "to", Double.POSITIVE_INFINITY ) );
        common( builder, bucket );
        return builder.build();
    }

    private static Bucket dateRangeBucket( final JsonNode bucket )
    {
        final DateRangeBucket.Builder builder = DateRangeBucket.create();
        // null, not a sentinel: InternalDateRange.Bucket returned null for an infinite bound, and
        // AggregationsFactory's toInstant is null-safe precisely because of that.
        builder.from( instant( bucket, "fromMillis" ) );
        builder.to( instant( bucket, "toMillis" ) );
        common( builder, bucket );
        return builder.build();
    }

    private static void common( final Bucket.Builder<?> builder, final JsonNode bucket )
    {
        builder.key( text( bucket, "key" ) );
        builder.docCount( bucket.path( "docCount" ).asLong() );
        builder.addAggregations( aggregations( bucket.get( "aggregations" ) ) );
    }

    private static StatsAggregation stats( final String name, final JsonNode node, final String keyType )
    {
        assertNoBuckets( name, keyType );

        final double count = number( node, "count", 0 );
        final double sum = number( node, "sum", 0 );
        return StatsAggregation.create( name )
            .count( count )
            .sum( sum )
            .min( number( node, "min", Double.POSITIVE_INFINITY ) )
            .max( number( node, "max", Double.NEGATIVE_INFINITY ) )
            // sum/count, exactly as InternalStats computed it -- 0/0 is NaN for an empty bucket, which
            // is the value XP has always reported there.
            .avg( number( node, "avg", sum / count ) )
            .build();
    }

    private static SingleValueMetricAggregation singleValue( final String name, final JsonNode node, final String keyType,
                                                            final double empty )
    {
        assertNoBuckets( name, keyType );
        return SingleValueMetricAggregation.create( name ).value( number( node, "value", empty ) ).build();
    }

    private static void assertNoBuckets( final String name, final String keyType )
    {
        if ( !"none".equals( keyType ) )
        {
            throw new NodbClientException(
                "Aggregation '" + name + "' is a metric aggregation but is tagged keyType '" + keyType + "'" );
        }
    }

    private static double number( final JsonNode node, final String member, final double fallback )
    {
        final JsonNode value = node.get( member );
        return value == null || !value.isNumber() ? fallback : value.doubleValue();
    }

    private static Instant instant( final JsonNode node, final String member )
    {
        final JsonNode value = node.get( member );
        return value == null || !value.isNumber() ? null : Instant.ofEpochMilli( value.asLong() );
    }

    private static String text( final JsonNode node, final String member )
    {
        final JsonNode value = node.get( member );
        return value == null || value.isNull() ? null : value.asText();
    }
}
