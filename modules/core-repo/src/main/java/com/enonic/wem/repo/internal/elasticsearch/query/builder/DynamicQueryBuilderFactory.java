package com.enonic.wem.repo.internal.elasticsearch.query.builder;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.core.query.expr.DynamicConstraintExpr;
import com.enonic.xp.core.query.expr.FunctionExpr;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.function.FunctionQueryBuilderFactory;

class DynamicQueryBuilderFactory
{
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
