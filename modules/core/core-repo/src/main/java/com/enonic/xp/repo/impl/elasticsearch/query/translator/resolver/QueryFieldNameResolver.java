package com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver;

import java.util.Locale;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;

public interface QueryFieldNameResolver
{
    String resolve( IndexPath queryFieldName, IndexValueType indexValueType );

    String resolve( String queryFieldName, IndexValueType indexValueType );

    String resolve( IndexPath queryFieldName, Value value );

    String resolve( String queryFieldName, Value value );

    String resolveOrderByFieldName( IndexPath queryFieldName, Locale language );

    /**
     * The numeric field a sort on this field orders by first, or {@code null} when the sort gets no numeric key.
     */
    @Nullable String resolveNumberOrderByFieldName( IndexPath queryFieldName );
}
