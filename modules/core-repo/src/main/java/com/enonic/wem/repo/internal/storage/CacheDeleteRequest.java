package com.enonic.wem.repo.internal.storage;

import java.util.Set;

import com.google.common.collect.Sets;

public class CacheDeleteRequest
{
    private Set<CacheKey> cacheKeys;

    private String id;

    private CacheDeleteRequest( Builder builder )
    {
        cacheKeys = builder.cacheKeys;
        id = builder.id;
    }

    public Set<CacheKey> getCacheKeys()
    {
        return cacheKeys;
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
        private Set<CacheKey> cacheKeys = Sets.newHashSet();

        private String id;

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

        public CacheDeleteRequest build()
        {
            return new CacheDeleteRequest( this );
        }
    }
}
