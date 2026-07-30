package com.enonic.xp.core.impl.app.resource;

import java.util.List;

final class MultiProcessingEntry
{
    final Object value;

    final long[] timestamps;

    final List<BundleStamp> stamps;

    MultiProcessingEntry( final Object value, final long[] timestamps, final List<BundleStamp> stamps )
    {
        this.value = value;
        this.timestamps = timestamps;
        this.stamps = stamps;
    }
}
