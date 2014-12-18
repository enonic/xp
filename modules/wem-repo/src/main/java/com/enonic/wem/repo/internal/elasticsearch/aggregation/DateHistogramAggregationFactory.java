package com.enonic.wem.repo.internal.elasticsearch.aggregation;

import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;

import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.aggregation.Buckets;

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
            createAndAddBucket( bucketsBuilder, bucket );
        }

        return bucketsBuilder.build();
    }

}
