package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramBuilder;

import com.enonic.wem.api.query.aggregation.AbstractHistogramAggregationQuery;
import com.enonic.wem.api.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.wem.api.query.aggregation.HistogramAggregationQuery;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

class HistogramAggregationQueryBuilderFactory
{
    static AggregationBuilder create( final AbstractHistogramAggregationQuery histogramAggregationQuery )
    {
        if ( histogramAggregationQuery instanceof DateHistogramAggregationQuery )
        {
            return createDateHistogram( (DateHistogramAggregationQuery) histogramAggregationQuery );
        }
        else if ( histogramAggregationQuery instanceof HistogramAggregationQuery )
        {
            return createHistogram( (HistogramAggregationQuery) histogramAggregationQuery );
        }

        throw new IllegalArgumentException( "Unknow histogramAggregationQuery type: " + histogramAggregationQuery.getClass().getName() );
    }

    private static AggregationBuilder createDateHistogram( final DateHistogramAggregationQuery aggregationQuery )
    {
        final DateHistogramBuilder builder = new DateHistogramBuilder( aggregationQuery.getName() ).
            interval( new DateHistogram.Interval( aggregationQuery.getInterval() ) ).
            field( IndexQueryFieldNameResolver.resolveDateTimeFieldName( aggregationQuery.getFieldName() ) );

        if ( aggregationQuery.getFormat() != null )
        {
            builder.format( aggregationQuery.getFormat() );
        }

        if ( aggregationQuery.getMinDocCount() != null )
        {
            builder.minDocCount( aggregationQuery.getMinDocCount() );
        }

        return builder;
    }

    private static AggregationBuilder createHistogram( final HistogramAggregationQuery aggregationQuery )
    {
        final HistogramBuilder builder = new HistogramBuilder( aggregationQuery.getName() ).
            interval( aggregationQuery.getInterval() ).
            field( IndexQueryFieldNameResolver.resolveNumericFieldName( aggregationQuery.getFieldName() ) );

        if ( aggregationQuery.getMinDocCount() != null )
        {
            builder.minDocCount( aggregationQuery.getMinDocCount() );
        }

        if ( aggregationQuery.setExtendedBounds() )
        {
            builder.extendedBounds( aggregationQuery.getExtendedBoundMin(), aggregationQuery.getExtendedBoundMax() );
        }

        if ( aggregationQuery.getOrder() != null )
        {
            builder.order( translate( aggregationQuery.getOrder() ) );
        }

        return builder;
    }

    private static Histogram.Order translate( HistogramAggregationQuery.Order order )
    {
        switch ( order )
        {
            case KEY_ASC:
                return Histogram.Order.KEY_ASC;
            case KEY_DESC:
                return Histogram.Order.KEY_DESC;
            case COUNT_DESC:
                return Histogram.Order.COUNT_DESC;
            case COUNT_ASC:
                return Histogram.Order.COUNT_ASC;
        }

        throw new IllegalArgumentException( "Unknown order value for histogram: " + order.name() );
    }
}
