package com.enonic.wem.core.index.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.api.query.expr.DynamicConstraintExpr;
import com.enonic.wem.api.query.expr.FunctionExpr;
import com.enonic.wem.core.index.query.function.FunctionQueryBuilderFactory;

public class DynamicQueryFactory
{
    private FunctionQueryBuilderFactory functionQueryBuilderFactory = new FunctionQueryBuilderFactory();

    public QueryBuilder create( final DynamicConstraintExpr expression )
    {
        final FunctionExpr function = expression.getFunction();

        if ( function != null )
        {
            return functionQueryBuilderFactory.create( function );
        }

        return null;
    }


}
