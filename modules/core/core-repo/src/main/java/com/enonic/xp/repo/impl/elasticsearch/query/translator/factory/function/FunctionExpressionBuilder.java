package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.query.expr.FunctionExpr;

public class FunctionExpressionBuilder
{
    public static QueryBuilder build( final FunctionExpr function )
    {
        final String functionName = function.getName();

        if ( "fulltext".equals( functionName ) )
        {
            return FulltextFunction.create( function );
        }
        else if ( "ngram".equals( functionName ) )
        {
            return NGramFunction.create( function );
        }
        else if ( "range".equals( functionName ) )
        {
            return RangeFunction.create( function );
        }
        else if ( "pathMatch".equals( functionName ) )
        {
            return PathMatchFunction.create( function );
        }

        throw new UnsupportedOperationException( "Function '" + functionName + "' is not supported" );
    }
}
