package com.enonic.wem.repo.internal.storage;

public class StorageSettings
{
    private final StorageName storageName;

    private final StorageType storageType;

    private final boolean forceRefresh;

    private final String parent;

    private final String routing;

    private StorageSettings( Builder builder )
    {
        storageName = builder.storageName;
        storageType = builder.storageType;
        forceRefresh = builder.forceRefresh;
        parent = builder.parent;
        routing = builder.routing;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder newBuilder()
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

    public String getParent()
    {
        return parent;
    }

    public String getRouting()
    {
        return routing;
    }

    public static final class Builder
    {
        private StorageName storageName;

        private StorageType storageType;

        private boolean forceRefresh;

        private String parent;

        private String routing;

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

        public Builder parent( String parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder routing( String routing )
        {
            this.routing = routing;
            return this;
        }

        public StorageSettings build()
        {
            return new StorageSettings( this );
        }
    }
}
