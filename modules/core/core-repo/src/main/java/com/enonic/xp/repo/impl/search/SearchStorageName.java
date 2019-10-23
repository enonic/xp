package com.enonic.xp.repo.impl.search;

import com.enonic.xp.repo.impl.storage.BaseStorageName;
import com.enonic.xp.repository.RepositoryId;

public class SearchStorageName
    extends BaseStorageName
{
    public static final String STORAGE_INDEX_PREFIX = "search";

    SearchStorageName( final String name )
    {
        super( name );
    }

    public static SearchStorageName from( final RepositoryId repositoryId )
    {
        return new SearchStorageName( getStorageName( STORAGE_INDEX_PREFIX, repositoryId ) );
    }

    @Override
    public String toString()
    {
        return "SearchStorageName{" + "name='" + this.getName() + '\'' + '}';
    }
}
