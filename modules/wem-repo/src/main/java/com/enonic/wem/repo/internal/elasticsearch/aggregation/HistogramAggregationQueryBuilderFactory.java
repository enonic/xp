package com.enonic.wem.repo.internal.elasticsearch.aggregation;


import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;

import com.enonic.wem.api.query.aggregation.AbstractHistogramAggregationQuery;
import com.enonic.wem.api.query.aggregation.DateHistogramAggregationsQuery;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

class HistogramAggregationQueryBuilderFactory
{
    public static AggregationBuilder create( final AbstractHistogramAggregationQuery histogramAggregationQuery )
    {
        if ( histogramAggregationQuery instanceof DateHistogramAggregationsQuery )
        {
            return createDateHistogram( (DateHistogramAggregationsQuery) histogramAggregationQuery );
        }

        throw new IllegalArgumentException( "Unknow histogramAggregationQuery type: " + histogramAggregationQuery.getClass().getName() );
    }

    private static AggregationBuilder createDateHistogram( final DateHistogramAggregationsQuery dateHistogramAggregationsQuery )
    {
        final DateHistogramBuilder builder = new DateHistogramBuilder( dateHistogramAggregationsQuery.getName() ).
            interval( new DateHistogram.Interval( dateHistogramAggregationsQuery.getInterval().toString() ) ).
            field( IndexQueryFieldNameResolver.resolveDateTimeFieldName( dateHistogramAggregationsQuery.getFieldName() ) ).
            minDocCount( 0 );

        if ( dateHistogramAggregationsQuery.getFormat() != null )
        {
            builder.format( dateHistogramAggregationsQuery.getFormat() );
        }

        return builder;
    }
}
