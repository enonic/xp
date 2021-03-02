package com.enonic.xp.internal.blobstore.file.config;

import java.nio.file.Path;

import com.enonic.xp.blob.ProviderConfig;

public interface FileBlobStoreConfig
    extends ProviderConfig
{
    Path baseDir();
}
