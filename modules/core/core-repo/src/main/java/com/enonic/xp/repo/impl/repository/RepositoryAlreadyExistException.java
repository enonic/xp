package com.enonic.xp.repo.impl.repository;


import com.google.common.annotations.Beta;

import com.enonic.xp.repository.RepositoryId;

@Beta
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
