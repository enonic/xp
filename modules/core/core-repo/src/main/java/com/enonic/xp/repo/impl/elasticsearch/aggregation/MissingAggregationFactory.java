package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.bucket.missing.Missing;

import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;

public class MissingAggregationFactory
    extends AggregationsFactory
{
    static BucketAggregation create( final Missing value )
    {
        return BucketAggregation.bucketAggregation( value.getName() ).buckets( Buckets.create().
            add( Bucket.create().key( value.getName() ).docCount( value.getDocCount() ).build() ).build() ).build();
    }
}
