package com.enonic.xp.blob;

public interface EvictableBlobStore
{
    /**
     * Evicts cached blob records, least recently modified first, until the cache is within its configured capacity.
     * Records without a valid last modified time are never evicted, as they cannot be ordered.
     *
     * @return number of evicted records
     */
    long evict();
}
