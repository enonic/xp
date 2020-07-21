package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

abstract class AbstractSourceAdapter
{
    private static final String SEARCH_INDEX_PREFIX = "search";

    private static final String STORAGE_INDEX_PREFIX = "storage";

    private static final String DIVIDER = "-";


    static String createSearchIndexName( final RepositoryId repositoryId )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    static String createStorageIndexName( final RepositoryId repositoryId )
    {
        return STORAGE_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    static String createSearchTypeName( final Branch branch )
    {
        return branch.getValue();
    }

}
