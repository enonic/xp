package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import java.util.List;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.TermsValuesSourceBuilder;

import com.enonic.xp.query.aggregation.CompositeAggregationQuery;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

class CompositeAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public CompositeAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final CompositeAggregationQuery aggregationQuery )
    {
        final String fieldName = fieldNameResolver.resolve( aggregationQuery.getFieldName(), IndexValueType.STRING );

        TermsValuesSourceBuilder versionSourceBuilder =
            new TermsValuesSourceBuilder( BranchIndexPath.VERSION_ID.toString() ).field( BranchIndexPath.VERSION_ID.toString() );
        TermsValuesSourceBuilder nodeSourceBuilder =
            new TermsValuesSourceBuilder( BranchIndexPath.NODE_ID.toString() ).field( BranchIndexPath.NODE_ID.toString() );

        final CompositeAggregationBuilder compositeBuilder =
            new CompositeAggregationBuilder( aggregationQuery.getName(), List.of( versionSourceBuilder, nodeSourceBuilder ) ).
                aggregateAfter( aggregationQuery.getAfter() ).
                size( aggregationQuery.getSize() );

        return compositeBuilder;
    }

}
