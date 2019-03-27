package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.enonic.xp.query.expr.FunctionExpr;

class PathMatchFunction
{
    public static QueryBuilder create( final FunctionExpr functionExpr )
    {
        final PathMatchFunctionArguments arguments = PathMatchFunctionArguments.create( functionExpr.getArguments() );
        final MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder( arguments.getFieldName(), arguments.getPath() );

        if ( arguments.getMinimumMatch() > 1 )
        {
            final String minimumPath = Arrays.stream( arguments.getPath().split( "/" ) ).
                limit( arguments.getMinimumMatch() + 1 ).
                collect( Collectors.joining( "/" ) );
            final TermQueryBuilder termQueryBuilder = new TermQueryBuilder( arguments.getFieldName(), minimumPath );
            return new BoolQueryBuilder().
                must( termQueryBuilder ).
                must( matchQueryBuilder );
        }

        return matchQueryBuilder;
    }
}
