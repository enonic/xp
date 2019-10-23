package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.search.SearchStorageName;
import com.enonic.xp.repo.impl.storage.BaseStorageName;
import com.enonic.xp.repo.impl.storage.BranchStorageName;
import com.enonic.xp.repo.impl.storage.CommitStorageName;
import com.enonic.xp.repo.impl.storage.VersionStorageName;
import com.enonic.xp.repository.RepositoryId;

abstract class AbstractSourceAdapter
{
    static SearchStorageName createSearchIndexName( final RepositoryId repositoryId )
    {
        return SearchStorageName.from( repositoryId );
    }

    static BaseStorageName createStorageIndexName( final RepositoryId repositoryId, final SingleRepoStorageSource.Type type )
    {
        switch ( type )
        {
            case VERSION:
                return VersionStorageName.from( repositoryId );
            case BRANCH:
                return BranchStorageName.from( repositoryId );
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
