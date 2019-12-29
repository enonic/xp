package com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver;

import java.util.List;

import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.repo.impl.index.IndexValueTypeInterface;

import static com.google.common.base.Strings.isNullOrEmpty;

public class SearchQueryFieldNameResolver
    extends AbstractQueryFieldNameResolver
{
    private final static List<String> BUILT_IN_FIELDS = List.of( "_score", "_id" );


    @Override
    protected List<String> getBuiltInFields()
    {
        return BUILT_IN_FIELDS;
    }

    @Override
    protected String appendIndexValueType( final String baseFieldName, final IndexValueTypeInterface indexValueType )
    {
        return IndexFieldNameNormalizer.normalize( baseFieldName + ( isNullOrEmpty( indexValueType.getPostfix() )
            ? ""
            : IndexValueType.INDEX_VALUE_TYPE_SEPARATOR + indexValueType.getPostfix() ) );
    }

}

