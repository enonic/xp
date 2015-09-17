package com.enonic.wem.repo.internal.elasticsearch.aggregation;

import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;

class TermsAggregationFactory
    extends AggregationsFactory
{

    static BucketAggregation create( final Terms termsAggregation )
    {
        return BucketAggregation.bucketAggregation( termsAggregation.getName() ).
            buckets( createBuckets( termsAggregation.getBuckets() ) ).
            build();
    }

    private static Buckets createBuckets( final Collection<Terms.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final Terms.Bucket bucket : buckets )
        {
            createAndAddBucket( bucketsBuilder, bucket );
        }

        return bucketsBuilder.build();
    }

}
