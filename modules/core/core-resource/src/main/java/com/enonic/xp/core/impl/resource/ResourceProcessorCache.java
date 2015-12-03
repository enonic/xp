package com.enonic.xp.core.impl.resource;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import com.google.common.collect.Maps;

import com.enonic.xp.resource.ResourceKey;

final class ResourceProcessorCache
{
    private final boolean enable;

    private final ConcurrentMap<String, Object> cache;

    public ResourceProcessorCache( final boolean enable )
    {
        this.enable = enable;
        this.cache = Maps.newConcurrentMap();
    }

    public <T> T process( final Class<T> type, final ResourceKey key, final Function<ResourceKey, T> processor )
    {
        if ( !this.enable )
        {
            return processor.apply( key );
        }

        final Object value = doProcess( type, key, processor );
        return value != null ? type.cast( value ) : null;
    }

    private Object doProcess( final Class type, final ResourceKey key, final Function<ResourceKey, ?> processor )
    {
        final String cacheKey = type.getName() + "-" + key;
        return this.cache.computeIfAbsent( cacheKey, s -> processor.apply( key ) );
    }

    public void invalidate()
    {
        this.cache.clear();
    }
}
