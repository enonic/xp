package com.enonic.xp.admin.impl.json.aggregation;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.DateRangeBucket;

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
            if ( bucket instanceof DateRangeBucket )
            {
                builder.add( new DateRangeBucketJson( (DateRangeBucket) bucket ) );
            }
            else
            {
                builder.add( new BucketJson( bucket ) );
            }
        }

        this.buckets = builder.build();
    }

    public ImmutableSet<BucketJson> getBuckets()
    {
        return buckets;
    }

}
