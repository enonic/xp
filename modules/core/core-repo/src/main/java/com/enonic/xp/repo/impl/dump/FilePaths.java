package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public interface FilePaths
{
    PathRef basePath();

    PathRef repoRootPath();

    PathRef repoPath( final RepositoryId repositoryId );

    PathRef branchRootPath( final RepositoryId repositoryId );

    PathRef branchMetaPath( final RepositoryId repositoryId, final Branch branch );

    PathRef versionMetaPath( final RepositoryId repositoryId );

    PathRef commitMetaPath( final RepositoryId repositoryId );

    PathRef metaDataFile();
}
