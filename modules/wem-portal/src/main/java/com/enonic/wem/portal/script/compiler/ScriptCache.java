package com.enonic.wem.portal.script.compiler;

import org.mozilla.javascript.Script;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

final class ScriptCache
{
    private final Cache<String, Script> cache;

    public ScriptCache()
    {
        this.cache = CacheBuilder.newBuilder().maximumSize( 1000 ).build();
    }

    public Script get( final String key )
    {
        return this.cache.getIfPresent( key );
    }

    public void put( final String key, final Script script )
    {
        this.cache.put( key, script );
    }
}
