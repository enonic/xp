package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.Expression;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.NotExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

public class ConstraintExpressionBuilder
{

    public static QueryBuilder build( final Expression constraint, final QueryFieldNameResolver resolver )
    {
        if ( constraint == null )
        {
            return QueryBuilders.matchAllQuery();
        }
        else if ( constraint instanceof LogicalExpr )
        {
            return LogicalExpressionBuilder.build( (LogicalExpr) constraint, resolver );
        }
        else if ( constraint instanceof DynamicConstraintExpr )
        {
            return DynamicExpressionBuilder.build( (DynamicConstraintExpr) constraint );
        }
        else if ( constraint instanceof CompareExpr )
        {
            return CompareExpressionBuilder.build( (CompareExpr) constraint, resolver );

        }
        else if ( constraint instanceof NotExpr )
        {
            return NotExpressionBuilder.build( (NotExpr) constraint, resolver );
        }
        else
        {
            throw new UnsupportedOperationException( "Not able to handle expression of type " + constraint.getClass() );
        }
    }

}
