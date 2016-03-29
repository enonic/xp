package com.enonic.xp.blobstore.swift;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreProvider;
import com.enonic.xp.blob.ProviderConfig;
import com.enonic.xp.blob.SegmentsCollectionMap;
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

        this.blobStore = SwiftBlobStore.create().
            projectId( this.config.projectId() ).
            authPassword( this.config.authPassword() ).
            authUrl( this.config.authUrl() ).
            authUser( this.config.authUser() ).
            authVersion( this.config.authVersion() ).
            domainId( this.config.domainId() ).
            domainName( this.config.domainName() ).
            projectName( this.config.projectName() ).
            projectId( this.config.projectId() ).
            segmentCollectionMap( new SegmentsCollectionMap( this.config.segments() ) ).
            build();
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
