package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class StoreQueryFieldNameResolver
    extends AbstractQueryFieldNameResolver
{
    private final List<String> builtInFields = Lists.newArrayList();


    @Override
    protected List<String> getBuiltInFields()
    {
        return builtInFields;
    }

    @Override
    protected String appendIndexValueType( final String baseFieldName, final IndexValueType indexValueType )
    {
        return IndexFieldNameNormalizer.normalize( baseFieldName );
    }
}
