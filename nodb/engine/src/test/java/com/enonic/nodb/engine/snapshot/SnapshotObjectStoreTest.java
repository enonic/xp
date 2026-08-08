package com.enonic.nodb.engine.snapshot;

import java.net.URI;
import java.security.MessageDigest;
import java.util.List;
import java.util.Random;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link SnapshotObjectStore} against a real MinIO container (the same harness as
 * {@code BinaryStoreTest}): the streaming multipart writer must round-trip byte-identically
 * across the part boundary, small objects must take the single-PUT path, {@code abort}
 * must never make an object visible, and {@code deletePrefix} must remove all of — and
 * only — its prefix.
 */
@Testcontainers
class SnapshotObjectStoreTest
{
    private static final String BUCKET = "nodb-snapshot-object-test";

    @Container
    private static final MinIOContainer MINIO = new MinIOContainer( "minio/minio:RELEASE.2024-11-07T00-52-20Z" );

    private static S3Client s3;

    private static SnapshotObjectStore store;

    @BeforeAll
    static void setUp()
    {
        s3 = S3Client.builder()
            .region( Region.US_EAST_1 )
            .endpointOverride( URI.create( MINIO.getS3URL() ) )
            .credentialsProvider(
                StaticCredentialsProvider.create( AwsBasicCredentials.create( MINIO.getUserName(), MINIO.getPassword() ) ) )
            .serviceConfiguration( S3Configuration.builder().pathStyleAccessEnabled( true ).build() )
            .build();
        s3.createBucket( CreateBucketRequest.builder().bucket( BUCKET ).build() );

        // The S3 minimum part size, so the multipart boundary is crossed with the least data.
        store = new SnapshotObjectStore( s3, BUCKET, SnapshotObjectStore.MIN_PART_SIZE );
    }

    @AfterAll
    static void tearDown()
    {
        s3.close();
    }

    @Test
    void multipartUploadRoundTripsBytesAcrossThePartBoundary()
        throws Exception
    {
        // 12.5 MiB of incompressible pseudo-random bytes: two full 5 MiB parts + a final part.
        byte[] bytes = new byte[( 12 * 1024 + 512 ) * 1024];
        new Random( 42 ).nextBytes( bytes );

        String key = "acme/snapshot/multipart-test/large.bin";
        try (SnapshotObjectStore.ObjectWriter writer = store.write( key ))
        {
            // Odd-sized chunks so part boundaries never align with write() calls.
            for ( int offset = 0; offset < bytes.length; offset += 1_000_003 )
            {
                writer.write( bytes, offset, Math.min( 1_000_003, bytes.length - offset ) );
            }
        }

        byte[] roundTripped = store.get( key ).readAllBytes();
        assertEquals( bytes.length, roundTripped.length );
        assertArrayEquals( MessageDigest.getInstance( "SHA-256" ).digest( bytes ),
                           MessageDigest.getInstance( "SHA-256" ).digest( roundTripped ),
                           "multipart round trip must be byte-identical" );
    }

    @Test
    void smallObjectsTakeTheSinglePutPathAndRoundTrip()
        throws Exception
    {
        byte[] bytes = "small snapshot artifact".getBytes();
        String key = "acme/snapshot/small-test/small.bin";
        try (SnapshotObjectStore.ObjectWriter writer = store.write( key ))
        {
            writer.write( bytes );
        }
        assertArrayEquals( bytes, store.get( key ).readAllBytes() );
    }

    @Test
    void abortNeverMakesTheObjectVisible()
    {
        byte[] part = new byte[6 * 1024 * 1024]; // enough to start a real multipart upload
        new Random( 7 ).nextBytes( part );

        String key = "acme/snapshot/abort-test/torso.bin";
        SnapshotObjectStore.ObjectWriter writer = store.write( key );
        writer.write( part, 0, part.length );
        writer.abort();

        assertEquals( List.of(), store.listKeys( "acme/snapshot/abort-test/" ),
                      "an aborted upload must leave no visible object" );
    }

    @Test
    void deletePrefixRemovesAllOfAndOnlyItsPrefix()
        throws Exception
    {
        for ( String key : List.of( "acme/snapshot/del-test/a", "acme/snapshot/del-test/sub/b", "acme/snapshot/del-other/keep" ) )
        {
            try (SnapshotObjectStore.ObjectWriter writer = store.write( key ))
            {
                writer.write( key.getBytes() );
            }
        }

        assertEquals( 2, store.deletePrefix( "acme/snapshot/del-test/" ) );
        assertEquals( List.of(), store.listKeys( "acme/snapshot/del-test/" ) );
        assertEquals( List.of( "acme/snapshot/del-other/keep" ), store.listKeys( "acme/snapshot/del-other/" ),
                      "a neighbouring prefix must be untouched" );
        assertTrue( store.deletePrefix( "acme/snapshot/del-test/" ) == 0, "deleting an already-empty prefix is a no-op" );
    }
}
