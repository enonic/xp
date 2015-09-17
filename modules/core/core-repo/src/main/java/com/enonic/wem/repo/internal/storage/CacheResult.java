package com.enonic.wem.repo.internal.storage;

public class CacheResult
{
    private final StorageData storageData;

    private final String id;

    public CacheResult( final StorageData storageData, final String id )
    {
        this.storageData = storageData;
        this.id = id;
    }

    public static CacheResult empty()
    {
        return new CacheResult( null, null );
    }

    public StorageData getStorageData()
    {
        return storageData;
    }

    public String getId()
    {
        return id;
    }

    public boolean exists()
    {
        return id != null && this.storageData != null;
    }

}


