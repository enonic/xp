package com.enonic.xp.awss3;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.enonic.xp.awss3.config.AwsS3Config;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.BlobStoreProvider;

@Component(configurationPid = "com.enonic.xp.blobstore.s3")
public class AwsS3BlobStoreProvider
    implements BlobStoreProvider
{
    private AwsS3BlobStore blobStore;

    @Activate
    public void activate( final AwsS3Config config )
    {
        if ( config == null )
        {
            throw new BlobStoreException( "Config [com.enonic.xp.aws.s3] not found" );
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
}
