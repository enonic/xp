package com.enonic.wem.repo.internal.elasticsearch.aggregation;

import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.DateHistogramBucket;

class DateHistogramAggregationFactory
    extends AggregationsFactory
{
    static BucketAggregation create( final DateHistogram dateHistogram )
    {
        return BucketAggregation.bucketAggregation( dateHistogram.getName() ).
            buckets( createBuckets( dateHistogram.getBuckets() ) ).
            build();
    }

    private static Buckets createBuckets( final Collection<? extends DateHistogram.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final DateHistogram.Bucket bucket : buckets )
        {
            final DateHistogramBucket.Builder builder = DateHistogramBucket.create().
                key( bucket.getKey() ).
                docCount( bucket.getDocCount() ).
                keyAsInstant( bucket.getKeyAsDate() != null ? bucket.getKeyAsDate().toDate().toInstant() : null );

            doAddSubAggregations( bucket, builder );

            bucketsBuilder.add( builder.build() );
        }

        return bucketsBuilder.build();
    }
}
