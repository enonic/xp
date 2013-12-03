package com.enonic.wem.core.index.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.core.index.query.function.FunctionQueryBuilderFactory;
import com.enonic.wem.query.expr.DynamicConstraintExpr;
import com.enonic.wem.query.expr.FunctionExpr;

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
