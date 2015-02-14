package com.enonic.wem.repo.internal.elasticsearch.query.builder;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.core.data.Value;
import com.enonic.xp.core.query.expr.CompareExpr;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

public class TermQueryBuilderFactory
    extends AbstractQueryBuilderFactory
{
    public static QueryBuilder create( final CompareExpr compareExpr )
    {
        final String queryFieldName = IndexQueryFieldNameResolver.resolve( compareExpr );

        final Value value = compareExpr.getFirstValue().getValue();
        return QueryBuilders.termQuery( queryFieldName, getValueAsType( value ) );
    }

    public static QueryBuilder create( final String fieldName, final Value value )
    {
        return QueryBuilders.termQuery( fieldName, getValueAsType( value ) );
    }
}
