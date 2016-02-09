package com.enonic.xp.blobstore;

import com.enonic.xp.blob.BlobStore;

public interface BlobStoreProvider
{
    BlobStore get();

    String name();
}
