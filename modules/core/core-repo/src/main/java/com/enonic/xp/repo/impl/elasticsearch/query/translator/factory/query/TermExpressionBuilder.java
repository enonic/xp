package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.ValueHelper;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

class TermExpressionBuilder
{
    public static QueryBuilder build( final CompareExpr compareExpr, final QueryFieldNameResolver resolver )
    {
        final String queryFieldName = resolver.resolve( compareExpr );

        if ( compareExpr.getFirstValue() == null )
        {
            throw new IllegalArgumentException( "Invalid compare expression [" + compareExpr + "]" );
        }

        final Value value = compareExpr.getFirstValue().getValue();
        return QueryBuilders.termQuery( queryFieldName, ValueHelper.getValueAsType( value ) ).queryName( queryFieldName );
    }

    public static QueryBuilder build( final String fieldName, final Value value )
    {
        return QueryBuilders.termQuery( fieldName, ValueHelper.getValueAsType( value ) ).queryName( fieldName );
    }
}
