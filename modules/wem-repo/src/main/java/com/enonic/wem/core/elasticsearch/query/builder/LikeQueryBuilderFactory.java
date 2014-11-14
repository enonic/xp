package com.enonic.wem.core.elasticsearch.query.builder;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.query.expr.CompareExpr;
import com.enonic.wem.core.index.query.IndexQueryFieldNameResolver;

public class LikeQueryBuilderFactory
    extends AbstractQueryBuilderFactory
{

    public static QueryBuilder create( final CompareExpr compareExpr )
    {
        final String queryFieldName = IndexQueryFieldNameResolver.resolveStringFieldName( compareExpr.getField().getName() );
        final Value value = compareExpr.getFirstValue().getValue();

        return QueryBuilders.wildcardQuery( queryFieldName, value.asString() );
    }
}
