package com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver;

import java.util.Set;

import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.repo.impl.index.IndexValueTypeInterface;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class SearchQueryFieldNameResolver
    extends AbstractQueryFieldNameResolver
{
    private static final Set<String> BUILT_IN_FIELDS = Set.of( "_score", "_id" );

    public static final SearchQueryFieldNameResolver INSTANCE = new SearchQueryFieldNameResolver();

    private SearchQueryFieldNameResolver()
    {
        super( BUILT_IN_FIELDS );
    }

    @Override
    protected String appendIndexValueType( final String baseFieldName, final IndexValueTypeInterface indexValueType )
    {
        return IndexFieldNameNormalizer.normalize( baseFieldName + ( isNullOrEmpty( indexValueType.getPostfix() )
            ? ""
            : IndexValueType.INDEX_VALUE_TYPE_SEPARATOR + indexValueType.getPostfix() ) );
    }

}

