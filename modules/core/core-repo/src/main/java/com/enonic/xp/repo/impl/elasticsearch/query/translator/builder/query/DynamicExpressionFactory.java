package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.FunctionExpr;

class DynamicExpressionFactory
{
    public static QueryBuilder build( final DynamicConstraintExpr expression )
    {
        final FunctionExpr function = expression.getFunction();

        if ( function != null )
        {
            return FunctionExpressionBuilder.build( function );
        }

        return null;
    }


}
