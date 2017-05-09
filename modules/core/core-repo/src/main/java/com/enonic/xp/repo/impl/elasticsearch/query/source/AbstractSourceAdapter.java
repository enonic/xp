package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

abstract class AbstractSourceAdapter
{
    private final static String SEARCH_INDEX_PREFIX = "search";

    private final static String STORAGE_INDEX_PREFIX = "storage";

    private final static String DIVIDER = "-";


    protected static String createSearchIndexName( final RepositoryId repositoryId )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    protected static String createStorageIndexName( final RepositoryId repositoryId )
    {
        return STORAGE_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    protected static String createSearchTypeName( final Branch branch )
    {
        return branch.getValue();
    }

}
