package com.enonic.wem.core.index.query.function;

import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.api.query.expr.FunctionExpr;
import com.enonic.wem.core.index.query.IndexQueryFieldNameResolver;

public class FunctionQueryBuilderFactory
{
    public QueryBuilder create( final FunctionExpr function )
    {
        final String functionName = function.getName();

        if ( "fulltext".equals( functionName ) )
        {
            return createFulltext( function );
        }

        throw new UnsupportedOperationException( "Function '" + functionName + "' is not supported" );
    }

    private QueryBuilder createFulltext( final FunctionExpr functionExpr )
    {
        final FulltextFunctionArguments arguments = new FulltextFunctionArguments( functionExpr.getArguments() );

        final String baseFieldName = arguments.getFieldName();

        final String queryFieldName = IndexQueryFieldNameResolver.resolveStringFieldName( baseFieldName );

        MatchQueryBuilder builder = new MatchQueryBuilder( queryFieldName, arguments.getSearchString() );
        builder.operator( arguments.getOperator() );

        return builder;
    }

}
