package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public interface RepositoryService
{
    Repository createRepository( final CreateRepositoryParams params );

    Branch createBranch( final CreateBranchParams params );

    Repository get( final RepositoryId repositoryId );

    boolean isInitialized( final RepositoryId id );
}
