package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramBuilder;

import com.enonic.xp.query.aggregation.HistogramAggregationQuery;
import com.enonic.xp.repo.impl.index.query.IndexQueryFieldNameResolver;

class NumericHistogramAggregationQueryBuilderFactory
{

    static AggregationBuilder create( final HistogramAggregationQuery aggregationQuery )
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
