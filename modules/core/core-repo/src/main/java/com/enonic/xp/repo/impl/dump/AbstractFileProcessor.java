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

    protected Path createBranchRootPath( final Path basePath, final RepositoryId repositoryId )
    {
        return Paths.get( createRepoRootPath( basePath ).toString(), repositoryId.toString() );
    }

    protected Path createMetaPath( final Path basePath, final RepositoryId repositoryId, final Branch branch )
    {
        return Paths.get( createBranchRootPath( basePath, repositoryId ).toString(), branch.toString(), "meta.tar.gz" );
    }
}
