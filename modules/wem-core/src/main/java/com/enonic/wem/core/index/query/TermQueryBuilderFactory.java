package com.enonic.wem.core.index.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.query.expr.CompareExpr;

public class TermQueryBuilderFactory
    extends AbstractQueryBuilderFactory
{
    public QueryBuilder create( final CompareExpr compareExpr )
    {
        final String queryFieldName = IndexQueryFieldNameResolver.resolve( compareExpr );

        final Value<?> value = compareExpr.getFirstValue().getValue();
        return QueryBuilders.termQuery( queryFieldName, getValueAsType( value ) );
    }


}
