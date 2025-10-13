package com.enonic.xp.repository;


import com.enonic.xp.exception.DuplicateElementException;

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
        return "branchAlreadyExists";
    }
}
