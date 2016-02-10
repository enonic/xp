package com.enonic.xp.awss3;

import java.io.IOException;
import java.io.InputStream;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeyCreator;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.util.ClassLoaderHelper;
import com.enonic.xp.util.Exceptions;

class AwsS3BlobStore
    implements BlobStore
{
    private BlobStoreContext context;

    private final org.jclouds.blobstore.BlobStore blobStore;

    private final String accessKey;

    private final String secretAccessKey;

    private final String endpoint;

    private final String bucketName;

    private AwsS3BlobStore( final Builder builder )
    {
        accessKey = builder.accessKey;
        secretAccessKey = builder.secretAccessKey;
        endpoint = builder.endpoint;
        bucketName = builder.bucketName;

        this.context = ClassLoaderHelper.callWith( this::createContext, ContextBuilder.class );

        this.blobStore = this.context.getBlobStore();

        verifyOrCreateBucket();
    }

    private void verifyOrCreateBucket()
    {
        try
        {
            final boolean exists = blobStore.createContainerInLocation( null, this.bucketName );

        }
        catch ( Exception e )
        {
            throw new BlobStoreException( "Cannot create or verify bucket [" + bucketName + "]", e );
        }
    }

    private BlobStoreContext createContext()
    {
        final ContextBuilder contextBuilder = ContextBuilder.newBuilder( "aws-s3" ).
            credentials( this.accessKey, this.secretAccessKey );

        if ( !Strings.isNullOrEmpty( endpoint ) )
        {
            contextBuilder.endpoint( this.endpoint );
        }

        return contextBuilder.buildView( BlobStoreContext.class );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {

        Stopwatch timer = Stopwatch.createStarted();

        final Blob blob = this.blobStore.getBlob( this.bucketName, key.toString() );

        try (final InputStream inputStream = blob.getPayload().openStream())
        {
            final ByteSource source = ByteSource.wrap( ByteStreams.toByteArray( inputStream ) );

            System.out.println( "Fetching blob: " + timer.stop().toString() );

            return new AwsS3BlobRecord( source, key );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        final BlobKey key = BlobKeyCreator.createKey( in );

        try
        {
            final Blob blob = blobStore.blobBuilder( key.toString() ).
                payload( in ).
                contentLength( in.size() ).
                build();

            Stopwatch timer = Stopwatch.createStarted();
            blobStore.putBlob( this.bucketName, blob );
            System.out.println( "Pushing blob: " + timer.stop().toString() );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }

        return new AwsS3BlobRecord( in, key );
    }

    public static final class Builder
    {
        private String accessKey;

        private String secretAccessKey;

        private String endpoint;

        private String bucketName;


        private Builder()
        {
        }

        public Builder accessKey( final String val )
        {
            accessKey = val;
            return this;
        }

        public Builder secretAccessKey( final String val )
        {
            secretAccessKey = val;
            return this;
        }

        public Builder endpoint( final String val )
        {
            endpoint = val;
            return this;
        }

        public Builder bucketName( final String val )
        {
            bucketName = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.accessKey, "accessKey must be configured" );
            Preconditions.checkNotNull( this.secretAccessKey, "secretAccessKey must be configured" );
            Preconditions.checkNotNull( this.bucketName, "bucketName must be configured" );
        }

        public AwsS3BlobStore build()
        {
            this.validate();
            return new AwsS3BlobStore( this );
        }
    }

    void close()
    {
        if ( this.context != null )
        {
            this.context.close();
        }
    }
}
