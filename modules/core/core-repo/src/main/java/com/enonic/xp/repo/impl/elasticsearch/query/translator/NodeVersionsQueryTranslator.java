package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import java.util.stream.Collectors;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeVersionsQuery;
import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.QueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.StoreQueryFieldNameResolver;
import com.enonic.xp.repo.impl.version.VersionIndexPath;

class NodeVersionsQueryTranslator
    implements QueryTypeTranslator
{
    private final NodeVersionsQuery query;

    private final QueryFieldNameResolver fieldNameResolver = new StoreQueryFieldNameResolver();

    NodeVersionsQueryTranslator( final NodeVersionsQuery query )
    {
        this.query = query;
    }

    @Override
    public QueryFieldNameResolver getFieldNameResolver()
    {
        return this.fieldNameResolver;
    }

    @Override
    public int getBatchSize()
    {
        return query.getSize();
    }

    @Override
    public SearchOptimizer getSearchOptimizer()
    {
        return query.getSearchOptimizer();
    }

    @Override
    public QueryBuilder createQueryBuilder( final Filters additionalFilters )
    {
        final QueryBuilderFactory.Builder queryBuilderBuilder = QueryBuilderFactory.newBuilder().
            queryExpr( this.query.getQuery() ).
            addQueryFilters( this.query.getQueryFilters() ).
            addQueryFilters( additionalFilters ).
            fieldNameResolver( this.fieldNameResolver );

        addIdsFilter( this.query, queryBuilderBuilder );

        return queryBuilderBuilder.build().create();
    }

    private void addIdsFilter( final NodeVersionsQuery nodeVersionsQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeVersionsQuery.getIds() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( VersionIndexPath.VERSION_ID.getPath() ).
                addAllValues( nodeVersionsQuery.getIds().stream().map( ValueFactory::newString ).collect( Collectors.toSet() ) ).
                build() );
        }
    }
}
