package com.enonic.xp.repository;

import com.enonic.xp.exception.BaseException;

public class RepositoryNotFoundException
    extends BaseException
{
    public RepositoryNotFoundException( final RepositoryId repositoryId )
    {
        super( "Repository with id [" + repositoryId + "] not found" );
    }

    public String getCode()
    {
        return "repositoryNotFound";
    }
}
