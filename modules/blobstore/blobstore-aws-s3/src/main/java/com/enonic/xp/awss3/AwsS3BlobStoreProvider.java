package com.enonic.xp.awss3;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.awss3.config.AwsS3Config;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreProvider;
import com.enonic.xp.blob.ProviderConfig;

@Component
public class AwsS3BlobStoreProvider
    implements BlobStoreProvider
{
    private AwsS3BlobStore blobStore;

    private AwsS3Config config;

    private static final Logger LOG = LoggerFactory.getLogger( AwsS3BlobStoreProvider.class );

    @Deactivate
    public void deactivate()
    {
        if ( this.blobStore != null )
        {
            this.blobStore.close();
        }
    }

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

        this.blobStore = AwsS3BlobStore.create().
            accessKey( config.accessKey() ).
            secretAccessKey( config.secretAccessKey() ).
            endpoint( config.endpoint() ).
            setBuckets( config.segments() ).
            build();
    }

    @Override
    public String name()
    {
        return "s3";
    }

    @Override
    public ProviderConfig config()
    {
        return this.config;
    }

    @Reference
    public void setConfig( final AwsS3Config config )
    {
        this.config = config;
    }
}
