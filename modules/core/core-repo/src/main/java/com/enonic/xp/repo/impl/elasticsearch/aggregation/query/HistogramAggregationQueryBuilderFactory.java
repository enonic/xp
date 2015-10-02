package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;

import com.enonic.xp.query.aggregation.AbstractHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.HistogramAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.AbstractBuilderFactory;

class HistogramAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public HistogramAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AggregationBuilder create( final AbstractHistogramAggregationQuery histogramAggregationQuery )
    {
        if ( histogramAggregationQuery instanceof DateHistogramAggregationQuery )
        {
            return new DateHistogramAggregationQueryFactory( fieldNameResolver ).create(
                (DateHistogramAggregationQuery) histogramAggregationQuery );
        }
        else if ( histogramAggregationQuery instanceof HistogramAggregationQuery )
        {
            return new NumericHistogramAggregationQueryBuilderFactory( fieldNameResolver ).create(
                (HistogramAggregationQuery) histogramAggregationQuery );
        }

        throw new IllegalArgumentException( "Unknow histogramAggregationQuery type: " + histogramAggregationQuery.getClass().getName() );
    }

}
