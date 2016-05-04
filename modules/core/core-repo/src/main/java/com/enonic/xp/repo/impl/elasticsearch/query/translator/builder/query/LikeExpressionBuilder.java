package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueNormalizer;

public class LikeExpressionBuilder
{


    public static QueryBuilder build( final CompareExpr compareExpr, final QueryFieldNameResolver resolver )
    {
        final String queryFieldName = resolver.resolve( compareExpr.getField().getFieldPath() );

        if ( compareExpr.getFirstValue() == null )
        {
            throw new IllegalArgumentException( "Invalid compare expression [" + compareExpr.toString() + "]" );
        }

        final Value value = compareExpr.getFirstValue().getValue();

        return QueryBuilders.wildcardQuery( queryFieldName, IndexValueNormalizer.normalize( value.asString() ) );
    }
}
