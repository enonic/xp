package com.enonic.wem.repo.internal.storage;

import com.enonic.xp.repository.RepositoryId;

public class SearchStorageName
    implements StorageName
{
    private final static String STORAGE_INDEX_PREFIX = "storage";

    private final static String DIVIDER = "-";

    private final String name;

    private SearchStorageName( final String name )
    {
        this.name = name;
    }

    public static SearchStorageName from( final RepositoryId repositoryId )
    {
        return new SearchStorageName( STORAGE_INDEX_PREFIX + DIVIDER + repositoryId.toString() );
    }

    @Override
    public String getName()
    {
        return name;
    }
}
