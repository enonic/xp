package com.enonic.xp.repo.impl.search;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.StorageType;

public class SearchStorageType
    implements StorageType
{
    private final String name;

    private SearchStorageType( final String name )
    {
        this.name = name;
    }

    public static StorageType from( final Branch branch )
    {
        return new SearchStorageType( branch.getName() );
    }

    @Override
    public String getName()
    {
        return this.name;
    }
}


