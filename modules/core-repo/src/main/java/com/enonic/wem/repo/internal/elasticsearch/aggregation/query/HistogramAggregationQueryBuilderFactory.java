package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;

import com.enonic.xp.query.aggregation.AbstractHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.HistogramAggregationQuery;

class HistogramAggregationQueryBuilderFactory
{
    static AggregationBuilder create( final AbstractHistogramAggregationQuery histogramAggregationQuery )
    {
        if ( histogramAggregationQuery instanceof DateHistogramAggregationQuery )
        {
            return DateHistogramAggregationQueryFactory.create( (DateHistogramAggregationQuery) histogramAggregationQuery );
        }
        else if ( histogramAggregationQuery instanceof HistogramAggregationQuery )
        {
            return NumericHistogramAggregationQueryBuilderFactory.create( (HistogramAggregationQuery) histogramAggregationQuery );
        }

        throw new IllegalArgumentException( "Unknow histogramAggregationQuery type: " + histogramAggregationQuery.getClass().getName() );
    }

}
