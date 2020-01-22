package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.repository.RepositoryId;

public class StoreStorageName
    extends BaseStorageName
{
    private static final String SEARCH_INDEX_PREFIX = "storage";

    StoreStorageName( final String name )
    {
        super( name );
    }

    public static StoreStorageName from( final RepositoryId repositoryId )
    {
        return new StoreStorageName( getStorageName( SEARCH_INDEX_PREFIX, repositoryId ) );
    }

    @Override
    public String toString()
    {
        return "StoreStorageName{" + "name='" + this.getName() + '\'' + '}';
    }
}


