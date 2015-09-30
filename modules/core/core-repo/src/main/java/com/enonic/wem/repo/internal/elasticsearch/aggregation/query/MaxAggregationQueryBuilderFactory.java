package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.MaxBuilder;

import com.enonic.wem.repo.internal.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.wem.repo.internal.elasticsearch.query.translator.builder.AbstractBuilderFactory;
import com.enonic.wem.repo.internal.index.IndexValueType;
import com.enonic.xp.query.aggregation.metric.MaxAggregationQuery;

class MaxAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{

    public MaxAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final MaxAggregationQuery aggregationQuery )
    {
        return new MaxBuilder( aggregationQuery.getName() ).
            field( fieldNameResolver.resolve( aggregationQuery.getFieldName(), IndexValueType.NUMBER ) );
    }

}
