package com.enonic.wem.core.repository;

import com.enonic.wem.api.repository.RepositoryId;

public class IndexNameResolver
{
    public final static String SEARCH_INDEX_PREFIX = "search";

    public final static String DIVIDER = "-";

    public static String resolveSearchIndexName( final RepositoryId repositoryId )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

}
