package com.enonic.xp.script.impl.executor;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

final class ScriptExportsCache
{
    private final Map<ResourceKey, ScriptExportEntry> cache;

    public ScriptExportsCache()
    {
        this.cache = Maps.newHashMap();
    }

    public Object get( final ResourceKey key )
    {
        final ScriptExportEntry entry = this.cache.get( key );
        return entry != null ? entry.value : null;
    }

    public void put( final Resource resource, final Object value )
    {
        final ResourceKey key = resource.getKey();
        this.cache.put( key, new ScriptExportEntry( key, value, resource.getTimestamp() ) );
    }

    public boolean isModified( final Resource resource )
    {
        final ResourceKey key = resource.getKey();
        final ScriptExportEntry entry = this.cache.get( key );
        return entry == null || !resource.exists() || resource.getTimestamp() > entry.timestamp;
    }
}
