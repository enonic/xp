package com.enonic.wem.repo.internal.elasticsearch.query.translator;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.repo.internal.index.IndexFieldNameNormalizer;
import com.enonic.wem.repo.internal.index.IndexValueType;

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
