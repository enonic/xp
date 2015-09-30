package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountBuilder;

import com.enonic.wem.repo.internal.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.wem.repo.internal.elasticsearch.query.translator.builder.AbstractBuilderFactory;
import com.enonic.wem.repo.internal.index.IndexValueType;
import com.enonic.xp.query.aggregation.metric.ValueCountAggregationQuery;

class ValueCountAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public ValueCountAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final ValueCountAggregationQuery valueCountAggregationQuery )
    {
        return new ValueCountBuilder( valueCountAggregationQuery.getName() ).
            field( fieldNameResolver.resolve( valueCountAggregationQuery.getFieldName(), IndexValueType.STRING ) );
    }

}
