package com.enonic.nodb.engine.binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.enonic.nodb.engine.TenantContext;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gate A: {@link BinaryStore} over a real MinIO container — same testcontainers-Docker
 * harness proven live in Gate 0 (BUILD-PHASE-2.md), just now exercising the engine class
 * itself rather than a throwaway scratch project. Covers: streamed multi-chunk upload/
 * download round-trip, per-tenant dedup (asserted via a real S3 {@code ListObjectsV2},
 * not just "two calls succeeded"), exists/delete, and presigned GET (base-credential
 * fallback path — see {@link BinaryStore}'s class Javadoc for why the STS-scoped
 * production path isn't exercised in this gate's MinIO harness).
 *
 * <p>Tenant isolation via distinct S3 prefixes is exercised here too, but the SECURITY-
 * relevant cross-tenant assertion (a tenant-A token cannot address tenant-B's binaries at
 * all, because the server resolves {@code TenantContext} from the auth token, never from a
 * request field) lives in {@code BinariesServiceIntegrationTest} (server module) — this
 * engine-level test calls {@link BinaryStore} directly with whichever {@link
 * TenantContext} it likes, so "isolation" here is only the key-layout guarantee (different
 * tenants -> different S3 keys), not an authorization proof.
 */
@Testcontainers
class BinaryStoreTest
{
    private static final String BUCKET = "nodb-test-bucket";

    @Container
    private static final MinIOContainer MINIO = new MinIOContainer( "minio/minio:RELEASE.2024-11-07T00-52-20Z" );

    private static S3Client rawS3Client;

    private static BinaryStore binaryStore;

    private static final TenantContext ACME = new TenantContext( "acme" );

    private static final TenantContext FISK = new TenantContext( "fisk" );

    @BeforeAll
    static void setUp()
    {
        Region region = Region.US_EAST_1;
        URI endpoint = URI.create( MINIO.getS3URL() );
        StaticCredentialsProvider credentials =
            StaticCredentialsProvider.create( AwsBasicCredentials.create( MINIO.getUserName(), MINIO.getPassword() ) );
        S3Configuration serviceConfiguration = S3Configuration.builder().pathStyleAccessEnabled( true ).build();

        rawS3Client = S3Client.builder()
            .region( region )
            .endpointOverride( endpoint )
            .credentialsProvider( credentials )
            .serviceConfiguration( serviceConfiguration )
            .build();
        rawS3Client.createBucket( CreateBucketRequest.builder().bucket( BUCKET ).build() );

        S3Presigner presigner = S3Presigner.builder()
            .region( region )
            .endpointOverride( endpoint )
            .credentialsProvider( credentials )
            .serviceConfiguration( serviceConfiguration )
            .build();

        // No STS client/role ARN -- deliberately exercises presignGet's documented
        // base-credential fallback (see BinaryStore's class Javadoc "Gate A test
        // limitation" note); the STS-scoped branch is real production code, just not
        // reachable from this MinIO harness within this gate's time budget.
        binaryStore = new BinaryStore( rawS3Client, presigner, null, null, BUCKET, region, endpoint, serviceConfiguration );
    }

    @AfterAll
    static void tearDown()
    {
        if ( binaryStore != null )
        {
            binaryStore.close();
        }
    }

    private static byte[] randomBytes( int size )
    {
        byte[] bytes = new byte[size];
        new SecureRandom().nextBytes( bytes );
        return bytes;
    }

    private static byte[] readAll( InputStream in )
        throws IOException
    {
        return in.readAllBytes();
    }

    // ---- 1. streamed multi-chunk upload/download round-trip -----------------------------

    @Test
    void storeViaStreamAndGetRoundTripsMultiMegabyteContent()
        throws IOException
    {
        // A few MB, comfortably larger than any single internal copy buffer used by
        // BinaryStore (64 KiB) or the (future) gRPC chunking layer -- exercises genuinely
        // multi-chunk streaming, not a single-buffer shortcut.
        byte[] content = randomBytes( 5 * 1024 * 1024 + 137 );

        String hash;
        try (InputStream in = new ByteArrayInputStream( content ))
        {
            hash = binaryStore.store( ACME, in );
        }

        assertTrue( hash.startsWith( "sha256:" ) );
        assertTrue( binaryStore.exists( ACME, hash ) );

        try (InputStream fetched = binaryStore.get( ACME, hash ))
        {
            assertArrayEquals( content, readAll( fetched ) );
        }
    }

    @Test
    void storeViaBytesRoundTrips()
        throws IOException
    {
        byte[] content = "small-attachment-content".getBytes();
        String hash = binaryStore.store( ACME, content );

        try (InputStream fetched = binaryStore.get( ACME, hash ))
        {
            assertArrayEquals( content, readAll( fetched ) );
        }
    }

    @Test
    void getUnknownHashThrowsBinaryNotFound()
    {
        assertThrows( BinaryNotFoundException.class, () -> binaryStore.get( ACME, "sha256:" + "0".repeat( 64 ) ) );
    }

    // ---- 2. dedup: same bytes twice -> exactly one S3 object -----------------------------

