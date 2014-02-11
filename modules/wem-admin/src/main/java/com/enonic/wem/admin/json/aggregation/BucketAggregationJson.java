package com.enonic.wem.admin.json.aggregation;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.aggregation.Bucket;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.aggregation.DateRangeBucket;

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
