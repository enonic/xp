package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.repository.RepositoryId;

public class IndexNameResolver
{
    private final static String SEARCH_INDEX_PREFIX = "search";

    private final static String STORAGE_INDEX_PREFIX = "storage";

    private final static String DIVIDER = "-";

    public static String resolveStorageIndexName( final RepositoryId repositoryId )
    {
        return STORAGE_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    public static String resolveSearchIndexName( final RepositoryId repositoryId )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }


}
