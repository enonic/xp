package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;

import com.enonic.xp.query.expr.FunctionExpr;

class RangeFunction
{
    public static QueryBuilder create( final FunctionExpr functionExpr )
    {
        final RangeFunctionArg arguments = RangeFunctionArgsFactory.create( functionExpr.getArguments() );

        return new RangeQueryBuilder( arguments.getFieldName() ).
            from( arguments.getFrom() ).
            to( arguments.getTo() ).
            includeLower( arguments.includeFrom() ).
            includeUpper( arguments.includeTo() );
    }
}
