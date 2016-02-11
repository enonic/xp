package com.enonic.xp.blobstore.config;

public interface BlobStoreConfig
{
    String providerName();

    boolean cache();

    long cacheSizeThreshold();

    long memoryCapacity();
}
