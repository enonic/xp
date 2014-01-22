package com.enonic.wem.admin.json.aggregation;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.query.aggregation.Bucket;
import com.enonic.wem.api.query.aggregation.Buckets;

public class BucketsJson
{
    private final ImmutableSet<BucketJson> bucketJsons;

    public BucketsJson( final Buckets buckets )
    {
        ImmutableSet.Builder<BucketJson> builder = ImmutableSet.builder();

        for ( final Bucket bucket : buckets )
        {
            builder.add( new BucketJson( bucket.getName(), bucket.getDocCount() ) );
        }

        this.bucketJsons = builder.build();
    }

    public ImmutableSet<BucketJson> getBucketJsons()
    {
        return bucketJsons;
    }
}
