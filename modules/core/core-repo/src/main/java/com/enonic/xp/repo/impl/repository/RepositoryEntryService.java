package com.enonic.xp.repo.impl.repository;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public interface RepositoryEntryService
{
    void createRepositoryEntry( RepositoryEntry repository );

    RepositoryIds findRepositoryEntryIds();

    RepositoryEntry getRepositoryEntry( RepositoryId repositoryId );

    RepositoryEntry addBranchToRepositoryEntry( RepositoryId repositoryId, Branch branch );

    RepositoryEntry removeBranchFromRepositoryEntry( RepositoryId repositoryId, Branch branch );

    RepositoryEntry updateRepositoryEntry( UpdateRepositoryEntryParams params );

    void deleteRepositoryEntry( RepositoryId repositoryId );

    ByteSource getBinary( AttachedBinary attachedBinary );
}
