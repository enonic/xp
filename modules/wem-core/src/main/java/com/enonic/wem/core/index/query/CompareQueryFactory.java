package com.enonic.wem.core.index.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.query.expr.CompareExpr;

public class CompareQueryFactory
    extends AbstractBuilderFactory
{
    private TermQueryBuilderFactory termQueryBuilderFactory = new TermQueryBuilderFactory();

    private RangeQueryBuilderFactory rangeQueryBuilderFactory = new RangeQueryBuilderFactory();

    private LikeQueryBuilderFactory likeQueryBuilderFactory = new LikeQueryBuilderFactory();

    private InQueryBuilderFactory inQueryBuilderFactory = new InQueryBuilderFactory();

    public QueryBuilder create( final CompareExpr compareExpr )
    {
        final CompareExpr.Operator operator = compareExpr.getOperator();

        switch ( operator )
        {
            case EQ:
                return termQueryBuilderFactory.create( compareExpr );
            case NEQ:
                return buildNotQuery( termQueryBuilderFactory.create( compareExpr ) );
            case GT:
                return rangeQueryBuilderFactory.create( compareExpr );
            case GTE:
                return rangeQueryBuilderFactory.create( compareExpr );
            case LT:
                return rangeQueryBuilderFactory.create( compareExpr );
            case LTE:
                return rangeQueryBuilderFactory.create( compareExpr );
            case LIKE:
                return likeQueryBuilderFactory.create( compareExpr );
            case NOT_LIKE:
                return buildNotQuery( likeQueryBuilderFactory.create( compareExpr ) );
            case IN:
                return inQueryBuilderFactory.create( compareExpr );
            case NOT_IN:
                return buildNotQuery( inQueryBuilderFactory.create( compareExpr ) );
            default:
                throw new IndexQueryBuilderException( "Operator " + operator + " not supported in builder" );
        }
    }

}
