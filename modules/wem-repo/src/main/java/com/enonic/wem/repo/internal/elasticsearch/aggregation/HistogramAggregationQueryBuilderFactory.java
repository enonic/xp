package com.enonic.wem.repo.internal.elasticsearch.aggregation;


import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;

import com.enonic.wem.api.query.aggregation.AbstractHistogramAggregationQuery;
import com.enonic.wem.api.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

class HistogramAggregationQueryBuilderFactory
{
    public static AggregationBuilder create( final AbstractHistogramAggregationQuery histogramAggregationQuery )
    {
        if ( histogramAggregationQuery instanceof DateHistogramAggregationQuery )
        {
            return createDateHistogram( (DateHistogramAggregationQuery) histogramAggregationQuery );
        }

        throw new IllegalArgumentException( "Unknow histogramAggregationQuery type: " + histogramAggregationQuery.getClass().getName() );
    }

    private static AggregationBuilder createDateHistogram( final DateHistogramAggregationQuery aggregationQuery )
    {
        final DateHistogramBuilder builder = new DateHistogramBuilder( aggregationQuery.getName() ).
            interval( new DateHistogram.Interval( aggregationQuery.getInterval().toString() ) ).
            field( IndexQueryFieldNameResolver.resolveDateTimeFieldName( aggregationQuery.getFieldName() ) ).
            minDocCount( aggregationQuery.getMinDocCount() );

        if ( aggregationQuery.getFormat() != null )
        {
            builder.format( aggregationQuery.getFormat() );
        }

        return builder;
    }
}
