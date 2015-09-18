package com.enonic.xp.repo.impl.elasticsearch.aggregation;


import org.elasticsearch.search.aggregations.HasAggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRange;
import org.elasticsearch.search.aggregations.bucket.range.geodistance.GeoDistance;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.Buckets;


public class AggregationsFactory
{
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
                aggregationsBuilder.add( TermsAggregationFactory.create( (Terms) aggregation ) );
            }
            else if ( aggregation instanceof GeoDistance )
            {
                aggregationsBuilder.add( GeoDistanceAggregationFactory.create( (GeoDistance) aggregation ) );
            }
            else if ( aggregation instanceof DateRange )
            {
                aggregationsBuilder.add( DateRangeAggregationFactory.create( (DateRange) aggregation ) );
            }
            else if ( aggregation instanceof Range )
            {
                aggregationsBuilder.add( NumericRangeAggregationFactory.create( (Range) aggregation ) );
            }
            else if ( aggregation instanceof DateHistogram )
            {
                aggregationsBuilder.add( DateHistogramAggregationFactory.create( (DateHistogram) aggregation ) );
            }
            else if ( aggregation instanceof Histogram )
            {
                aggregationsBuilder.add( HistogramAggregationFactory.create( (Histogram) aggregation ) );
            }
            else if ( aggregation instanceof Stats )
            {
                aggregationsBuilder.add( StatsAggregationFactory.create( (Stats) aggregation ) );
            }
            else if ( aggregation instanceof ValueCount )
            {
                aggregationsBuilder.add( ValueCountAggregationFactory.create( (ValueCount) aggregation ) );
            }
            else if ( aggregation instanceof Min )
            {
                aggregationsBuilder.add( MinAggregationFactory.create( (Min) aggregation ) );
            }
            else if ( aggregation instanceof Max )
            {
                aggregationsBuilder.add( MaxAggregationFactory.create( (Max) aggregation ) );
            }
            else
            {
                throw new IllegalArgumentException( "Aggregation translator for " + aggregation.getClass().getName() + " not implemented" );
            }
        }

        return aggregationsBuilder.build();
    }

    static void createAndAddBucket( final Buckets.Builder bucketsBuilder, final MultiBucketsAggregation.Bucket bucket )
    {
        final Bucket.Builder builder = Bucket.create().
            key( bucket.getKey() ).
            docCount( bucket.getDocCount() );

        doAddSubAggregations( bucket, builder );

        bucketsBuilder.add( builder.build() );
    }

    static void doAddSubAggregations( final HasAggregations bucket, final Bucket.Builder builder )
    {
        final org.elasticsearch.search.aggregations.Aggregations subAggregations = bucket.getAggregations();

        builder.addAggregations( doCreate( subAggregations ) );
    }
}


