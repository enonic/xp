package com.enonic.xp.script.impl.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

public final class ScriptExportsCache<T>
{
    private final Function<ResourceKey, Resource> resourceLookup;

    private final Runnable expiredCallback;

    private final Map<ResourceKey, ScriptExportEntry<T>> cache = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();

    public ScriptExportsCache( final Function<ResourceKey, Resource> resourceLookup, Runnable expiredCallback )
    {
        this.resourceLookup = resourceLookup;
        this.expiredCallback = expiredCallback;
    }

    public T getOrCompute( final ResourceKey key, final Function<Resource, T> requireFunction )
        throws InterruptedException, TimeoutException
    {
        ScriptExportEntry<T> cached = cache.get( key );
        if ( cached != null )
        {
            return cached.value;
        }
        if ( lock.tryLock( 5, TimeUnit.MINUTES ) )
        {
            try
            {
                cached = cache.get( key );

                if ( cached != null )
                {
                    return cached.value;
                }
                final Resource resource = resourceLookup.apply( key );
                final T value = requireFunction.apply( resource );
                cache.put( key, new ScriptExportEntry<>( resource, value ) );
                return value;
            }
            finally
            {
                lock.unlock();
            }
        }
        else
        {
            throw new TimeoutException();
        }
    }

    public void expireCacheIfNeeded()
    {
        if ( isExpired() )
        {
            expiredCallback.run();
            lock.lock();
            try
            {
                cache.clear();
            }
            finally
            {
                lock.unlock();
            }
        }
    }

    private boolean isExpired()
    {
        return cache.values().stream().anyMatch( ScriptExportEntry::isExpired );
    }

    private static class ScriptExportEntry<T>
    {
        final Resource resource;

        final T value;

        final long timestamp;

        ScriptExportEntry( final Resource resource, final T value )
        {
            this.resource = resource;
            this.value = value;
            this.timestamp = this.resource.getTimestamp();
        }

        boolean isExpired()
        {
            return this.resource.getTimestamp() > this.timestamp;
        }
    }
}
