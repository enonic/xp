package com.enonic.xp.repository;

public final class DeleteRepositoryParams
{
    private final RepositoryId repositoryId;

    private DeleteRepositoryParams( final RepositoryId repositoryId )
    {
        this.repositoryId = repositoryId;
    }

    public static DeleteRepositoryParams from( final String repositoryId )
    {
        return new DeleteRepositoryParams( RepositoryId.from( repositoryId ) );
    }

    public static DeleteRepositoryParams from( final RepositoryId repositoryId )
    {
        return new DeleteRepositoryParams( repositoryId );
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }
}


