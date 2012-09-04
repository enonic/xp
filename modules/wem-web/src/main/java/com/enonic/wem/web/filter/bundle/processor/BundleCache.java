package com.enonic.wem.web.filter.bundle.processor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

final class BundleCache
{
    private final Cache<String, String> cache;

    public BundleCache()
    {
        this.cache = CacheBuilder.newBuilder().maximumSize( 100 ).build();
    }

    public String get( final String key )
    {
        return this.cache.getIfPresent( key );
    }

    public void put( final String key, final String content )
    {
        this.cache.put( key, content );
    }
}
