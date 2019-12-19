package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.IncludeExclude;
import org.elasticsearch.search.aggregations.bucket.terms.RareTermsAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;

import com.enonic.xp.query.aggregation.RareTermsAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

class RareTermsAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public RareTermsAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final RareTermsAggregationQuery aggregationQuery )
    {
        final String fieldName = fieldNameResolver.resolve( aggregationQuery.getFieldName(), IndexValueType.STRING );

        final RareTermsAggregationBuilder rareTermsBuilder =
            new RareTermsAggregationBuilder( aggregationQuery.getName(), ValueType.STRING ).
                maxDocCount( aggregationQuery.getMaxDocCount() ).
                includeExclude( new IncludeExclude( aggregationQuery.getPartition(), aggregationQuery.getNumOfPartitions() ) ).
                field( fieldName );

        return rareTermsBuilder;
    }
}
