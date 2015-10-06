package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;

class DynamicQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public DynamicQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public static QueryBuilder create( final DynamicConstraintExpr expression )
    {
        final FunctionExpr function = expression.getFunction();

        if ( function != null )
        {
            return FunctionQueryBuilderFactory.create( function );
        }

        return null;
    }


}
