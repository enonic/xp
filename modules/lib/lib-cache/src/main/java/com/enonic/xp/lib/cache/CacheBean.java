package com.enonic.xp.lib.cache;

import java.util.concurrent.Callable;

import com.google.common.cache.Cache;

public final class CacheBean
{
    private final Cache<Object, Object> cache;

    public CacheBean( final Cache<Object, Object> cache )
    {
        this.cache = cache;
    }

    public Object get( final String key, final Callable<Object> callback )
        throws Exception
    {
        return this.cache.get( key, callback );
    }

    public void clear()
    {
        this.cache.invalidateAll();
    }

    public int getSize()
    {
        return (int) this.cache.size();
    }
}
