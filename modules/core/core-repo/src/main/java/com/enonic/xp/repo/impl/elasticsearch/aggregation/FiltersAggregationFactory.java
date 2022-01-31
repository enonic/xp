package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.bucket.filters.Filters;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;

public class FiltersAggregationFactory
    extends AggregationsFactory
{
    static BucketAggregation create( final Filters filters )
    {
        final BucketAggregation.Builder builder = BucketAggregation.bucketAggregation( filters.getName() );

        final Buckets.Builder bucketsBuilder = new Buckets.Builder();
        for ( final Filters.Bucket bucket : filters.getBuckets() )
        {
            createAndAddBucket( bucketsBuilder, bucket );
        }

        builder.buckets( bucketsBuilder.build() );

        return builder.build();
    }
}
