package com.enonic.wem.repo.internal.storage;

import com.enonic.xp.repository.RepositoryId;

public class StoreStorageName
    implements StorageName
{
    private final static String SEARCH_INDEX_PREFIX = "storage";

    private final static String DIVIDER = "-";

    private final String name;

    private StoreStorageName( final String name )
    {
        this.name = name;
    }

    public static StoreStorageName from( final RepositoryId repositoryId )
    {
        return new StoreStorageName( SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString() );
    }

    @Override
    public String getName()
    {
        return name;
    }
}


