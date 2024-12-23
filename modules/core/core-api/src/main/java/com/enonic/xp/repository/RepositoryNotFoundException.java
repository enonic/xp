package com.enonic.xp.repository;

import com.enonic.xp.exception.NotFoundException;

public class RepositoryNotFoundException
    extends NotFoundException
{
    public RepositoryNotFoundException( final RepositoryId repositoryId )
    {
        super( "Repository with id [" + repositoryId + "] not found" );
    }

    @Override
    public String getCode()
    {
        return "repositoryNotFound";
    }
}
