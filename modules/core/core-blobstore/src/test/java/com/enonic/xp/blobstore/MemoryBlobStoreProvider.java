package com.enonic.xp.blobstore;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreProvider;
import com.enonic.xp.blob.ProviderConfig;

public class MemoryBlobStoreProvider
    implements BlobStoreProvider
{
    final MemoryBlobStore blobStore;

    final ProviderConfig config;

    final String name;

    public MemoryBlobStoreProvider( final String name, final MemoryBlobStore blobStore, final ProviderConfig config )
    {
        this.blobStore = blobStore;
        this.config = config;
        this.name = name;
    }

    @Override
    public BlobStore get()
    {
        return this.blobStore;
    }

    @Override
    public String name()
    {
        return this.name;
    }

    @Override
    public ProviderConfig config()
    {
        return this.config;
    }
}
