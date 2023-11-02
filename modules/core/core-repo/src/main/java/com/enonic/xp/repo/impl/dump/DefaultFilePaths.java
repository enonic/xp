package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public class DefaultFilePaths
    implements FilePaths
{
    private final PathRef basePath;

    public DefaultFilePaths()
    {
        this( PathRef.of() );
    }

    public DefaultFilePaths( PathRef basePath )
    {
        this.basePath = basePath;
    }

    @Override
    public PathRef basePath()
    {
        return basePath;
    }

    @Override
    public PathRef metaDataFile()
    {
        return basePath.resolve( "dump.json" );
    }

    @Override
    public PathRef repoRootPath()
    {
        return basePath.resolve( "meta" );
    }

    @Override
    public PathRef repoPath( final RepositoryId repositoryId )
    {
        return repoRootPath().resolve( repositoryId.toString() );
    }

    @Override
    public PathRef branchRootPath( final RepositoryId repositoryId )
    {
        return repoRootPath().resolve( repositoryId.toString() );
    }

    @Override
    public PathRef branchMetaPath( final RepositoryId repositoryId, final Branch branch )
    {
        return branchRootPath( repositoryId ).resolve( branch.toString() ).resolve( "meta.tar.gz" );
    }

    @Override
    public PathRef versionMetaPath( final RepositoryId repositoryId )
    {
        return branchRootPath( repositoryId ).resolve( "versions.tar.gz" );
    }

    @Override
    public PathRef commitMetaPath( final RepositoryId repositoryId )
    {
        return branchRootPath( repositoryId ).resolve( "commits.tar.gz" );
    }
}
