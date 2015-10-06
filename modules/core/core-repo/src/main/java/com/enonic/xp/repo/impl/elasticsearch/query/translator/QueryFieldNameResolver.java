package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.index.IndexValueType;

public interface QueryFieldNameResolver
{
    String resolve( final CompareExpr compareExpr );

    String resolve( final ValueFilter valueQueryFilter );

    String resolve( final String queryFieldName );

    String resolve( final String queryFieldName, final IndexValueType indexValueType );

    String resolve( final String queryFieldName, final Value value );

    String resolveOrderByFieldName( final String queryFieldName );

}
