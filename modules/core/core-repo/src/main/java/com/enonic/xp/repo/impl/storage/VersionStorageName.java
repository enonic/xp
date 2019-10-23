package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.repository.RepositoryId;

public class VersionStorageName
    extends BaseStorageName
{
    private static final String SEARCH_INDEX_PREFIX = "version";

    VersionStorageName( final String name )
    {
        super( name );
    }

    public static VersionStorageName from( final RepositoryId repositoryId )
    {
        return new VersionStorageName( getStorageName( SEARCH_INDEX_PREFIX, repositoryId ) );
    }

    @Override
    public String toString()
    {
        return "VersionStorageName{" + "name='" + this.getName() + '\'' + '}';
    }
}


