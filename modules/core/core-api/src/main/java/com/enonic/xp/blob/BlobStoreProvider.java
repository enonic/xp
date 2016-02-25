package com.enonic.xp.blob;

public interface BlobStoreProvider
{
    BlobStore get();

    String name();

    ProviderConfig config();
}
