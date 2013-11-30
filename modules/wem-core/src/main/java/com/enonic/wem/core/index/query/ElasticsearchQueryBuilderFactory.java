package com.enonic.wem.core.index.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.wem.query.expr.CompareExpr;
import com.enonic.wem.query.expr.ConstraintExpr;
import com.enonic.wem.query.expr.DynamicConstraintExpr;
import com.enonic.wem.query.expr.LogicalExpr;
import com.enonic.wem.query.expr.NotExpr;
import com.enonic.wem.query.expr.QueryExpr;

public class ElasticsearchQueryBuilderFactory
{
    private CompareQueryFactory compareQueryFactory = new CompareQueryFactory();

    private DynamicQueryFactory dynamicQueryFactory = new DynamicQueryFactory();

    public QueryBuilder create( final QueryExpr queryExpr )
    {
        final ConstraintExpr constraint = queryExpr.getConstraint();

        return buildConstraint( constraint );
    }

    private QueryBuilder buildConstraint( final ConstraintExpr constraint )
    {
        if ( constraint == null )
        {
            return QueryBuilders.matchAllQuery();
        }
        else if ( constraint instanceof LogicalExpr )
        {
            return buildLogicalExpression( (LogicalExpr) constraint );
        }
        else if ( constraint instanceof DynamicConstraintExpr )
        {
            return dynamicQueryFactory.create( (DynamicConstraintExpr) constraint );

        }
        else if ( constraint instanceof CompareExpr )
        {
            return compareQueryFactory.create( (CompareExpr) constraint );

        }
        else if ( constraint instanceof NotExpr )
        {

        }
        else
        {
            throw new UnsupportedOperationException( "Not able to handle expression of type " + constraint.getClass() );
        }

        return null;
    }

    private QueryBuilder buildLogicalExpression( final LogicalExpr expr )
    {

        final QueryBuilder left = buildConstraint( expr.getLeft() );
        final QueryBuilder right = buildConstraint( expr.getRight() );

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
