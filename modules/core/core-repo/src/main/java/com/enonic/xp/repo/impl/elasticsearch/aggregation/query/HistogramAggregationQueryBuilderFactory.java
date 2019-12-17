package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;

import com.enonic.xp.query.aggregation.AbstractHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.HistogramAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

class HistogramAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public HistogramAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final AbstractHistogramAggregationQuery histogramAggregationQuery )
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
