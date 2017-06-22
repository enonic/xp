package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.query.expr.FunctionExpr;

class PathMatchFunction
{
    public static QueryBuilder create( final FunctionExpr functionExpr )
    {
        final PathMatchFunctionArguments arguments = PathMatchFunctionArguments.create( functionExpr.getArguments() );
        return new MatchQueryBuilder( arguments.getFieldName(), arguments.getPath() ).
            minimumShouldMatch( arguments.getMinimumMatch() + "" );
    }
}
