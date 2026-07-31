package com.enonic.xp.blob;

public interface ProviderConfig
{
    String readThroughProvider();

    boolean readThroughEnabled();

    long readThroughSizeThreshold();

    /**
     * Maximum total size in bytes of the read-through store. Blobs are evicted down to this capacity
     * by the ReadThroughCacheVacuumTask. Non-positive value means unbounded.
     */
    default long readThroughCacheCapacity()
    {
        return -1;
    }

    boolean isValid();
}
