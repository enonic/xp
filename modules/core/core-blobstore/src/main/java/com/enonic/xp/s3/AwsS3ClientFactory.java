package com.enonic.xp.s3;

public class AwsS3ClientFactory
{
   /* public static AmazonS3Client create( final S3Configuration config )
    {
        System.out.println( "######## Config : " + config );

        AWSCredentials credentials = new BasicAWSCredentials( config.getAccessKey(), config.getSecretAccessKey() );

        AmazonS3Client client = new AmazonS3Client( credentials, new ClientConfiguration() );
        client.setS3ClientOptions( new S3ClientOptions().withPathStyleAccess( true ) );

        if ( config.getEndpoint() != null )
        {
            client.setEndpoint( config.getEndpoint() );
        }

        return client;
    }
    */
}
