package com.enonic.wem.core.index.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.wem.query.expr.CompareExpr;

public class CompareQueryBuilder
{
    private TermQueryBuilderFactory termQueryBuilderFactory = new TermQueryBuilderFactory();

    public QueryBuilder build( final CompareExpr compareExpr )
    {
        final CompareExpr.Operator operator = compareExpr.getOperator();

        switch ( operator )
        {
            case EQ:
                return termQueryBuilderFactory.create( compareExpr );
            case NEQ:
                return buildNotQuery( termQueryBuilderFactory.create( compareExpr ) );
            case GT:
                // return QueryBuilders.termQuery( indexFieldName, compareExpr.getFirstValue() );
            case GTE:
                // return QueryBuilders.termQuery( indexFieldName, compareExpr.getFirstValue() );
            case LT:
                // return QueryBuilders.termQuery( indexFieldName, compareExpr.getFirstValue() );
            case LTE:
                //  return QueryBuilders.termQuery( indexFieldName, compareExpr.getFirstValue() );
            case LIKE:
                //  return QueryBuilders.termQuery( indexFieldName, compareExpr.getFirstValue() );
            case NOT_LIKE:
                //  return QueryBuilders.termQuery( indexFieldName, compareExpr.getFirstValue() );
            case IN:
                //  return QueryBuilders.termQuery( indexFieldName, compareExpr.getFirstValue() );
            case NOT_IN:
                //  return QueryBuilders.termQuery( indexFieldName, compareExpr.getFirstValue() );
        }

        return null;


    }

    private QueryBuilder buildNotQuery( final QueryBuilder negated )
    {
        return QueryBuilders.boolQuery().mustNot( negated );
    }


}
