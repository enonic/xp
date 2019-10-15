package com.enonic.xp.script.impl.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

final class ScriptExportsCache
{
    private final Map<ResourceKey, ScriptExportEntry> cache;

    ScriptExportsCache()
    {
        this.cache = new ConcurrentHashMap<>();
    }

    public Object get( final ResourceKey key )
    {
        final ScriptExportEntry entry = this.cache.get( key );
        return entry != null ? entry.value : null;
    }

    public void put( final Resource resource, final Object value )
    {
        final ResourceKey key = resource.getKey();
        this.cache.put( key, new ScriptExportEntry( resource, value ) );
    }

    public void clear()
    {
        this.cache.clear();
    }

    public boolean isExpired()
    {
        for ( final ScriptExportEntry entry : this.cache.values() )
        {
            if ( entry.isExpired() )
            {
                return true;
            }
        }

        return false;
    }
}
