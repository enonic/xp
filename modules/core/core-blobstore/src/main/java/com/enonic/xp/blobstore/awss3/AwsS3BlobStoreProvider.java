package com.enonic.xp.blobstore.awss3;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blobstore.BlobStoreProvider;
import com.enonic.xp.blobstore.awss3.config.S3Configuration;

@Component(configurationPid = "com.enonic.xp.aws.s3")
public class AwsS3BlobStoreProvider
    implements BlobStoreProvider
{
    private AwsS3BlobStore blobStore;

    @Activate
    public void activate( final S3Configuration config )
    {
        this.blobStore = new AwsS3BlobStore();
        this.blobStore.activate( config );
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
