package com.enonic.xp.blobstore.awss3;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blobstore.ClassLoaderHelper;
import com.enonic.xp.blobstore.awss3.config.S3Configuration;

class AwsS3BlobStore
    implements BlobStore
{
    public void activate( final S3Configuration config )
    {
        final Iterable<ProviderMetadata> providers = ClassLoaderHelper.callWith( Providers::all, Providers.class );

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
