package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

class LogicalExpressionBuilder
{
    public static QueryBuilder build( final LogicalExpr expr, final QueryFieldNameResolver resolver )
    {
        final QueryBuilder left = ConstraintExpressionBuilder.build( expr.getLeft(), resolver );
        final QueryBuilder right = ConstraintExpressionBuilder.build( expr.getRight(), resolver );

        if ( expr.getOperator() == LogicalExpr.Operator.OR )
        {
            return QueryBuilders.boolQuery().should( left ).should( right );
        }
        else if ( expr.getOperator() == LogicalExpr.Operator.AND )
        {
            return QueryBuilders.boolQuery().must( left ).must( right );
        }
        else
        {
            throw new IllegalArgumentException( "Operation [" + expr.getOperator() + "] not supported" );
        }
    }

}
