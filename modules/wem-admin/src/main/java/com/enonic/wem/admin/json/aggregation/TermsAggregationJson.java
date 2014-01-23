package com.enonic.wem.admin.json.aggregation;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.query.aggregation.Bucket;
import com.enonic.wem.api.query.aggregation.TermsAggregation;

public class TermsAggregationJson
    extends AggregationJson
{
    private final ImmutableSet<BucketJson> buckets;

    public TermsAggregationJson( final TermsAggregation termsAggregation )
    {
        super( termsAggregation );

        ImmutableSet.Builder<BucketJson> builder = ImmutableSet.builder();

        for ( final Bucket bucket : termsAggregation.getBuckets() )
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


