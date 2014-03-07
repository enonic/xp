package com.enonic.wem.core.index.query.function;

import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.google.common.base.Strings;

import com.enonic.wem.api.query.expr.FunctionExpr;

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

        SimpleQueryStringBuilder builder = new SimpleQueryStringBuilder( arguments.getSearchString() ).
            defaultOperator( arguments.getOperator() );

        appendQueryFieldNames( arguments, builder );

        return builder;
    }

    private QueryBuilder createNGram( final FunctionExpr functionExpr )
    {
        final NGramFunctionArguments arguments = new NGramFunctionArguments( functionExpr.getArguments() );

        SimpleQueryStringBuilder builder = new SimpleQueryStringBuilder( arguments.getSearchString() ).
            defaultOperator( arguments.getOperator() );

        appendQueryFieldNames( arguments, builder );

        return builder;
    }

    private void appendQueryFieldNames( final AbstractSimpleQueryStringFunction arguments, final SimpleQueryStringBuilder builder )
    {
        for ( final WeightedQueryFieldName weightedQueryFieldName : arguments.getWeightedQueryFieldName() )
        {
            final String queryFieldName = arguments.resolveQueryFieldName( weightedQueryFieldName.getBaseFieldName() );

            if ( weightedQueryFieldName.getWeight() != null )
            {
                builder.field( queryFieldName, weightedQueryFieldName.getWeight() );
            }
            else
            {
                builder.field( queryFieldName );
            }
        }
    }


}
