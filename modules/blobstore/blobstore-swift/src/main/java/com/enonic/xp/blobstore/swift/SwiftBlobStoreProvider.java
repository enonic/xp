package com.enonic.xp.blobstore.swift;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreProvider;
import com.enonic.xp.blob.ProviderConfig;
import com.enonic.xp.blobstore.swift.config.SwiftConfig;

@Component
public class SwiftBlobStoreProvider
    implements BlobStoreProvider
{
    private SwiftBlobStore blobStore;

    private SwiftConfig config;

    private static final Logger LOG = LoggerFactory.getLogger( SwiftBlobStoreProvider.class );

    @Override
    public BlobStore get()
    {
        if ( this.blobStore == null )
        {
            connect();
        }

        return this.blobStore;
    }

    private void connect()
    {
        if ( !this.config.isValid() )
        {
            LOG.error( "Cannot connect to blobstore [" + this.name() + "], invalid config" );
            return;
        }

        this.blobStore = new SwiftBlobStore( this.config );
    }

    @Override
    public String name()
    {
        return "swift";
    }

    @Override
    public ProviderConfig config()
    {
        return this.config;
    }

    @Reference
    public void setConfig( final SwiftConfig config )
    {
        this.config = config;
    }
}
