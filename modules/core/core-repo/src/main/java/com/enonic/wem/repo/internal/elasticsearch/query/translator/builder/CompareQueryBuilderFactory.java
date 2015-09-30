package com.enonic.wem.repo.internal.elasticsearch.query.translator.builder;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.repo.internal.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.wem.repo.internal.index.query.IndexQueryBuilderException;
import com.enonic.xp.query.expr.CompareExpr;

public class CompareQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public CompareQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public QueryBuilder create( final CompareExpr compareExpr )
    {
        final CompareExpr.Operator operator = compareExpr.getOperator();

        switch ( operator )
        {
            case EQ:
                return new TermQueryBuilderFactory( fieldNameResolver ).create( compareExpr );
            case NEQ:
                return new NotQueryBuilderFactory( fieldNameResolver ).create(
                    new TermQueryBuilderFactory( fieldNameResolver ).create( compareExpr ) );
            case GT:
                return new RangeQueryBuilderFactory( fieldNameResolver ).create( compareExpr );
            case GTE:
                return new RangeQueryBuilderFactory( fieldNameResolver ).create( compareExpr );
            case LT:
                return new RangeQueryBuilderFactory( fieldNameResolver ).create( compareExpr );
            case LTE:
                return new RangeQueryBuilderFactory( fieldNameResolver ).create( compareExpr );
            case LIKE:
                return new LikeQueryBuilderFactory( fieldNameResolver ).create( compareExpr );
            case NOT_LIKE:
                return new NotQueryBuilderFactory( fieldNameResolver ).create(
                    new LikeQueryBuilderFactory( fieldNameResolver ).create( compareExpr ) );
            case IN:
                return new InQueryBuilderFactory( fieldNameResolver ).create( compareExpr );
            case NOT_IN:
                return new NotQueryBuilderFactory( fieldNameResolver ).create(
                    new InQueryBuilderFactory( fieldNameResolver ).create( compareExpr ) );
            default:
                throw new IndexQueryBuilderException( "Operator " + operator + " not supported in builder" );
        }
    }

}
