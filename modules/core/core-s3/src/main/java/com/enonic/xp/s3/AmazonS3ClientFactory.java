package com.enonic.xp.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;

public class AmazonS3ClientFactory
{
    public static AmazonS3Client create()
    {
        AWSCredentials credentials = new BasicAWSCredentials( "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY" );
        AmazonS3Client newClient = new AmazonS3Client( credentials, new ClientConfiguration() );
        newClient.setS3ClientOptions( new S3ClientOptions().withPathStyleAccess( true ) );
        newClient.setEndpoint( "http://localhost:9444/s3" );

        return newClient;
    }
}
