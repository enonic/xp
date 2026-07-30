package com.enonic.xp.core.impl.app.resource;

final class ProcessingEntry
{
    final Object value;

    final long timestamp;

    final BundleStamp stamp;

    final boolean stable;

    ProcessingEntry( final Object value, final long timestamp, final BundleStamp stamp, final boolean stable )
    {
        this.value = value;
        this.timestamp = timestamp;
        this.stamp = stamp;
        this.stable = stable;
    }
}
