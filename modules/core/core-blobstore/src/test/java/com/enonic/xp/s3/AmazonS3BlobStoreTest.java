package com.enonic.xp.s3;

public class AmazonS3BlobStoreTest
{

    /*
    private BlobStore localStore;

    //private AmazonS3Client client;

    private final Segment segment = Segment.from( "test" );

    @Test
    public void store()
        throws Exception
    {
        /*
        client = Mockito.mock( AmazonS3Client.class );
        localStore = Mockito.mock( BlobStore.class );

        final AmazonS3BlobStore blobStore = AmazonS3BlobStore.create().
            bucketName( "myBucket" ).
            localStore( localStore ).
            build();

        blobStore.setClient( client );

        final ByteSource byteSource = ByteSource.wrap( "This is my file".getBytes() );

        final BlobRecord abc = newRecord( "abc", 12l );

        Mockito.when( localStore.addRecord( segment, byteSource ) ).
            thenReturn( abc );

        blobStore.addRecord( segment, byteSource );

        Mockito.verify( this.client, Mockito.times( 1 ) ).putObject( Mockito.isA( PutObjectRequest.class ) );
}

    private BlobRecord newRecord( final String key, final long size )
    {
        final BlobRecord record = Mockito.mock( BlobRecord.class );
        Mockito.when( record.getKey() ).thenReturn( new BlobKey( key ) );
        Mockito.when( record.getLength() ).thenReturn( size );
        return record;
    }
*/

}