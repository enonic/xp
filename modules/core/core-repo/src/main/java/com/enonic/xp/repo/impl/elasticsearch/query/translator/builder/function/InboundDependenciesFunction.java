package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.function;

import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.query.expr.FunctionExpr;

class InboundDependenciesFunction
{
    public static QueryBuilder create( final FunctionExpr functionExpr )
    {
        final InboundDependenciesFunctionArguments arguments = InboundDependenciesFunctionArguments.create( functionExpr.getArguments() );
        return new MatchQueryBuilder( arguments.getFieldName(), arguments.getContentId() );
    }
}
