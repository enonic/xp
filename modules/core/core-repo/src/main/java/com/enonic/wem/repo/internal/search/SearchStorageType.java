package com.enonic.wem.repo.internal.search;

import com.enonic.wem.repo.internal.StorageType;
import com.enonic.xp.branch.Branch;

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


