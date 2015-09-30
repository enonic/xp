package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.StatsBuilder;

import com.enonic.wem.repo.internal.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.wem.repo.internal.elasticsearch.query.translator.builder.AbstractBuilderFactory;
import com.enonic.wem.repo.internal.index.IndexValueType;
import com.enonic.xp.query.aggregation.metric.StatsAggregationQuery;

class StatsAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public StatsAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final StatsAggregationQuery statsAggregationQuery )
    {
        return new StatsBuilder( statsAggregationQuery.getName() ).
            field( fieldNameResolver.resolve( statsAggregationQuery.getFieldName(), IndexValueType.NUMBER ) );
    }


}
