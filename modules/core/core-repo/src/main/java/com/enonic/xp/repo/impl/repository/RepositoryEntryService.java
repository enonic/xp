package com.enonic.xp.repo.impl.repository;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public interface RepositoryEntryService
{
    void createRepositoryEntry( Repository repository );

    RepositoryIds findRepositoryEntryIds();

    Repository getRepositoryEntry( RepositoryId repositoryId );

    Repository addBranchToRepositoryEntry( RepositoryId repositoryId, Branch branch );

    Repository removeBranchFromRepositoryEntry( RepositoryId repositoryId, Branch branch );

    Repository updateRepositoryEntry( UpdateRepositoryEntryParams params );

    void deleteRepositoryEntry( RepositoryId repositoryId );

    ByteSource getBinary( AttachedBinary attachedBinary );
}
