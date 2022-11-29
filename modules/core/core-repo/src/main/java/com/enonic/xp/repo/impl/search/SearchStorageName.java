package com.enonic.xp.repo.impl.search;

import com.enonic.xp.repo.impl.StorageName;
import com.enonic.xp.repository.RepositoryId;

public final class SearchStorageName
    implements StorageName
{
    public static final String STORAGE_INDEX_PREFIX = "search";

    public static final String DIVIDER = "-";

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

        final SearchStorageName that = (SearchStorageName) o;

        return name.equals( that.name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
