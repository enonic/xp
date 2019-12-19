package com.enonic.xp.repo.impl.elasticsearch.aggregation;


import java.time.Instant;
import java.time.ZonedDateTime;

import org.elasticsearch.search.aggregations.HasAggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.composite.ParsedComposite;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.bucket.range.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.range.InternalGeoDistance;
import org.elasticsearch.search.aggregations.bucket.range.ParsedDateRange;
import org.elasticsearch.search.aggregations.bucket.range.ParsedGeoDistance;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.Min;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.elasticsearch.search.aggregations.metrics.Stats;
import org.elasticsearch.search.aggregations.metrics.ValueCount;
import org.joda.time.DateTime;

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
            else if ( aggregation instanceof ParsedComposite )
            {
                aggregationsBuilder.add( CompositeAggregationFactory.create( (ParsedComposite) aggregation ) );
            }
            else if ( aggregation instanceof ParsedStringTerms )
            {
                aggregationsBuilder.add( TermsAggregationFactory.create( (ParsedStringTerms) aggregation ) );
            }
            else if ( aggregation instanceof InternalGeoDistance )
            {
                aggregationsBuilder.add( GeoDistanceAggregationFactory.create( (InternalGeoDistance) aggregation ) );
            }
            else if ( aggregation instanceof InternalDateRange )
            {
                aggregationsBuilder.add( DateRangeAggregationFactory.create( (InternalDateRange) aggregation ) );
            }
            else if ( aggregation instanceof Range )
            {
                if ( aggregation instanceof ParsedGeoDistance )
                {
                    aggregationsBuilder.add( ParsedGeoDistanceAggregationFactory.create( (ParsedGeoDistance) aggregation ) );
                }
                else if ( aggregation instanceof ParsedDateRange )
                {
                    aggregationsBuilder.add( ParsedDateRangeAggregationFactory.create( (ParsedDateRange) aggregation ) );
                }
                else
                {
                    aggregationsBuilder.add( NumericRangeAggregationFactory.create( (Range) aggregation ) );
                }
            }
            else if ( aggregation instanceof InternalHistogram )
            {
                aggregationsBuilder.add( DateHistogramAggregationFactory.create( (InternalHistogram) aggregation ) );
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
            else if ( aggregation instanceof ParsedCardinality )
            {
                aggregationsBuilder.add( CardinalityAggregationFactory.create( (ParsedCardinality) aggregation ) );
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
            key( bucket.getKeyAsString() ).
            docCount( bucket.getDocCount() );

        doAddSubAggregations( bucket, builder );

        bucketsBuilder.add( builder.build() );
    }

    static void doAddSubAggregations( final HasAggregations bucket, final Bucket.Builder builder )
    {
        final org.elasticsearch.search.aggregations.Aggregations subAggregations = bucket.getAggregations();

        builder.addAggregations( doCreate( subAggregations ) );
    }

    static Instant toInstant( final DateTime dateTime )
    {
        return dateTime == null ? null : java.time.Instant.ofEpochMilli( dateTime.getMillis() );
    }

    static Instant toInstant( final ZonedDateTime dateTime )
    {
        return dateTime == null ? null : dateTime.toInstant();
    }

}


