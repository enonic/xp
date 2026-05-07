package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.ValueHelper;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

class RangeExpressionBuilder
{
    public static QueryBuilder build( final CompareExpr compareExpr, final QueryFieldNameResolver resolver )
    {
        final CompareExpr.Operator operator = compareExpr.getOperator();

        return switch ( operator )
        {
            case GT -> doBuildGT( compareExpr, resolver, false );
            case GTE -> doBuildGT( compareExpr, resolver, true );
            case LT -> doBuildLT( compareExpr, resolver, false );
            case LTE -> doBuildLT( compareExpr, resolver, true );
            default -> throw new IllegalArgumentException( "Operator " + operator + " not expected in rangeQueryBuilder" );
        };
    }

    private static QueryBuilder doBuildGT( final CompareExpr compareExpr, final QueryFieldNameResolver resolver,
                                           final boolean includeLower )
    {
        final ValueExpr firstValue = compareExpr.getFirstValue();
        if ( firstValue == null )
        {
            throw new IllegalArgumentException( "Invalid compare expression [" + compareExpr + "]" );
        }

        final String queryFieldName = resolver.resolve( compareExpr.getField().getIndexPath(), firstValue.getValue() );
        final Object value = ValueHelper.getValueAsType( firstValue.getValue() );

        return rangeQuery( queryFieldName ).from( value ).to( null ).includeLower( includeLower );
    }

    private static QueryBuilder doBuildLT( final CompareExpr compareExpr, final QueryFieldNameResolver resolver,
                                           final boolean includeUpper )
    {
        final ValueExpr firstValue = compareExpr.getFirstValue();
        if ( firstValue == null )
        {
            throw new IllegalArgumentException( "Invalid compare expression [" + compareExpr + "]" );
        }

        final String queryFieldName = resolver.resolve( compareExpr.getField().getIndexPath(), firstValue.getValue() );
        final Object value = ValueHelper.getValueAsType( firstValue.getValue() );

        return rangeQuery( queryFieldName ).from( null ).to( value ).includeUpper( includeUpper );
    }
}
