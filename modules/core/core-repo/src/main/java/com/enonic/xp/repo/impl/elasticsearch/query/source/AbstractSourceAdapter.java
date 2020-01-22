package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.storage.BaseStorageName;
import com.enonic.xp.repo.impl.storage.CommitStorageName;
import com.enonic.xp.repo.impl.storage.StoreStorageName;
import com.enonic.xp.repository.RepositoryId;

abstract class AbstractSourceAdapter
{
    private final static String SEARCH_INDEX_PREFIX = "search";

    private final static String DIVIDER = "-";

    static String createSearchIndexName( final RepositoryId repositoryId, final Branch branch )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString() + DIVIDER + branch.getValue().toLowerCase();
    }

    static BaseStorageName createStorageIndexName( final RepositoryId repositoryId, final SingleRepoStorageSource.Type type )
    {
        switch ( type )
        {
            case VERSION:
            case BRANCH:
                return StoreStorageName.from( repositoryId );
            case COMMIT:
                return CommitStorageName.from( repositoryId );
        }
        return null;
    }

    static String createSearchTypeName( final Branch branch )
    {
        return branch.getValue();
    }

}
