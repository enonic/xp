package com.enonic.xp.s3;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.s3.config.S3Configuration;

@Component(immediate = true, configurationPid = "com.enonic.xp.amazonS3")
public class AwsS3BlobStore
    implements BlobStore
{
    private String bucketName;

    @Activate
    public void activate( final S3Configuration config )
    {

    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        return null;
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        return null;
    }

}
