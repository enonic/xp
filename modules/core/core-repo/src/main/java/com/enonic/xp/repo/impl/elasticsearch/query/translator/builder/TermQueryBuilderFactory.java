package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.ValueHelper;

public class TermQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public TermQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public QueryBuilder create( final CompareExpr compareExpr )
    {
        final String queryFieldName = fieldNameResolver.resolve( compareExpr );

        final Value value = compareExpr.getFirstValue().getValue();
        return QueryBuilders.termQuery( queryFieldName, ValueHelper.getValueAsType( value ) );
    }

    public QueryBuilder create( final String fieldName, final Value value )
    {
        return QueryBuilders.termQuery( fieldName, ValueHelper.getValueAsType( value ) );
    }
}
