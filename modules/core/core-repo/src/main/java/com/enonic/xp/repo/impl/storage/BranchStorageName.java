package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.repository.RepositoryId;

public class BranchStorageName
    extends BaseStorageName
{
    private static final String SEARCH_INDEX_PREFIX = "branch";

    BranchStorageName( final String name )
    {
        super( name );
    }

    public static BranchStorageName from( final RepositoryId repositoryId )
    {
        return new BranchStorageName( getStorageName( SEARCH_INDEX_PREFIX, repositoryId ) );
    }

    @Override
    public String toString()
    {
        return "BranchStorageName{" + "name='" + this.getName() + '\'' + '}';
    }
}


