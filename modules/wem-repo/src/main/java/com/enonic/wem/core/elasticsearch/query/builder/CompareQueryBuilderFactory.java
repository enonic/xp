package com.enonic.wem.core.elasticsearch.query.builder;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.api.query.expr.CompareExpr;
import com.enonic.wem.core.index.query.IndexQueryBuilderException;

public class CompareQueryBuilderFactory
    extends AbstractQueryBuilderFactory
{
    public static QueryBuilder create( final CompareExpr compareExpr )
    {
        final CompareExpr.Operator operator = compareExpr.getOperator();

        switch ( operator )
        {
            case EQ:
                return TermQueryBuilderFactory.create( compareExpr );
            case NEQ:
                return buildNotQuery( TermQueryBuilderFactory.create( compareExpr ) );
            case GT:
                return RangeQueryBuilderFactory.create( compareExpr );
            case GTE:
                return RangeQueryBuilderFactory.create( compareExpr );
            case LT:
                return RangeQueryBuilderFactory.create( compareExpr );
            case LTE:
                return RangeQueryBuilderFactory.create( compareExpr );
            case LIKE:
                return LikeQueryBuilderFactory.create( compareExpr );
            case NOT_LIKE:
                return buildNotQuery( LikeQueryBuilderFactory.create( compareExpr ) );
            case IN:
                return InQueryBuilderFactory.create( compareExpr );
            case NOT_IN:
                return buildNotQuery( InQueryBuilderFactory.create( compareExpr ) );
            default:
                throw new IndexQueryBuilderException( "Operator " + operator + " not supported in builder" );
        }
    }

}
