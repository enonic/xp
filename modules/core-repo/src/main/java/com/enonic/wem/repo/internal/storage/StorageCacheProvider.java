package com.enonic.wem.repo.internal.storage;

public class StorageCacheProvider
{
    public static StorageCache provide()
    {
        return new SimpleCache();
    }

}
