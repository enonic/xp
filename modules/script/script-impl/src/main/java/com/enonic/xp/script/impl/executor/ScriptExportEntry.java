package com.enonic.xp.script.impl.executor;

import com.enonic.xp.resource.ResourceKey;

final class ScriptExportEntry
{
    protected final ResourceKey key;

    protected final Object value;

    protected final long timestamp;

    public ScriptExportEntry( final ResourceKey key, final Object value, final long timestamp )
    {
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }
}
