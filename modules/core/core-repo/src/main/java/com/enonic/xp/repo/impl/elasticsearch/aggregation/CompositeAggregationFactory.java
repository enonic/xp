package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import java.util.List;
import java.util.Map;

import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.composite.ParsedComposite;

import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.CompositeBucket;
import com.enonic.xp.aggregation.CompositeBucketAggregation;

class CompositeAggregationFactory
    extends AggregationsFactory
{

    static CompositeBucketAggregation create( final ParsedComposite compositeAggregation )
    {
        return CompositeBucketAggregation.bucketAggregation( compositeAggregation.getName() ).
            after( compositeAggregation.afterKey() ).
            buckets( createBuckets( compositeAggregation.getBuckets() ) ).
            build();
    }

    private static Buckets createBuckets( final List<ParsedComposite.ParsedBucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final ParsedComposite.ParsedBucket bucket : buckets )
        {
            createAndAddBucket( bucketsBuilder, bucket );
        }

        return bucketsBuilder.build();
    }

    static void createAndAddBucket( final Buckets.Builder bucketsBuilder, final MultiBucketsAggregation.Bucket bucket )
    {
        final CompositeBucket.Builder builder = CompositeBucket.create().
            key( bucket.getKeyAsString() ).
            keys( (Map<String, String>) bucket.getKey() ).
            docCount( bucket.getDocCount() );

        doAddSubAggregations( bucket, builder );

        bucketsBuilder.add( builder.build() );
    }


}
