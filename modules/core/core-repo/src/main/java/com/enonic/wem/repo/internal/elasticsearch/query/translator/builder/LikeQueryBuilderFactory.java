package com.enonic.wem.repo.internal.elasticsearch.query.translator.builder;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.wem.repo.internal.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.wem.repo.internal.index.IndexValueNormalizer;
import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;

public class LikeQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public LikeQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public QueryBuilder create( final CompareExpr compareExpr )
    {
        final String queryFieldName = fieldNameResolver.resolve( compareExpr.getField().getFieldPath() );
        final Value value = compareExpr.getFirstValue().getValue();

        return QueryBuilders.wildcardQuery( queryFieldName, IndexValueNormalizer.normalize( value.asString() ) );
    }
}
