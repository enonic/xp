package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;

import com.enonic.wem.repo.internal.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.wem.repo.internal.elasticsearch.query.translator.builder.AbstractBuilderFactory;
import com.enonic.xp.query.aggregation.AbstractHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.HistogramAggregationQuery;

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
