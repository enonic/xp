package com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;

public final class StoreQueryFieldNameResolver
    extends AbstractQueryFieldNameResolver
{
    public static final StoreQueryFieldNameResolver INSTANCE = new StoreQueryFieldNameResolver();

    private StoreQueryFieldNameResolver()
    {
    }

    @Override
    public String resolve( final IndexPath queryFieldName, final IndexValueType indexValueType )
    {
        return queryFieldName.getPath();
    }
}
