package com.enonic.wem.repo.internal.storage;

public class StorageSettings
{
    private final StorageName storageName;

    private final StorageType storageType;

    private StorageSettings( final Builder builder )
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

        public StorageSettings build()
        {
            return new StorageSettings( this );
        }
    }
}
