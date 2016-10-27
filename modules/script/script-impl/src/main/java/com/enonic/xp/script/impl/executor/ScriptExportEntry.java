package com.enonic.xp.script.impl.executor;

import com.enonic.xp.resource.Resource;

final class ScriptExportEntry
{
    private final Resource resource;

    final Object value;

    private final long timestamp;

    ScriptExportEntry( final Resource resource, final Object value )
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
