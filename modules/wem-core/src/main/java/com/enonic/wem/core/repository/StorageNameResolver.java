package com.enonic.wem.core.repository;

import com.enonic.wem.api.repository.Repository;

public class StorageNameResolver
{
    public final static String STORAGE_INDEX_PREFIX = "storage";

    public final static String DIVIDER = "-";

    public static String resolveStorageIndexName( final Repository repository )
    {
        return STORAGE_INDEX_PREFIX + DIVIDER + repository.getId().toString();
    }
}
