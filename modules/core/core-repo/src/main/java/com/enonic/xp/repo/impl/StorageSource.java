package com.enonic.xp.repo.impl;

public class StorageSource
{
    private final StorageName storageName;

    private final StorageType storageType;

    private StorageSource( final Builder builder )
    {
        this.storageName = builder.storageName;
        this.storageType = builder.storageType;
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

    public static final class Builder
    {
        private StorageName storageName;

        private StorageType storageType;

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

        public StorageSource build()
        {
            return new StorageSource( this );
        }
    }

    @Override
    public String toString()
    {
        return storageName + ":" + storageType;
    }
}
