package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;

import com.enonic.xp.query.aggregation.MissingAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class MissingAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    MissingAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AggregationBuilder create( final MissingAggregationQuery aggregationQuery )
    {
        final String fieldName = fieldNameResolver.resolve( aggregationQuery.getFieldName(), IndexValueType.STRING );

        return new MissingBuilder( aggregationQuery.getName() ).field( fieldName );
    }
}
