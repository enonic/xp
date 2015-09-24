package com.enonic.wem.repo.internal.elasticsearch.query.translator.builder;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.repo.internal.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.FunctionExpr;

class DynamicQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public DynamicQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public static QueryBuilder create( final DynamicConstraintExpr expression )
    {
        final FunctionExpr function = expression.getFunction();

        if ( function != null )
        {
            return FunctionQueryBuilderFactory.create( function );
        }

        return null;
    }


}
