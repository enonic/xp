package com.enonic.xp.script.impl.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import org.graalvm.polyglot.Value;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.server.RunMode;

final class ScriptExportsCache
{
    private final RunMode runMode;

    private final Function<ResourceKey, Resource> resourceLookup;

    private final Runnable expiredCallback;

    private final Map<ResourceKey, ScriptExportEntry> cache = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();

    ScriptExportsCache( final RunMode runMode, final Function<ResourceKey, Resource> resourceLookup, Runnable expiredCallback )
    {
        this.runMode = runMode;
        this.resourceLookup = resourceLookup;
        this.expiredCallback = expiredCallback;
    }

    public Value getOrCompute( final ResourceKey key, final Function<Resource, Value> requireFunction )
        throws InterruptedException, TimeoutException
    {
        ScriptExportEntry cached = cache.get( key );
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
                final Value value = requireFunction.apply( resource );
                cache.put( key, new ScriptExportEntry( resource, value ) );
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
        if ( this.runMode != RunMode.DEV )
        {
            return;
        }

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

    private static class ScriptExportEntry
    {
        final Resource resource;

        final Value value;

        final long timestamp;

        ScriptExportEntry( final Resource resource, final Value value )
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
