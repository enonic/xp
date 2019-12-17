package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import java.time.ZonedDateTime;
import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.range.ParsedDateRange;
import org.elasticsearch.search.aggregations.bucket.range.Range;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.DateRangeBucket;

public class ParsedDateRangeAggregationFactory
    extends AggregationsFactory
{
    static BucketAggregation create( final ParsedDateRange dateRangeAggregation )
    {
        return BucketAggregation.bucketAggregation( dateRangeAggregation.getName() ).
            buckets( createBuckets( dateRangeAggregation.getBuckets() ) ).
            build();
    }

    private static Buckets createBuckets( final Collection<? extends Range.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final Range.Bucket bucket : buckets )
        {
            final DateRangeBucket.Builder builder = DateRangeBucket.create().
                key( bucket.getKeyAsString() ).
                docCount( bucket.getDocCount() ).
                from( toInstant( (ZonedDateTime) bucket.getFrom() ) ).
                to( toInstant( (ZonedDateTime) bucket.getTo() ) );

            doAddSubAggregations( bucket, builder );

            bucketsBuilder.add( builder.build() );
        }

        return bucketsBuilder.build();
    }
}
