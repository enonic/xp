package com.enonic.wem.repo.internal.storage;

public class StorageSettings
{
    private final StorageName storageName;

    private final StorageType storageType;

    private final boolean forceRefresh;

    private StorageSettings( Builder builder )
    {
        storageName = builder.storageName;
        storageType = builder.storageType;
        forceRefresh = builder.forceRefresh;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public StorageName getStorageName()
    {
        return storageName;
    }

    public StorageType getStorageType()
    {
        return storageType;
    }

    public boolean forceRefresh()
    {
        return forceRefresh;
    }

    public static final class Builder
    {
        private StorageName storageName;

        private StorageType storageType;

        private boolean forceRefresh;

        private Builder()
        {
        }

        public Builder storageName( StorageName storageName )
        {
            this.storageName = storageName;
            return this;
        }

        public Builder storageType( StorageType storageType )
        {
            this.storageType = storageType;
            return this;
        }

        public Builder forceRefresh( boolean forceRefresh )
        {
            this.forceRefresh = forceRefresh;
            return this;
        }

        public StorageSettings build()
        {
            return new StorageSettings( this );
        }
    }
}
