package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.range.Range;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.NumericRangeBucket;

class NumericRangeAggregationFactory
    extends AggregationsFactory
{
    static BucketAggregation create( final Range rangeAggregtaion )
    {
        return BucketAggregation.bucketAggregation( rangeAggregtaion.getName() ).
            buckets( createBuckets( rangeAggregtaion.getBuckets() ) ).
            build();
    }

    private static Buckets createBuckets( final Collection<? extends Range.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final Range.Bucket bucket : buckets )
        {
            final NumericRangeBucket.Builder builder = NumericRangeBucket.create().
                from( (Number) bucket.getFrom() ).
                to( (Number) bucket.getTo() ).
                key( bucket.getKeyAsString() ).
                docCount( bucket.getDocCount() );

            doAddSubAggregations( bucket, builder );

            bucketsBuilder.add( builder.build() );
        }
        return bucketsBuilder.build();
    }

}
