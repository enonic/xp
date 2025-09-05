package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.query.Query;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.QueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

class NodeQueryTranslator
    implements QueryTypeTranslator
{
    private final QueryFieldNameResolver fieldNameResolver = SearchQueryFieldNameResolver.INSTANCE;

    private final NodeQuery nodeQuery;

    NodeQueryTranslator( final NodeQuery nodeQuery )
    {
        this.nodeQuery = nodeQuery;
    }

    @Override
    public int getBatchSize()
    {
        return nodeQuery.getBatchSize();
    }

    @Override
    public SearchOptimizer getSearchOptimizer()
    {
        return nodeQuery.getSearchOptimizer();
    }

    @Override
    public QueryBuilder createQueryBuilder( final Filters additionalFilters )
    {
        final QueryBuilderFactory.Builder queryBuilderBuilder = createQuery( nodeQuery );
        queryBuilderBuilder.addQueryFilters( additionalFilters );
        addParentFilter( nodeQuery, queryBuilderBuilder );

        return queryBuilderBuilder.build().create();
    }

    private QueryBuilderFactory.Builder createQuery( final Query nodeQuery )
    {
        return QueryBuilderFactory.newBuilder().
            queryExpr( nodeQuery.getQuery() ).
            addQueryFilters( nodeQuery.getQueryFilters() ).
            fieldNameResolver( this.fieldNameResolver );
    }

    private void addParentFilter( final NodeQuery nodeQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeQuery.getParent() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.PARENT_PATH.getPath() ).
                addValue( ValueFactory.newString( nodeQuery.getParent().toString() ) ).
                build() );
        }
    }

    @Override
    public QueryFieldNameResolver getFieldNameResolver()
    {
        return fieldNameResolver;
    }
}
