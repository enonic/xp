package com.enonic.xp.core.impl.app.resource;

final class ProcessingEntry
{
    final Object value;

    final long timestamp;

    final BundleStamp stamp;

    ProcessingEntry( final Object value, final long timestamp, final BundleStamp stamp )
    {
        this.value = value;
        this.timestamp = timestamp;
        this.stamp = stamp;
    }
}
