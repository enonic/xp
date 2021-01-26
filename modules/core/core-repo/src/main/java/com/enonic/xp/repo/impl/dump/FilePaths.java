package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public interface FilePaths
{
    PathRef basePath();

    PathRef repoRootPath();

    PathRef repoPath( RepositoryId repositoryId );

    PathRef branchRootPath( RepositoryId repositoryId );

    PathRef branchMetaPath( RepositoryId repositoryId, Branch branch );

    PathRef versionMetaPath( RepositoryId repositoryId );

    PathRef commitMetaPath( RepositoryId repositoryId );

    PathRef metaDataFile();
}
