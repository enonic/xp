package com.enonic.wem.repo.internal.elasticsearch.aggregation;


import java.util.Collection;

import org.elasticsearch.search.aggregations.HasAggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRange;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.aggregation.Aggregations;
import com.enonic.wem.api.aggregation.Bucket;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.aggregation.Buckets;
import com.enonic.wem.api.aggregation.DateRangeBucket;
import com.enonic.wem.api.aggregation.StatsAggregation;

public class AggregationsFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( AggregationsFactory.class );

    public static Aggregations create( final org.elasticsearch.search.aggregations.Aggregations aggregations )
    {
        return doCreate( aggregations );
    }

    private static Aggregations doCreate( final org.elasticsearch.search.aggregations.Aggregations aggregations )
    {
        if ( aggregations == null )
        {
            return Aggregations.empty();
        }

        Aggregations.Builder aggregationsBuilder = new Aggregations.Builder();

        for ( final org.elasticsearch.search.aggregations.Aggregation aggregation : aggregations )
        {
            if ( aggregation instanceof Terms )
            {
                aggregationsBuilder.add( createTermsAggregationBuckets( (Terms) aggregation ) );
            }
            else if ( aggregation instanceof DateRange )
            {
                aggregationsBuilder.add( createDateRangeBuckets( (DateRange) aggregation ) );
            }
            else if ( aggregation instanceof DateHistogram )
            {
                aggregationsBuilder.add( createDateHistogramBuckets( (DateHistogram) aggregation ) );
            }
            else if ( aggregation instanceof Histogram )
            {
                aggregationsBuilder.add( createHistogramBuckets( (Histogram) aggregation ) );
            }
            else if ( aggregation instanceof Stats )
            {
                aggregationsBuilder.add( createStatsMetrics( (Stats) aggregation ) );
            }
            else
            {
                throw new IllegalArgumentException( "Aggregation translator for " + aggregation.getClass().getName() + " not implemented" );
            }
        }

        return aggregationsBuilder.build();
    }

    private static StatsAggregation createStatsMetrics( final Stats stats )
    {
        return StatsAggregation.create( stats.getName() ).
            avg( stats.getAvg() ).
            count( stats.getCount() ).
            max( stats.getMax() ).
            min( stats.getMin() ).
            sum( stats.getSum() ).
            build();
    }

    private static BucketAggregation createTermsAggregationBuckets( final Terms termsAggregation )
    {
        return BucketAggregation.bucketAggregation( termsAggregation.getName() ).
            buckets( createFromTerms( termsAggregation.getBuckets() ) ).
            build();
    }

    private static BucketAggregation createDateRangeBuckets( final DateRange dateRangeAggregation )
    {
        return BucketAggregation.bucketAggregation( dateRangeAggregation.getName() ).
            buckets( createFromDateRange( dateRangeAggregation.getBuckets() ) ).
            build();
    }

    private static BucketAggregation createDateHistogramBuckets( final DateHistogram dateHistogram )
    {
        return BucketAggregation.bucketAggregation( dateHistogram.getName() ).
            buckets( createFromDateHistogram( dateHistogram.getBuckets() ) ).
            build();
    }

    private static BucketAggregation createHistogramBuckets( final Histogram histogram )
    {
        return BucketAggregation.bucketAggregation( histogram.getName() ).
            buckets( createFromHistogram( histogram.getBuckets() ) ).
            build();
    }


    private static Buckets createFromTerms( final Collection<Terms.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final Terms.Bucket bucket : buckets )
        {
            createAndAddBucket( bucketsBuilder, bucket );
        }

        return bucketsBuilder.build();
    }

    private static Buckets createFromDateRange( final Collection<? extends DateRange.Bucket> buckets )
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

    private static Buckets createFromDateHistogram( final Collection<? extends DateHistogram.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final DateHistogram.Bucket bucket : buckets )
        {
            createAndAddBucket( bucketsBuilder, bucket );
        }

        return bucketsBuilder.build();
    }

    private static Buckets createFromHistogram( final Collection<? extends Histogram.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final Histogram.Bucket bucket : buckets )
        {
            createAndAddBucket( bucketsBuilder, bucket );
        }

        return bucketsBuilder.build();
    }

    private static void createAndAddBucket( final Buckets.Builder bucketsBuilder, final MultiBucketsAggregation.Bucket bucket )
    {
        final Bucket.Builder builder = Bucket.create().
            key( bucket.getKey() ).
            docCount( bucket.getDocCount() );

        doAddSubAggregations( bucket, builder );

        bucketsBuilder.add( builder.build() );
    }

    private static void doAddSubAggregations( final HasAggregations bucket, final Bucket.Builder builder )
    {
        final org.elasticsearch.search.aggregations.Aggregations subAggregations = bucket.getAggregations();

        builder.addAggregations( doCreate( subAggregations ) );
    }
}


