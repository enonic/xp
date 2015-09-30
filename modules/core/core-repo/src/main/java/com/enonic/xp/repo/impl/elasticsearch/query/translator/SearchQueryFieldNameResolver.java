package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class SearchQueryFieldNameResolver
    extends AbstractQueryFieldNameResolver
{
    private final static List<String> BUILT_IN_FIELDS = Lists.newArrayList( "_score", "_id" );


    @Override
    protected List<String> getBuiltInFields()
    {
        return BUILT_IN_FIELDS;
    }

    protected String appendIndexValueType( final String baseFieldName, final IndexValueType indexValueType )
    {
        return IndexFieldNameNormalizer.normalize( baseFieldName + ( Strings.isNullOrEmpty( indexValueType.getPostfix() )
            ? ""
            : IndexValueType.INDEX_VALUE_TYPE_SEPARATOR + indexValueType.getPostfix() ) );
    }

}

