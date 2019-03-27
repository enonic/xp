package com.enonic.xp.repo.impl.dump;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public abstract class AbstractFileProcessor
{
    protected Path createRepoRootPath( final Path basePath )
    {
        return Paths.get( basePath.toString(), DumpConstants.META_BASE_PATH );
    }

    protected Path createRepoPath( final Path basePath, final RepositoryId repositoryId )
    {
        return Paths.get( basePath.toString(), DumpConstants.META_BASE_PATH, repositoryId.toString() );
    }


    protected Path createBranchRootPath( final Path basePath, final RepositoryId repositoryId )
    {
        return Paths.get( createRepoRootPath( basePath ).toString(), repositoryId.toString() );
    }

    protected Path createBranchMetaPath( final Path basePath, final RepositoryId repositoryId, final Branch branch )
    {
        return Paths.get( createBranchRootPath( basePath, repositoryId ).toString(), branch.toString(), "meta.tar.gz" );
    }

    protected Path createVersionMetaPath( final Path basePath, final RepositoryId repositoryId )
    {
        return Paths.get( createBranchRootPath( basePath, repositoryId ).toString(), "versions.tar.gz" );
    }

    protected Path createCommitMetaPath( final Path basePath, final RepositoryId repositoryId )
    {
        return Paths.get( createBranchRootPath( basePath, repositoryId ).toString(), "commits.tar.gz" );
    }
}
