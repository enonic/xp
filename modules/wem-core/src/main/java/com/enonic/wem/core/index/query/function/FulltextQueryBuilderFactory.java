package com.enonic.wem.core.index.query.function;

import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.query.expr.FunctionExpr;

public class FulltextQueryBuilderFactory
{
    public static QueryBuilder create( final FunctionExpr functionExpr )
    {
        final FulltextFunctionArguments arguments = new FulltextFunctionArguments( functionExpr.getArguments() );

        MatchQueryBuilder builder = new MatchQueryBuilder( arguments.getFieldName(), arguments.getSearchString() );
        builder.operator( arguments.getOperator() );

        return builder;
    }


}