    @Test
    void dedupWritesExactlyOneS3ObjectForRepeatedContent()
        throws IOException
    {
        byte[] content = randomBytes( 512 * 1024 );

        String firstHash = binaryStore.store( ACME, content );
        String secondHash = binaryStore.store( ACME, content.clone() ); // fresh array, same bytes

        assertEquals( firstHash, secondHash );

        // Ground truth at the S3 level: exactly one object under acme's binary prefix with
        // this exact key -- not merely "two store() calls both returned success".
        String expectedKey = "acme/binary/" + firstHash.substring( "sha256:".length() );
        ListObjectsV2Response listing = rawS3Client.listObjectsV2(
            ListObjectsV2Request.builder().bucket( BUCKET ).prefix( expectedKey ).build() );
        List<S3Object> matches = listing.contents();
        assertEquals( 1, matches.size(), "dedup must not create a second S3 object for identical content" );
        assertEquals( expectedKey, matches.get( 0 ).key() );
    }

    @Test
    void dedupIsPerTenantNotGlobal()
        throws IOException
    {
        // Gate 0's documented nuance (BUILD-PHASE-2.md): dedup scope is per-TENANT, not
        // cross-tenant -- identical bytes under two different tenants are two S3 objects,
        // one per tenant prefix, deliberately (DESIGN.md §7.2: global dedup would create a
        // cross-tenant existence oracle).
        byte[] content = randomBytes( 1024 );
        String acmeHash = binaryStore.store( ACME, content );
        String fiskHash = binaryStore.store( FISK, content.clone() );

        assertEquals( acmeHash, fiskHash, "the content hash itself is tenant-independent" );

        String acmeKey = "acme/binary/" + acmeHash.substring( "sha256:".length() );
        String fiskKey = "fisk/binary/" + fiskHash.substring( "sha256:".length() );
        assertTrue( rawS3Client.listObjectsV2( ListObjectsV2Request.builder().bucket( BUCKET ).prefix( acmeKey ).build() )
                        .contents()
                        .stream()
                        .anyMatch( o -> o.key().equals( acmeKey ) ) );
        assertTrue( rawS3Client.listObjectsV2( ListObjectsV2Request.builder().bucket( BUCKET ).prefix( fiskKey ).build() )
                        .contents()
                        .stream()
                        .anyMatch( o -> o.key().equals( fiskKey ) ) );
    }

    // ---- 3. exists / delete ----------------------------------------------------------------

    @Test
    void existsIsFalseThenTrueThenFalseAfterDelete()
        throws IOException
    {
        byte[] content = randomBytes( 4096 );
        String hash = "sha256:" + sha256Hex( content );

        assertFalse( binaryStore.exists( ACME, hash ), "must not exist before it's stored" );

        String storedHash = binaryStore.store( ACME, content );
        assertEquals( hash, storedHash );
        assertTrue( binaryStore.exists( ACME, hash ) );

        binaryStore.delete( ACME, hash );
        assertFalse( binaryStore.exists( ACME, hash ) );
        assertThrows( BinaryNotFoundException.class, () -> binaryStore.get( ACME, hash ) );
    }

    @Test
    void deletingAnAlreadyGoneHashIsANoOp()
    {
        // S3 DeleteObject is idempotent -- matches the store's documented convention.
        binaryStore.delete( ACME, "sha256:" + "f".repeat( 64 ) );
    }

    // ---- 4. presigned GET (base-credential fallback path; see BinaryStore Javadoc) -------

    @Test
    void presignGetReturnsAWorkingUrlScopedToExactlyOneObject()
        throws IOException, InterruptedException
    {
        byte[] content = "presign-me".getBytes();
        String hash = binaryStore.store( ACME, content );

        URL url = binaryStore.presignGet( ACME, hash, Duration.ofMinutes( 5 ) );
        assertTrue( url.toString().contains( "acme/binary/" ), "the presigned URL must address the tenant-prefixed key" );

        byte[] fetched = httpGet( url );
        assertArrayEquals( content, fetched );

        // Real security property (not faked): the presigned URL's SigV4 signature covers
        // the exact bucket+key+query params. Swapping the path to a DIFFERENT (but
        // existing) object under the SAME tenant invalidates the signature -- proving the
        // URL cannot be repurposed to fetch anything other than what it was minted for,
        // independent of whether STS session-credential scoping (the production path) is
        // in play.
        byte[] otherContent = "a-different-object".getBytes();
        String otherHash = binaryStore.store( ACME, otherContent );
        URL tamperedUrl = swapObjectKeyInUrl( url, hash, otherHash );
        int status = httpStatus( tamperedUrl );
        assertTrue( status == 403 || status == 400, "a tampered presigned URL must be rejected, got HTTP " + status );
    }

    private static URL swapObjectKeyInUrl( URL original, String originalHash, String otherHash )
        throws IOException
    {
        String originalHex = originalHash.substring( "sha256:".length() );
        String otherHex = otherHash.substring( "sha256:".length() );
        String tampered = original.toString().replace( originalHex, otherHex );
        return URI.create( tampered ).toURL();
    }

    private static byte[] httpGet( URL url )
        throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try
        {
            connection.setRequestMethod( "GET" );
            try (InputStream in = connection.getInputStream())
            {
                return in.readAllBytes();
            }
        }
        finally
        {
            connection.disconnect();
        }
    }

    private static int httpStatus( URL url )
        throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try
        {
            connection.setRequestMethod( "GET" );
            return connection.getResponseCode();
        }
        finally
        {
            connection.disconnect();
        }
    }

    private static String sha256Hex( byte[] bytes )
    {
        try
        {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance( "SHA-256" );
            byte[] hash = digest.digest( bytes );
            StringBuilder sb = new StringBuilder();
            for ( byte b : hash )
            {
                sb.append( String.format( "%02x", b ) );
            }
            return sb.toString();
        }
        catch ( java.security.NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }
}
