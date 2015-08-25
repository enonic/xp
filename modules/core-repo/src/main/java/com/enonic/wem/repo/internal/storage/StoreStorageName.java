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

        return !( name != null ? !name.equals( that.name ) : that.name != null );

    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }
}


