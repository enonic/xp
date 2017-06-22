package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.QueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.StoreQueryFieldNameResolver;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.NodeVersionQuery;

class NodeVersionQueryTranslator
    implements QueryTypeTranslator
{
    private final NodeVersionQuery query;

    private final QueryFieldNameResolver fieldNameResolver = new StoreQueryFieldNameResolver();

    NodeVersionQueryTranslator( final NodeVersionQuery query )
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

        addNodeIdFilter( this.query, queryBuilderBuilder );

        return queryBuilderBuilder.build().create();
    }

    private void addNodeIdFilter( final NodeVersionQuery nodeVersionQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeVersionQuery.getNodeId() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( VersionIndexPath.NODE_ID.getPath() ).
                addValue( ValueFactory.newString( nodeVersionQuery.getNodeId().toString() ) ).
                build() );
        }
    }
}
