package com.enonic.wem.repo.internal.storage;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class CacheStoreRequest
{
    private Set<CacheKey> cacheKeys;

    private String id;

    private final StorageData storageData;

    private CacheStoreRequest( Builder builder )
    {
        cacheKeys = builder.cacheKeys;
        id = builder.id;
        storageData = builder.storageData;
    }

    public Set<CacheKey> getCacheKeys()
    {
        return cacheKeys;
    }

    public StorageData getStorageData()
    {
        return storageData;
    }

    public String getId()
    {
        return id;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        Set<CacheKey> cacheKeys = Sets.newHashSet();

        private String id;

        private StorageData storageData;

        private Builder()
        {
        }

        public Builder addCacheKey( final CacheKey cacheKey )
        {
            this.cacheKeys.add( cacheKey );
            return this;
        }

        public Builder id( String id )
        {
            this.id = id;
            return this;
        }

        public Builder storageData( StorageData storageData )
        {
            this.storageData = storageData;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.id, "id must be set in CacheStoreRequest" );
            Preconditions.checkNotNull( this.storageData, "storageData must be set in CacheStoreRequest" );
        }

        public CacheStoreRequest build()
        {
            this.validate();
            return new CacheStoreRequest( this );
        }
    }
}
