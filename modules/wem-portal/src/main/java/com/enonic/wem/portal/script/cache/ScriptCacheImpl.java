package com.enonic.wem.portal.script.cache;

import java.nio.file.Path;

import org.mozilla.javascript.Script;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public final class ScriptCacheImpl
    implements ScriptCache
{
    private final Cache<Path, ScriptCacheEntry> cache;

    public ScriptCacheImpl()
    {
        this.cache = CacheBuilder.newBuilder().maximumSize( 1000 ).build();
    }

    @Override
    public Script get( final Path path )
    {
        final ScriptCacheEntry entry = this.cache.getIfPresent( path );
        if ( entry == null )
        {
            return null;
        }

        if ( entry.isModified() )
        {
            return null;
        }

        return entry.getScript();
    }

    @Override
    public void put( final Path path, final Script script )
    {
        this.cache.put( path, new ScriptCacheEntry( path, script ) );
    }
}
