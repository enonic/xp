package com.enonic.wem.portal.script.cache;

import org.mozilla.javascript.Script;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public final class ScriptCacheImpl
    implements ScriptCache
{
    private final Cache<String, Script> cache;

    public ScriptCacheImpl()
    {
        this.cache = CacheBuilder.newBuilder().maximumSize( 1000 ).build();
    }

    @Override
    public Script get( final String key )
    {
        return this.cache.getIfPresent( key );
    }

    @Override
    public void put( final String key, final Script script )
    {
        this.cache.put( key, script );
    }
}
