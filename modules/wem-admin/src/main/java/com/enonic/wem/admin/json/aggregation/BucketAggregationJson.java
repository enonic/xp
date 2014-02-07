package com.enonic.wem.admin.json.aggregation;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.query.aggregation.Bucket;
import com.enonic.wem.api.query.aggregation.BucketAggregation;

public class BucketAggregationJson
    extends AggregationJson
{
    private final ImmutableSet<BucketJson> buckets;

    public BucketAggregationJson( final BucketAggregation bucketAggregation )
    {
        super( bucketAggregation );

        ImmutableSet.Builder<BucketJson> builder = ImmutableSet.builder();

        for ( final Bucket bucket : bucketAggregation.getBuckets() )
        {
            builder.add( new BucketJson( bucket ) );
        }

        this.buckets = builder.build();
    }

    public ImmutableSet<BucketJson> getBuckets()
    {
        return buckets;
    }

}
