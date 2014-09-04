package com.enonic.wem.core.index.query.builder;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.api.query.expr.DynamicConstraintExpr;
import com.enonic.wem.api.query.expr.FunctionExpr;
import com.enonic.wem.core.index.query.function.FunctionQueryBuilderFactory;

public class DynamicQueryFactory
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
