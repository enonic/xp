package com.enonic.xp.internal.blobstore.file.config;

import java.io.File;

import com.enonic.xp.blob.ProviderConfig;

public interface FileBlobStoreConfig
    extends ProviderConfig
{
    File baseDir();
}
