package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public interface RepositoryEntryService
{
    void createRepositoryEntry( Repository repository );

    RepositoryIds findRepositoryEntryIds();

    Repository getRepositoryEntry( final RepositoryId repositoryId );

    Repository addBranchToRepositoryEntry( final RepositoryId repositoryId, final Branch branch );

    Repository removeBranchFromRepositoryEntry( final RepositoryId repositoryId, final Branch branch );

    void deleteRepositoryEntry( RepositoryId repositoryId );
}
