package com.enonic.xp.awss3;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

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

    @Activate
    public void activate()
    {
        if ( config.accessKey() == null )
        {
            return;
        }

        this.blobStore = AwsS3BlobStore.create().
            accessKey( config.accessKey() ).
            secretAccessKey( config.secretAccessKey() ).
            endpoint( config.endpoint() ).
            bucketName( config.bucketName() ).
            build();
    }

    @Deactivate
    public void deactivate()
    {
        this.blobStore.close();
    }

    @Override
    public BlobStore get()
    {
        return this.blobStore;
    }

    @Override
    public String name()
    {
        return "aws-s3";
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
