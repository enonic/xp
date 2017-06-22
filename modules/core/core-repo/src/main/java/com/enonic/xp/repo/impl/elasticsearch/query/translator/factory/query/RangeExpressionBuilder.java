package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.ValueHelper;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

class RangeExpressionBuilder
{
    public static QueryBuilder build( final CompareExpr compareExpr, final QueryFieldNameResolver resolver )
    {
        final CompareExpr.Operator operator = compareExpr.getOperator();

        switch ( operator )
        {
            case GT:
                return doBuildGT( compareExpr, resolver, false );
            case GTE:
                return doBuildGT( compareExpr, resolver, true );
            case LT:
                return doBuildLT( compareExpr, resolver, false );
            case LTE:
                return doBuildLT( compareExpr, resolver, true );
            default:
                throw new IllegalArgumentException( "Operator " + operator + " not expected in rangeQueryBuilder" );
        }
    }

    private static QueryBuilder doBuildGT( final CompareExpr compareExpr, final QueryFieldNameResolver resolver,
                                           final boolean includeLower )
    {
        final String queryFieldName = resolver.resolve( compareExpr );

        if ( compareExpr.getFirstValue() == null )
        {
            throw new IllegalArgumentException( "Invalid compare expression [" + compareExpr.toString() + "]" );
        }

        final Object value = ValueHelper.getValueAsType( compareExpr.getFirstValue().getValue() );

        return rangeQuery( queryFieldName ).
            from( value ).
            to( null ).
            includeLower( includeLower );
    }

    private static QueryBuilder doBuildLT( final CompareExpr compareExpr, final QueryFieldNameResolver resolver,
                                           final boolean includeUpper )
    {
        final String queryFieldName = resolver.resolve( compareExpr );

        if ( compareExpr.getFirstValue() == null )
        {
            throw new IllegalArgumentException( "Invalid compare expression [" + compareExpr.toString() + "]" );
        }

        final Object value = ValueHelper.getValueAsType( compareExpr.getFirstValue().getValue() );

        return rangeQuery( queryFieldName ).
            from( null ).
            to( value ).
            includeUpper( includeUpper );
    }

}
