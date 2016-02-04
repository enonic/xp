package com.enonic.xp.repo.impl.blob.objectstore;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.repo.impl.blob.BlobKey;
import com.enonic.xp.repo.impl.blob.BlobRecord;
import com.enonic.xp.repo.impl.blob.BlobStoreException;

public class AmazonS3BlobStore
    extends AbstractReadThroughBlobStore
{
    private final String bucketName;

    private final AmazonS3Client client;

    private AmazonS3BlobStore( Builder builder )
    {
        super( builder );
        bucketName = builder.bucketName;
        client = builder.client;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public BlobRecord getRecord( final BlobKey key )
        throws BlobStoreException
    {
        final BlobRecord localRecord = this.localStore.getRecord( key );

        if ( localRecord != null )
        {
            return localRecord;
        }

        final S3Object object = getFromS3( key );

        storeToLocal( object );

        return this.localStore.getRecord( key );
    }

    @Override
    public BlobRecord addRecord( final ByteSource in )
        throws BlobStoreException
    {
        final BlobRecord blobRecord = this.localStore.addRecord( in );

        try (final InputStream stream = in.openStream())
        {
            storeToS3( blobRecord, stream );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Storing object failed, could not open stream", e );
        }

        return blobRecord;
    }

    private void storeToLocal( final S3Object object )
    {
        try (InputStream stream = object.getObjectContent())
        {
            final ByteSource byteSource = ByteSource.wrap( ByteStreams.toByteArray( stream ) );

            this.localStore.addRecord( byteSource );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    private S3Object getFromS3( final BlobKey key )
    {
        try
        {
            return this.client.getObject( new GetObjectRequest( this.bucketName, key.toString() ) );
        }
        catch ( AmazonServiceException ase )
        {
            throw new BlobStoreException( "Getting object from Amazon S3 failed, operation rejected", ase );
        }
        catch ( AmazonClientException ace )
        {
            throw new BlobStoreException( "Getting object from Amazon S3 failed, could not connect", ace );
        }
    }

    private void storeToS3( final BlobRecord blobRecord, final InputStream stream )
    {
        try
        {
            this.client.putObject( new PutObjectRequest( this.bucketName, blobRecord.getKey().toString(), stream, new ObjectMetadata() ) );
        }
        catch ( AmazonServiceException ase )
        {
            throw new BlobStoreException( "Storing object to Amazon S3 failed, object rejected", ase );
        }
        catch ( AmazonClientException ace )
        {
            throw new BlobStoreException( "Storing object to Amazon S3 failed, could not connect", ace );
        }
    }

    public static final class Builder
        extends AbstractReadThroughBlobStore.Builder<Builder>
    {
        private String bucketName;

        private AmazonS3Client client;

        public Builder bucketName( final String bucketName )
        {
            this.bucketName = bucketName;
            return this;
        }

        public Builder client( final AmazonS3Client client )
        {
            this.client = client;
            return this;
        }

        public AmazonS3BlobStore build()
        {
            return new AmazonS3BlobStore( this );
        }
    }
}
