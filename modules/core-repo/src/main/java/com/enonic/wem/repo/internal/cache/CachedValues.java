package com.enonic.wem.repo.internal.cache;

import com.enonic.wem.repo.internal.storage.StorageData;

public class CachedValues
{
    private final String id;

    private final StorageData storageData;

    public CachedValues( final String id, final StorageData storageData )
    {
        this.id = id;
        this.storageData = storageData;
    }

    public String getId()
    {
        return id;
    }

    public StorageData getStorageData()
    {
        return storageData;
    }
}
