package com.enonic.xp.repository;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public interface RepositoryService
{
    Repository createRepository( final CreateRepositoryParams params );

    Branch createBranch( final CreateBranchParams params );

    Repositories list();

    boolean isInitialized( final RepositoryId id );

    Repository get( final RepositoryId repositoryId );

    RepositoryId deleteRepository( final DeleteRepositoryParams params );

    Branch deleteBranch( final DeleteBranchParams params );

    void invalidateAll();

    void invalidate( final RepositoryId repositoryId );
}
