package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder;

import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.google.common.base.Strings;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.repo.impl.elasticsearch.function.AbstractSimpleQueryStringFunction;
import com.enonic.xp.repo.impl.elasticsearch.function.FulltextFunctionArguments;
import com.enonic.xp.repo.impl.elasticsearch.function.NGramFunctionArguments;
import com.enonic.xp.repo.impl.elasticsearch.function.WeightedQueryFieldName;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;

public class FunctionQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public FunctionQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public static QueryBuilder create( final FunctionExpr function )
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

    private static QueryBuilder createFulltext( final FunctionExpr functionExpr )
    {
        final FulltextFunctionArguments arguments = new FulltextFunctionArguments( functionExpr.getArguments() );

        if ( Strings.isNullOrEmpty( arguments.getSearchString() ) )
        {
            return new MatchAllQueryBuilder();
        }

        SimpleQueryStringBuilder builder = new SimpleQueryStringBuilder( arguments.getSearchString() ).
            defaultOperator( arguments.getOperator() ).
            analyzer( arguments.getAnalyzer() );

        appendQueryFieldNames( arguments, builder );

        return builder;
    }

    private static QueryBuilder createNGram( final FunctionExpr functionExpr )
    {
        final NGramFunctionArguments arguments = new NGramFunctionArguments( functionExpr.getArguments() );

        SimpleQueryStringBuilder builder = new SimpleQueryStringBuilder( arguments.getSearchString() ).
            defaultOperator( arguments.getOperator() ).
            analyzer( arguments.getAnalyzer() );

        appendQueryFieldNames( arguments, builder );

        return builder;
    }

    private static void appendQueryFieldNames( final AbstractSimpleQueryStringFunction arguments, final SimpleQueryStringBuilder builder )
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
