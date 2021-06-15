package com.enonic.xp.core.impl.app.resource;

import com.enonic.xp.resource.ResourceKey;

final class ProcessingEntry
{
    protected final ResourceKey key;

    protected final Object value;

    protected final long timestamp;

    ProcessingEntry( final ResourceKey key, final Object value, final long timestamp )
    {
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }
}
