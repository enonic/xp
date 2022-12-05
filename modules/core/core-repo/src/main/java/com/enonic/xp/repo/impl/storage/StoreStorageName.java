package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.repo.impl.StorageName;
import com.enonic.xp.repository.RepositoryId;

public final class StoreStorageName
    implements StorageName
{
    private static final String SEARCH_INDEX_PREFIX = "storage";

    private static final String DIVIDER = "-";

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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final StoreStorageName that = (StoreStorageName) o;

        return name.equals( that.name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return name;
    }
}


