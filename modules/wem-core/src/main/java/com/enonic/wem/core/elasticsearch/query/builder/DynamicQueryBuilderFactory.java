package com.enonic.wem.core.elasticsearch.query.builder;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.api.query.expr.DynamicConstraintExpr;
import com.enonic.wem.api.query.expr.FunctionExpr;
import com.enonic.wem.core.elasticsearch.query.builder.function.FunctionQueryBuilderFactory;

public class DynamicQueryBuilderFactory
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
