package com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver;

import java.util.Locale;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.index.IndexValueType;

public interface QueryFieldNameResolver
{
    String resolve( CompareExpr compareExpr );

    String resolve( ValueFilter valueQueryFilter );

    String resolve( String queryFieldName );

    String resolve( String queryFieldName, IndexValueType indexValueType );

    String resolve( String queryFieldName, Value value );

    String resolveOrderByFieldName( String queryFieldName, Locale language );
}
