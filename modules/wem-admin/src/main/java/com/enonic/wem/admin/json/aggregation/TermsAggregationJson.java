package com.enonic.wem.admin.json.aggregation;

import com.enonic.wem.api.query.aggregation.TermsAggregation;

public class TermsAggregationJson
    extends AggregationJson
{
    private final BucketsJson buckets;

    public TermsAggregationJson( final TermsAggregation termsAggregation )
    {
        super( termsAggregation );

        this.buckets = new BucketsJson( termsAggregation.getBuckets() );

    }

    public BucketsJson getBuckets()
    {
        return buckets;
    }
}


