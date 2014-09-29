package com.enonic.wem.core.blob;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.core.blob.file.FileBlobStore;

public final class BlobModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( BlobStore.class ).to( FileBlobStore.class ).in( Scopes.SINGLETON );
        bind( BlobService.class ).to( BlobServiceImpl.class ).in( Scopes.SINGLETON );
    }
}
