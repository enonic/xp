package com.enonic.xp.repository;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.util.BinaryReference;


public interface RepositoryService
{
    Repository createRepository( CreateRepositoryParams params );

    Repository updateRepository( UpdateRepositoryParams params );

    Branch createBranch( CreateBranchParams params );

    Repositories list();

    Repository get( RepositoryId repositoryId );

    RepositoryId deleteRepository( DeleteRepositoryParams params );

    Branch deleteBranch( DeleteBranchParams params );

    ByteSource getBinary( RepositoryId repositoryId, BinaryReference binaryReference );
}
