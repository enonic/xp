package com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver;

import java.util.Set;

import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;
import com.enonic.xp.repo.impl.index.IndexValueTypeInterface;

public final class StoreQueryFieldNameResolver
    extends AbstractQueryFieldNameResolver
{
    public static final StoreQueryFieldNameResolver INSTANCE = new StoreQueryFieldNameResolver();

    private StoreQueryFieldNameResolver()
    {
        super( Set.of() );
    }

    @Override
    protected String appendIndexValueType( final String baseFieldName, final IndexValueTypeInterface indexValueType )
    {
        return IndexFieldNameNormalizer.normalize( baseFieldName );
    }
}
