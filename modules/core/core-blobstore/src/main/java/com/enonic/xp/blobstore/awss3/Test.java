package com.enonic.xp.blobstore.awss3;

public class Test
{

    public static void main( String... args )
    {
        final AwsS3BlobStore awsS3BlobStore = new AwsS3BlobStore();
        awsS3BlobStore.activate( null );
    }

}
