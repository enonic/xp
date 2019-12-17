package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.range.InternalGeoDistance;
import org.elasticsearch.search.aggregations.bucket.range.InternalRange;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.GeoDistanceRangeBucket;

class GeoDistanceAggregationFactory
    extends AggregationsFactory
{
    static BucketAggregation create( final InternalGeoDistance geoDistance )
    {
        return BucketAggregation.bucketAggregation( geoDistance.getName() ).
            buckets( createBuckets( geoDistance.getBuckets() ) ).
            build();
    }

    private static Buckets createBuckets( final Collection<? extends InternalRange.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final InternalRange.Bucket bucket : buckets )
        {
            final GeoDistanceRangeBucket.Builder builder = GeoDistanceRangeBucket.create().
                from( (Double) bucket.getFrom() ).
                to( (Double) bucket.getTo() ).
                key( bucket.getKey() ).
                docCount( bucket.getDocCount() );

            doAddSubAggregations( bucket, builder );

            bucketsBuilder.add( builder.build() );
        }
        return bucketsBuilder.build();
    }
}
