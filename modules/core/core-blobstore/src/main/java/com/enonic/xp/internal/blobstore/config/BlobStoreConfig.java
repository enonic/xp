package com.enonic.xp.internal.blobstore.config;

public interface BlobStoreConfig
{
    String providerName();

    boolean cache();

    long cacheSizeThreshold();

    long memoryCapacity();
}
