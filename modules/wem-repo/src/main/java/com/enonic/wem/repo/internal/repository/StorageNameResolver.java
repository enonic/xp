package com.enonic.wem.repo.internal.repository;

import com.enonic.wem.api.repository.RepositoryId;

public class StorageNameResolver
{
    public final static String STORAGE_INDEX_PREFIX = "storage";

    public final static String DIVIDER = "-";

    public static String resolveStorageIndexName( final RepositoryId repositoryId )
    {
        return STORAGE_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }
}
