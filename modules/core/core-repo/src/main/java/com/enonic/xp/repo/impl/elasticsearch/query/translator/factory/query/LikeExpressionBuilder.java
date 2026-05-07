package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueNormalizer;
import com.enonic.xp.repo.impl.index.StaticIndexValueType;

class LikeExpressionBuilder
{

    public static QueryBuilder build( final CompareExpr compareExpr, final QueryFieldNameResolver resolver )
    {
        final ValueExpr firstValue = compareExpr.getFirstValue();
        if ( firstValue == null )
        {
            throw new IllegalArgumentException( "Invalid compare expression [" + compareExpr + "]" );
        }

        final Value value = firstValue.getValue();
        final String queryFieldName = resolver.resolve( compareExpr.getField().getIndexPath(), StaticIndexValueType.STRING );

        return QueryBuilders.wildcardQuery( queryFieldName, IndexValueNormalizer.normalize( value.asString() ) );
    }
}
