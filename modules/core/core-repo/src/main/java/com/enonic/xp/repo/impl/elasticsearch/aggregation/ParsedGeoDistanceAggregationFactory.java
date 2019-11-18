package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.range.ParsedGeoDistance;
import org.elasticsearch.search.aggregations.bucket.range.Range;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.GeoDistanceRangeBucket;

public class ParsedGeoDistanceAggregationFactory
    extends AggregationsFactory
{

    static BucketAggregation create( final ParsedGeoDistance geoDistance )
    {
        return BucketAggregation.bucketAggregation( geoDistance.getName() ).
            buckets( createBuckets( geoDistance.getBuckets() ) ).
            build();
    }

    private static Buckets createBuckets( final Collection<? extends Range.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final Range.Bucket bucket : buckets )
        {
            final GeoDistanceRangeBucket.Builder builder = GeoDistanceRangeBucket.create().
                from( (Double) bucket.getFrom() ).
                to( (Double) bucket.getTo() ).
                key( bucket.getKey() != null ? bucket.getKeyAsString() : null ).
                docCount( bucket.getDocCount() );

            doAddSubAggregations( bucket, builder );

            bucketsBuilder.add( builder.build() );
        }
        return bucketsBuilder.build();
    }

}
