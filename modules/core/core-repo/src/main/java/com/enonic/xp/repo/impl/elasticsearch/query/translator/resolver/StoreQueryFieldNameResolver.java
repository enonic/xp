package com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;
import com.enonic.xp.repo.impl.index.IndexValueTypeInterface;

public class StoreQueryFieldNameResolver
    extends AbstractQueryFieldNameResolver
{
    private final List<String> builtInFields = new ArrayList<>();


    @Override
    protected List<String> getBuiltInFields()
    {
        return builtInFields;
    }

    @Override
    protected String appendIndexValueType( final String baseFieldName, final IndexValueTypeInterface indexValueType )
    {
        return IndexFieldNameNormalizer.normalize( baseFieldName );
    }
}
