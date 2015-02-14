package com.enonic.wem.repo.internal.elasticsearch.aggregation;

import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.range.date.DateRange;

import com.enonic.xp.core.aggregation.BucketAggregation;
import com.enonic.xp.core.aggregation.Buckets;
import com.enonic.xp.core.aggregation.DateRangeBucket;

class DateRangeAggregationFactory
    extends AggregationsFactory
{
    static BucketAggregation create( final DateRange dateRangeAggregation )
    {
        return BucketAggregation.bucketAggregation( dateRangeAggregation.getName() ).
            buckets( createBuckets( dateRangeAggregation.getBuckets() ) ).
            build();
    }

    private static Buckets createBuckets( final Collection<? extends DateRange.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final DateRange.Bucket bucket : buckets )
        {
            final DateRangeBucket.Builder builder = DateRangeBucket.create().
                key( bucket.getKey() ).
                docCount( bucket.getDocCount() ).
                from( bucket.getFromAsDate() != null ? bucket.getFromAsDate().toDate().toInstant() : null ).
                to( bucket.getToAsDate() != null ? bucket.getToAsDate().toDate().toInstant() : null );

            doAddSubAggregations( bucket, builder );

            bucketsBuilder.add( builder.build() );
        }

        return bucketsBuilder.build();
    }

}
