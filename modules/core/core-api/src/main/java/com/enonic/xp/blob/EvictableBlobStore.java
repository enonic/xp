package com.enonic.xp.blob;

public interface EvictableBlobStore
{
    /**
     * Evicts randomly chosen cached blob records until the cache is within its configured capacity.
     * Evicting a record that is still in use is harmless: the next read repopulates it from the main store.
     *
     * @return number of evicted records
     */
    long evict();
}
