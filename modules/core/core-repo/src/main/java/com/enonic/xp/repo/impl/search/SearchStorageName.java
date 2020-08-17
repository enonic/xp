package com.enonic.xp.repo.impl.search;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.storage.BaseStorageName;
import com.enonic.xp.repository.RepositoryId;

public class SearchStorageName
    extends BaseStorageName
{
    public static final String STORAGE_INDEX_PREFIX = "search";

    public static final String DIVIDER = "-";

    private final String name;
    public static final String STORAGE_INDEX_PREFIX = "search";

    SearchStorageName( final String name )
    {
        super( name );
    }

    public static SearchStorageName from( final RepositoryId repositoryId, final Branch branch )
    {
        return new SearchStorageName(
            STORAGE_INDEX_PREFIX + DIVIDER + repositoryId.toString() + DIVIDER + branch.getValue().toLowerCase() );
    }

    @Override
    public String toString()
    {
        return "SearchStorageName{" + "name='" + this.getName() + '\'' + '}';
    }
}
