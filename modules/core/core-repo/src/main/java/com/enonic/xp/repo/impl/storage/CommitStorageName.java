package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.repository.RepositoryId;

public class CommitStorageName
    extends BaseStorageName
{
    private static final String SEARCH_INDEX_PREFIX = "commit";

    CommitStorageName( final String name )
    {
        super( name );
    }

    public static CommitStorageName from( final RepositoryId repositoryId )
    {
        return new CommitStorageName( getStorageName( SEARCH_INDEX_PREFIX, repositoryId ) );
    }

    @Override
    public String toString()
    {
        return "CommitStorageName{" + "name='" + this.getName() + '\'' + '}';
    }
}


