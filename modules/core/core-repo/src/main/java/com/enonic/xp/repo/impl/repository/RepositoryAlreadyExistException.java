package com.enonic.xp.repo.impl.repository;


import com.enonic.xp.repository.RepositoryId;

public class RepositoryAlreadyExistException
    extends RuntimeException
{
    private final RepositoryId repositoryId;

    public RepositoryAlreadyExistException( final RepositoryId repositoryId )
    {
        super( "Repository [{" + repositoryId + "}] already exists" );
        this.repositoryId = repositoryId;
    }

    public RepositoryId getPrincipal()
    {
        return repositoryId;
    }
}
