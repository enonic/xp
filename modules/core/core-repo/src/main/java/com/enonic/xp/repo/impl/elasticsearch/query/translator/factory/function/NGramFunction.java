package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.SimpleQueryStringAsciiFolder;

class NGramFunction
    extends AbstractSimpleQueryStringFunction
{
    public static QueryBuilder create( final FunctionExpr functionExpr )
    {
        final NGramFunctionArguments arguments = new NGramFunctionArguments( functionExpr.getArguments() );

        final String searchString = SimpleQueryStringAsciiFolder.foldFuzzyTerms( arguments.getSearchString() );

        final SimpleQueryStringBuilder builder = new SimpleQueryStringBuilder( searchString ).
            defaultOperator( arguments.getOperator() ).
            analyzer( arguments.getAnalyzer() ).
            analyzeWildcard( true );

        appendQueryFieldNames( arguments, builder );

        return builder;
    }

}
