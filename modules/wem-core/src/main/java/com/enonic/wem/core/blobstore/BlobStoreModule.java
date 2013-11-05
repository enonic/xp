package com.enonic.wem.core.blobstore;


import javax.inject.Inject;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.core.blobstore.file.FileBlobStore;
import com.enonic.wem.core.blobstore.gc.GarbageCollector;
import com.enonic.wem.core.config.SystemConfig;


public final class BlobStoreModule
    extends AbstractModule
{
    private SystemConfig systemConfig;

    @Override
    protected void configure()
    {
        bind( BlobStore.class ).to( FileBlobStore.class ).in( Scopes.SINGLETON );
        bind( GarbageCollector.class ).in( Scopes.SINGLETON );
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }
}
