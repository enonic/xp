package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.enonic.xp.query.expr.FunctionExpr;

import static com.google.common.base.Strings.isNullOrEmpty;

class FulltextFunction
    extends AbstractSimpleQueryStringFunction
{
    public static QueryBuilder create( final FunctionExpr functionExpr )
    {
        final FulltextFunctionArguments arguments = new FulltextFunctionArguments( functionExpr.getArguments() );

        if ( isNullOrEmpty( arguments.getSearchString() ) )
        {
            return new MatchAllQueryBuilder();
        }

        final SimpleQueryStringBuilder builder = new SimpleQueryStringBuilder( arguments.getSearchString() ).
            defaultOperator( arguments.getOperator() ).
            analyzer( arguments.getAnalyzer() ).
            analyzeWildcard( true );

        appendQueryFieldNames( arguments, builder );

        return builder;

    }
}
