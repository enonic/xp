package com.enonic.wem.repo.internal.elasticsearch.aggregation;


import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRange;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.aggregation.Aggregations;
import com.enonic.wem.api.aggregation.BucketAggregation;

public class AggregationsFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( AggregationsFactory.class );

    public static Aggregations create( final org.elasticsearch.search.aggregations.Aggregations aggregations )
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
            else
            {
                LOG.warn( "Aggregation translator for " + aggregation.getClass().getName() + " not implemented, skipping" );
            }
        }

        return aggregationsBuilder.build();
    }

    private static BucketAggregation createTermsAggregationBuckets( final Terms termsAggregation )
    {
        return BucketAggregation.bucketAggregation( termsAggregation.getName() ).
            buckets( BucketsFactory.createFromTerms( termsAggregation.getBuckets() ) ).
            build();
    }

    private static BucketAggregation createDateRangeBuckets( final DateRange dateRangeAggregation )
    {
        return BucketAggregation.bucketAggregation( dateRangeAggregation.getName() ).
            buckets( BucketsFactory.createFromDateRange( dateRangeAggregation.getBuckets() ) ).
            build();
    }

    private static BucketAggregation createDateHistogramBuckets( final DateHistogram dateHistogram )
    {
        return BucketAggregation.bucketAggregation( dateHistogram.getName() ).
            buckets( BucketsFactory.createFromDateHistogram( dateHistogram.getBuckets() ) ).
            build();
    }


}


