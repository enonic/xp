package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
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

    private QueryBuilderFactory.Builder createQuery( final NodeQuery nodeQuery )
    {
        return QueryBuilderFactory.newBuilder().
            queryExpr( effectiveQueryExpr( nodeQuery ) ).
            addQueryFilters( nodeQuery.getQueryFilters() ).
            fieldNameResolver( this.fieldNameResolver );
    }

    // a recursive parent is a path-prefix constraint, so it goes into the query expression instead of the parent filter
    private static QueryExpr effectiveQueryExpr( final NodeQuery nodeQuery )
    {
        final QueryExpr queryExpr = nodeQuery.getQuery();

        if ( nodeQuery.getParent() == null || !nodeQuery.isRecursive() )
        {
            return queryExpr;
        }

        final NodePath parent = nodeQuery.getParent();
        final CompareExpr descendantsExpr = parent.isRoot()
            ? CompareExpr.neq( FieldExpr.from( NodeIndexPath.PATH ), ValueExpr.string( NodePath.ROOT.toString() ) )
            : CompareExpr.like( FieldExpr.from( NodeIndexPath.PATH ), ValueExpr.string( parent + "/*" ) );

        final ConstraintExpr constraintExpr = queryExpr != null && queryExpr.getConstraint() != null
            ? LogicalExpr.and( queryExpr.getConstraint(), descendantsExpr )
            : descendantsExpr;

        return QueryExpr.from( constraintExpr, queryExpr != null ? queryExpr.getOrderList() : List.of() );
    }

    private void addParentFilter( final NodeQuery nodeQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeQuery.getParent() != null && !nodeQuery.isRecursive() )
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
