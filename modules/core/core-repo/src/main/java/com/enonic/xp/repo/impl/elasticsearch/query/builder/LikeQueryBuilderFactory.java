package com.enonic.xp.repo.impl.elasticsearch.query.builder;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.repo.impl.index.IndexValueNormalizer;
import com.enonic.xp.repo.impl.index.query.IndexQueryFieldNameResolver;

public class LikeQueryBuilderFactory
    extends AbstractQueryBuilderFactory
{

    public static QueryBuilder create( final CompareExpr compareExpr )
    {
        final String queryFieldName = IndexQueryFieldNameResolver.resolveStringFieldName( compareExpr.getField().getFieldPath() );
        final Value value = compareExpr.getFirstValue().getValue();

        return QueryBuilders.wildcardQuery( queryFieldName, IndexValueNormalizer.normalize( value.asString() ) );
    }
}
