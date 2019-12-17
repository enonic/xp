package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.enonic.xp.query.expr.FunctionExpr;

class StemmedFunction
    extends AbstractSimpleQueryStringFunction
{
    public static QueryBuilder create( final FunctionExpr functionExpr )
    {
        final StemmedFunctionArguments arguments = new StemmedFunctionArguments( functionExpr.getArguments() );

        final SimpleQueryStringBuilder builder = new SimpleQueryStringBuilder( arguments.getSearchString() ).
            defaultOperator( arguments.getOperator() ).
            analyzer( arguments.getAnalyzer() ).
            analyzeWildcard( true );

        appendQueryFieldNames( arguments, builder );

        return builder;

    }
}
