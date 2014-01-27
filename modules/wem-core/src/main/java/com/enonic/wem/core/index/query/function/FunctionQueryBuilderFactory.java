package com.enonic.wem.core.index.query.function;

import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.google.common.base.Strings;

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
        else if ( "ngram".equals( functionName ) )
        {
            return createNGram( function );
        }

        throw new UnsupportedOperationException( "Function '" + functionName + "' is not supported" );
    }

    private QueryBuilder createFulltext( final FunctionExpr functionExpr )
    {
        final FulltextFunctionArguments arguments = new FulltextFunctionArguments( functionExpr.getArguments() );

        if ( Strings.isNullOrEmpty( arguments.getSearchString() ) )
        {
            return new MatchAllQueryBuilder();
        }

        final String baseFieldName = arguments.getFieldName();

        final String queryFieldName = IndexQueryFieldNameResolver.resolveAnalyzedFieldName( baseFieldName );

        SimpleQueryStringBuilder builder = new SimpleQueryStringBuilder( arguments.getSearchString() ).
            field( queryFieldName ).
            defaultOperator( arguments.getOperator() );

        return builder;
    }


    private QueryBuilder createNGram( final FunctionExpr functionExpr )
    {
        final NGramFunctionArguments arguments = new NGramFunctionArguments( functionExpr.getArguments() );

        final String baseFieldName = arguments.getFieldName();

        final String queryFieldName = IndexQueryFieldNameResolver.resolveNGramFieldName( baseFieldName );

        SimpleQueryStringBuilder builder = new SimpleQueryStringBuilder( arguments.getSearchString() ).
            field( queryFieldName ).
            defaultOperator( arguments.getOperator() );

        return builder;
    }

}
