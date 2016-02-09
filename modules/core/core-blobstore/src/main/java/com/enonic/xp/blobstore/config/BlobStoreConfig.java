package com.enonic.xp.blobstore.config;

import java.io.File;

public interface BlobStoreConfig
{
    String providerName();

    boolean cache();

    File blobStoreDir();
}
