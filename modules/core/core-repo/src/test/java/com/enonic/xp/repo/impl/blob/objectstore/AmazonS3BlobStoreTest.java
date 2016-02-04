package com.enonic.xp.repo.impl.blob.objectstore;

import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.io.ByteSource;

import com.enonic.xp.repo.impl.blob.BlobKey;
import com.enonic.xp.repo.impl.blob.BlobRecord;
import com.enonic.xp.repo.impl.blob.BlobStore;

public class AmazonS3BlobStoreTest
{

    private BlobStore localStore;

    private AmazonS3Client client;

    @Test
    public void store()
        throws Exception
    {
        client = Mockito.mock( AmazonS3Client.class );
        localStore = Mockito.mock( BlobStore.class );

        final AmazonS3BlobStore blobStore = AmazonS3BlobStore.create().
            bucketName( "myBucket" ).
            client( client ).
            localStore( localStore ).
            build();

        final ByteSource byteSource = ByteSource.wrap( "This is my file".getBytes() );

        final BlobRecord abc = newRecord( "abc", 12l );

        Mockito.when( localStore.addRecord( byteSource ) ).
            thenReturn( abc );

        blobStore.addRecord( byteSource );

        Mockito.verify( this.client, Mockito.times( 1 ) ).putObject( Mockito.isA( PutObjectRequest.class ) );
    }

    private BlobRecord newRecord( final String key, final long size )
    {
        final BlobRecord record = Mockito.mock( BlobRecord.class );
        Mockito.when( record.getKey() ).thenReturn( new BlobKey( key ) );
        Mockito.when( record.getLength() ).thenReturn( size );
        return record;
    }
}