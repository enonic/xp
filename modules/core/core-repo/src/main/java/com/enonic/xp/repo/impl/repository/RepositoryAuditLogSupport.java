package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.DeleteRepositoryParams;

public interface RepositoryAuditLogSupport
{
    void createRepository( CreateRepositoryParams params );

    void deleteRepository( DeleteRepositoryParams params );

    void createBranch( CreateBranchParams params );

    void deleteBranch( DeleteBranchParams params );
}
