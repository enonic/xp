package com.enonic.wem.core.index.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.query.expr.CompareExpr;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

public class RangeQueryBuilderFactory
    extends AbstractBuilderFactory
{

    public QueryBuilder create( final CompareExpr compareExpr )
    {
        final CompareExpr.Operator operator = compareExpr.getOperator();

        switch ( operator )
        {
            case GT:
                return doBuildGT( compareExpr, false );
            case GTE:
                return doBuildGT( compareExpr, true );
            case LT:
                return doBuildGT( compareExpr, false );
            case LTE:
                return doBuildGT( compareExpr, true );
            default:
                throw new IllegalArgumentException( "Operator " + operator + " not expected in rangeQueryBuilder" );
        }

    }

    private QueryBuilder doBuildGT( final CompareExpr compareExpr, final boolean includeLower )
    {
        final String queryFieldName = IndexQueryFieldNameResolver.resolve( compareExpr );
        final Object value = getValueAsType( compareExpr.getFirstValue().getValue() );

        return rangeQuery( queryFieldName ).
            from( value ).
            to( null ).
            includeLower( includeLower );
    }

    private QueryBuilder doBuildLT( final CompareExpr compareExpr, final boolean includeUpper )
    {
        final String queryFieldName = IndexQueryFieldNameResolver.resolve( compareExpr );
        final Object value = getValueAsType( compareExpr.getFirstValue().getValue() );

        return rangeQuery( queryFieldName ).
            from( null ).
            to( value ).
            includeUpper( includeUpper );
    }

}
