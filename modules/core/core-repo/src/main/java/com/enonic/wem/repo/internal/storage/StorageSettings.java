package com.enonic.wem.repo.internal.storage;

public class StorageSettings
{
    private final StorageName storageName;

    private final StorageType storageType;

    private StorageSettings( Builder builder )
    {
        storageName = builder.storageName;
        storageType = builder.storageType;
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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final StorageSettings that = (StorageSettings) o;

        if ( storageName != null ? !storageName.equals( that.storageName ) : that.storageName != null )
        {
            return false;
        }
        return !( storageType != null ? !storageType.equals( that.storageType ) : that.storageType != null );

    }

    @Override
    public int hashCode()
    {
        int result = storageName != null ? storageName.hashCode() : 0;
        result = 31 * result + ( storageType != null ? storageType.hashCode() : 0 );
        return result;
    }
}
