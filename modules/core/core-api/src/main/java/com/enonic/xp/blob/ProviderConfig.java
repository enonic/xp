package com.enonic.xp.blob;

public interface ProviderConfig
{
    String readThroughProvider();

    boolean readThroughEnabled();

    long readThroughSizeThreshold();

    /**
     * Maximum total size in bytes of the read-through store. Enforced inline on every access:
     * records are admitted and evicted so that the store stays within this capacity.
     * Non-positive value means unbounded.
     */
    default long readThroughCacheCapacity()
    {
        return -1;
    }

    boolean isValid();
}
