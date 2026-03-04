package com.enonic.xp.repository;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.DuplicateElementException;

@PublicApi
public final class RepositoryAlreadyExistsException
    extends DuplicateElementException
{
    public RepositoryAlreadyExistsException( final RepositoryId repositoryId )
    {
        super( "Repository [{" + repositoryId + "}] already exists" );
    }

    @Override
    public String getCode()
    {
        return "repositoryAlreadyExists";
    }
}
