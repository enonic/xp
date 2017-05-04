package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.query.IndexQueryBuilderException;

class CompareExpressionBuilder
{
    public static QueryBuilder build( final CompareExpr compareExpr, final QueryFieldNameResolver resolver )
    {
        final CompareExpr.Operator operator = compareExpr.getOperator();

        switch ( operator )
        {
            case EQ:
                return TermExpressionBuilder.build( compareExpr, resolver );
            case NEQ:
                return NotQueryBuilder.build( TermExpressionBuilder.build( compareExpr, resolver ) );
            case GT:
                return RangeExpressionBuilder.build( compareExpr, resolver );
            case GTE:
                return RangeExpressionBuilder.build( compareExpr, resolver );
            case LT:
                return RangeExpressionBuilder.build( compareExpr, resolver );
            case LTE:
                return RangeExpressionBuilder.build( compareExpr, resolver );
            case LIKE:
                return LikeExpressionBuilder.build( compareExpr, resolver );
            case NOT_LIKE:
                return NotQueryBuilder.build( LikeExpressionBuilder.build( compareExpr, resolver ) );
            case IN:
                return InExpressionBuilder.build( compareExpr, resolver );
            case NOT_IN:
                return NotQueryBuilder.build( InExpressionBuilder.build( compareExpr, resolver ) );
            default:
                throw new IndexQueryBuilderException( "Operator " + operator + " not supported in builder" );
        }
    }

}
