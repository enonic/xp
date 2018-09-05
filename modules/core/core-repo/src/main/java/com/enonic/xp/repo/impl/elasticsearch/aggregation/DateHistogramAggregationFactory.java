package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.joda.time.DateTime;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.DateHistogramBucket;

class DateHistogramAggregationFactory
    extends AggregationsFactory
{
    static BucketAggregation create( final InternalHistogram dateHistogram )
    {
        return BucketAggregation.bucketAggregation( dateHistogram.getName() ).
            buckets( createBuckets( dateHistogram.getBuckets() ) ).
            build();
    }

    private static Buckets createBuckets( final Collection<? extends InternalHistogram.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final InternalHistogram.Bucket bucket : buckets )
        {
            final DateHistogramBucket.Builder builder = DateHistogramBucket.create().
                key( bucket.getKeyAsString() ).
                docCount( bucket.getDocCount() ).
                keyAsInstant( toInstant( (DateTime) bucket.getKey() ) );

            doAddSubAggregations( bucket, builder );

            bucketsBuilder.add( builder.build() );
        }

        return bucketsBuilder.build();
    }
}
