package com.enonic.xp.awss3.config;

import java.util.Map;

import org.junit.Test;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;

import static org.junit.Assert.*;

public class AwsS3ConfigImplTest
{
    @Test
    public void buckets_map_populated_correctly()
        throws Exception
    {
        final Configuration config = ConfigBuilder.create().
            add( AwsS3ConfigImpl.COLLECTION_PROPERTY_NAME + "node", "nodeBucket" ).
            add( AwsS3ConfigImpl.COLLECTION_PROPERTY_NAME + "blob", "blobBucket" ).
            add( "other.property", "fisk" ).
            build();

        AwsS3ConfigImpl awsS3Config = new AwsS3ConfigImpl();
        awsS3Config.setConfig( config );

        final Map<Segment, String> buckets = awsS3Config.segments();

        assertEquals( 2, buckets.size() );
        assertEquals( buckets.get( Segment.from( "node" ) ), "nodeBucket" );
        assertEquals( buckets.get( Segment.from( "blob" ) ), "blobBucket" );
    }

    @Test
    public void missing_required_segments_populated_from_default()
        throws Exception
    {
        final Configuration config = ConfigBuilder.create().
            add( AwsS3ConfigImpl.COLLECTION_PROPERTY_NAME, "defaultBucket" ).
            add( AwsS3ConfigImpl.COLLECTION_PROPERTY_NAME + "fish", "fishBucket" ).
            build();

        AwsS3ConfigImpl awsS3Config = new AwsS3ConfigImpl();
        awsS3Config.setConfig( config );

        for ( final Segment segment : AwsS3ConfigImpl.REQUIRED_SEGMENTS )
        {
            assertEquals( "defaultBucket", awsS3Config.segments().get( segment ) );
        }

        assertEquals( "fishBucket", awsS3Config.segments().get( Segment.from( "fish" ) ) );
    }

    @Test
    public void valid_config()
        throws Exception
    {
        final Configuration config = ConfigBuilder.create().
            add( AwsS3ConfigImpl.COLLECTION_PROPERTY_NAME, "defaultBucket" ).
            add( AwsS3ConfigImpl.ACCESS_KEY, "myAccessKey" ).
            add( AwsS3ConfigImpl.SECRET_ACCESS_KEY, "mySecretAccessKey" ).
            build();

        AwsS3ConfigImpl awsS3Config = new AwsS3ConfigImpl();
        awsS3Config.setConfig( config );

        assertTrue( awsS3Config.isValid() );
    }

    @Test
    public void valid_config_specified_collections()
        throws Exception
    {
        final Configuration config = ConfigBuilder.create().
            add( AwsS3ConfigImpl.COLLECTION_PROPERTY_NAME, "" ).
            add( AwsS3ConfigImpl.COLLECTION_PROPERTY_NAME + "binary", "binaryBucker" ).
            add( AwsS3ConfigImpl.COLLECTION_PROPERTY_NAME + "node", "nodeBucket" ).
            add( AwsS3ConfigImpl.ACCESS_KEY, "myAccessKey" ).
            add( AwsS3ConfigImpl.SECRET_ACCESS_KEY, "mySecretAccessKey" ).
            build();

        AwsS3ConfigImpl awsS3Config = new AwsS3ConfigImpl();
        awsS3Config.setConfig( config );

        assertTrue( awsS3Config.isValid() );
    }


    @Test
    public void invalid_config()
        throws Exception
    {
        final Configuration config = ConfigBuilder.create().
            add( AwsS3ConfigImpl.ACCESS_KEY, "myAccessKey" ).
            add( AwsS3ConfigImpl.SECRET_ACCESS_KEY, "mySecretAccessKey" ).
            build();

        AwsS3ConfigImpl awsS3Config = new AwsS3ConfigImpl();
        awsS3Config.setConfig( config );

        assertFalse( awsS3Config.isValid() );
    }

}