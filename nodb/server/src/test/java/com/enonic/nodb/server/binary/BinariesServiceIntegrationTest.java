package com.enonic.nodb.server.binary;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.google.protobuf.ByteString;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.StreamObserver;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.enonic.nodb.engine.binary.BinaryStore;
import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.BinariesGrpc;
import com.enonic.nodb.proto.v1.BinaryExistsRequest;
import com.enonic.nodb.proto.v1.DeleteBinaryRequest;
import com.enonic.nodb.proto.v1.ExistsResponse;
import com.enonic.nodb.proto.v1.GetBinaryChunk;
import com.enonic.nodb.proto.v1.GetBinaryRequest;
import com.enonic.nodb.proto.v1.PresignGetRequest;
import com.enonic.nodb.proto.v1.PresignGetResponse;
import com.enonic.nodb.proto.v1.PutBinaryChunk;
import com.enonic.nodb.proto.v1.PutBinaryResponse;
import com.enonic.nodb.server.auth.DevKeys;
import com.enonic.nodb.server.auth.JwtIssuer;
import com.enonic.nodb.server.auth.Scope;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.auth.JwtVerifier;
import com.enonic.nodb.server.service.BinariesService;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gate A: the {@code Binaries} gRPC service end-to-end over a real in-process gRPC server
 * + a real MinIO container — no Postgres needed here (unlike {@code
 * NodbServerIntegrationTest}): {@link BinariesService}/{@link BinaryStore} never touch the
 * database, only S3 and the (DB-free) JWT auth interceptor.
 *
 * <p>Covers what the engine-level {@code BinaryStoreTest} structurally cannot: tenant
 * identity here comes ONLY from the bearer token (via {@link TenantAuthInterceptor}), never
 * from a request field — so "cross-tenant isolation" in this class is a real authorization
 * proof (a fisk-scoped token literally cannot address acme's binary), not just "different
 * keys were used".
 */
@Testcontainers
class BinariesServiceIntegrationTest
{
    private static final String BUCKET = "nodb-server-test-bucket";

    @Container
    private static final MinIOContainer MINIO = new MinIOContainer( "minio/minio:RELEASE.2024-11-07T00-52-20Z" );

    private static S3Client rawS3Client;

    private static BinaryStore binaryStore;

    private static KeyPair issuerKeyPair;

    private static Server grpcServer;

    private static ManagedChannel channel;

    @BeforeAll
    static void setUp()
        throws Exception
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

        // Same documented test-limitation posture as BinaryStoreTest: no STS client, so
        // presignGet exercises its base-credential fallback branch here too.
        binaryStore = new BinaryStore( rawS3Client, presigner, null, null, BUCKET, region, endpoint, serviceConfiguration );

        issuerKeyPair = DevKeys.loadOrGenerate( Files.createTempDirectory( "nodb-binaries-test-dev-keys" ) );
        TenantAuthInterceptor authInterceptor =
            new TenantAuthInterceptor( new JwtVerifier( (RSAPublicKey) issuerKeyPair.getPublic() ) );

        String serverName = InProcessServerBuilder.generateName();
        grpcServer = InProcessServerBuilder.forName( serverName )
            .directExecutor()
            .addService( ServerInterceptors.intercept( new BinariesService( binaryStore ), authInterceptor ) )
            .build()
            .start();
        channel = InProcessChannelBuilder.forName( serverName ).directExecutor().build();
    }

    @AfterAll
    static void tearDown()
    {
        if ( channel != null )
        {
            channel.shutdownNow();
        }
        if ( grpcServer != null )
        {
            grpcServer.shutdownNow();
        }
        if ( binaryStore != null )
        {
            binaryStore.close();
        }
    }

    // ---- token / stub helpers -------------------------------------------------------------

    private static String token( String tenant, Scope scope )
    {
        return JwtIssuer.mint( (RSAPrivateKey) issuerKeyPair.getPrivate(), (RSAPublicKey) issuerKeyPair.getPublic(), tenant, scope,
                                "test-subject", Duration.ofMinutes( 30 ) );
    }

    private static <T extends AbstractStub<T>> T withAuth( T stub, String bearerToken )
    {
        Metadata headers = new Metadata();
        if ( bearerToken != null )
        {
            headers.put( Metadata.Key.of( "authorization", Metadata.ASCII_STRING_MARSHALLER ), "Bearer " + bearerToken );
        }
        return stub.withInterceptors( attachHeaders( headers ) );
    }

    private static ClientInterceptor attachHeaders( Metadata extraHeaders )
    {
        return new ClientInterceptor()
        {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall( MethodDescriptor<ReqT, RespT> method, CallOptions callOptions,
                                                                          Channel next )
            {
                return new ForwardingClientCall.SimpleForwardingClientCall<>( next.newCall( method, callOptions ) )
                {
                    @Override
                    public void start( Listener<RespT> responseListener, Metadata headers )
                    {
                        headers.merge( extraHeaders );
                        super.start( responseListener, headers );
                    }
                };
            }
        };
    }

    private static BinariesGrpc.BinariesStub asyncBinaries( String bearerToken )
    {
        return withAuth( BinariesGrpc.newStub( channel ), bearerToken );
    }

    private static BinariesGrpc.BinariesBlockingStub blockingBinaries( String bearerToken )
    {
        return withAuth( BinariesGrpc.newBlockingStub( channel ), bearerToken );
    }

    // ---- upload/download helpers (exercise the streaming RPCs directly, chunked) --------

    private static final int TEST_CHUNK_SIZE = 64 * 1024;

    /** Streams {@code content} up via PutBinary in {@link #TEST_CHUNK_SIZE}-sized chunks. */
    private static String uploadViaPutBinary( BinariesGrpc.BinariesStub stub, byte[] content )
        throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch( 1 );
        AtomicReference<PutBinaryResponse> responseRef = new AtomicReference<>();
        AtomicReference<Throwable> errorRef = new AtomicReference<>();

        StreamObserver<PutBinaryChunk> requestObserver = stub.putBinary( new StreamObserver<>()
        {
            @Override
            public void onNext( PutBinaryResponse value )
            {
                responseRef.set( value );
            }

            @Override
            public void onError( Throwable t )
            {
                errorRef.set( t );
                latch.countDown();
            }

            @Override
            public void onCompleted()
            {
                latch.countDown();
            }
        } );

        for ( int offset = 0; offset < content.length; offset += TEST_CHUNK_SIZE )
        {
            int len = Math.min( TEST_CHUNK_SIZE, content.length - offset );
            requestObserver.onNext(
                PutBinaryChunk.newBuilder().setChunk( ByteString.copyFrom( content, offset, len ) ).build() );
        }
        if ( content.length == 0 )
        {
            // still exercise a real (zero-chunk) upload rather than skipping it
        }
        requestObserver.onCompleted();

        assertTrue( latch.await( 30, TimeUnit.SECONDS ), "PutBinary did not complete in time" );
        if ( errorRef.get() != null )
        {
            throw sneakyThrowStatus( errorRef.get() );
        }
        return responseRef.get().getHash();
    }

    private static RuntimeException sneakyThrowStatus( Throwable t )
    {
        if ( t instanceof RuntimeException re )
        {
            return re;
        }
        return new RuntimeException( t );
    }

    private static byte[] downloadViaGetBinary( BinariesGrpc.BinariesBlockingStub stub, String hash )
    {
        Iterator<GetBinaryChunk> chunks = stub.getBinary( GetBinaryRequest.newBuilder().setHash( hash ).build() );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // ByteArrayOutputStream#write(byte[]) never throws IOException -- avoids
        // ByteString#writeTo's checked signature inside this Consumer lambda.
        chunks.forEachRemaining( chunk -> out.writeBytes( chunk.getChunk().toByteArray() ) );
        return out.toByteArray();
    }

    private static byte[] randomBytes( int size )
    {
        byte[] bytes = new byte[size];
        new SecureRandom().nextBytes( bytes );
        return bytes;
    }

    // ---- 1. streamed upload/download round-trip -------------------------------------------

    @Test
    void putBinaryThenGetBinaryRoundTripsMultiChunkContent()
        throws Exception
    {
        // Several times TEST_CHUNK_SIZE, so both the upload (client-streaming) and download
        // (server-streaming) directions genuinely span multiple chunks.
        byte[] content = randomBytes( TEST_CHUNK_SIZE * 9 + 12345 );

        String hash = uploadViaPutBinary( asyncBinaries( token( "acme", Scope.RUNTIME ) ), content );
        assertTrue( hash.startsWith( "sha256:" ) );

        byte[] downloaded = downloadViaGetBinary( blockingBinaries( token( "acme", Scope.RUNTIME ) ), hash );
        assertArrayEquals( content, downloaded );

        boolean exists =
            blockingBinaries( token( "acme", Scope.RUNTIME ) ).binaryExists( BinaryExistsRequest.newBuilder().setHash( hash ).build() )
                .getExists();
        assertTrue( exists );
    }

    @Test
    void getBinaryForUnknownHashFailsNotFound()
    {
        StatusRuntimeException thrown = assertThrows( StatusRuntimeException.class, () -> blockingBinaries(
            token( "acme", Scope.RUNTIME ) ).getBinary( GetBinaryRequest.newBuilder().setHash( "sha256:" + "0".repeat( 64 ) ).build() )
            .hasNext() );
        assertEquals( Status.Code.NOT_FOUND, thrown.getStatus().getCode() );
    }

    // ---- 2. dedup ---------------------------------------------------------------------------

    @Test
    void repeatedUploadDedupsToOneS3Object()
        throws Exception
    {
        byte[] content = randomBytes( 300_000 );
        BinariesGrpc.BinariesStub acme = asyncBinaries( token( "acme", Scope.RUNTIME ) );

        String firstHash = uploadViaPutBinary( acme, content );
        String secondHash = uploadViaPutBinary( acme, content.clone() );
        assertEquals( firstHash, secondHash );

        String expectedKey = "acme/binary/" + firstHash.substring( "sha256:".length() );
        List<S3Object> matches =
            rawS3Client.listObjectsV2( ListObjectsV2Request.builder().bucket( BUCKET ).prefix( expectedKey ).build() ).contents();
        assertEquals( 1, matches.size(), "dedup must not create a second S3 object for identical content" );
    }

    // ---- 3. cross-tenant isolation (the security-relevant assertion) ----------------------

    @Test
    void fiskTokenCannotGetOrDeleteAcmesBinaryEvenForTheExactSameHash()
        throws Exception
    {
        byte[] content = randomBytes( 8192 );
        String hash = uploadViaPutBinary( asyncBinaries( token( "acme", Scope.RUNTIME ) ), content );

        BinariesGrpc.BinariesBlockingStub fisk = blockingBinaries( token( "fisk", Scope.RUNTIME ) );

        // fisk's own prefix has no such object -> BinaryExists is false, not an error.
        boolean fiskSeesIt = fisk.binaryExists( BinaryExistsRequest.newBuilder().setHash( hash ).build() ).getExists();
        assertFalse( fiskSeesIt, "fisk must not see acme's binary under its own prefix, even for the identical hash" );

        // fisk's GetBinary for the exact same hash string resolves under fisk's OWN prefix
        // (fisk/binary/<hex>, not acme/binary/<hex>) -- structurally impossible to read
        // acme's bytes, regardless of what hash string is supplied.
        StatusRuntimeException getThrown = assertThrows( StatusRuntimeException.class,
                                                           () -> fisk.getBinary( GetBinaryRequest.newBuilder().setHash( hash ).build() )
                                                               .hasNext() );
        assertEquals( Status.Code.NOT_FOUND, getThrown.getStatus().getCode() );

        // fisk "deleting" the same hash is a no-op against fisk's own (empty) prefix --
        // acme's copy must survive.
        fisk.deleteBinary( DeleteBinaryRequest.newBuilder().setHash( hash ).build() );

        BinariesGrpc.BinariesBlockingStub acme = blockingBinaries( token( "acme", Scope.RUNTIME ) );
        assertTrue( acme.binaryExists( BinaryExistsRequest.newBuilder().setHash( hash ).build() ).getExists(),
                    "fisk's delete call must not remove acme's binary" );
        byte[] stillThere = downloadViaGetBinary( acme, hash );
        assertArrayEquals( content, stillThere );

        // Ground truth at the S3 level: the object physically exists ONLY under acme's
        // prefix, never fisk's.
        String acmeKey = "acme/binary/" + hash.substring( "sha256:".length() );
        String fiskKey = "fisk/binary/" + hash.substring( "sha256:".length() );
        assertEquals( 1,
                       rawS3Client.listObjectsV2( ListObjectsV2Request.builder().bucket( BUCKET ).prefix( acmeKey ).build() )
                           .contents()
                           .size() );
        assertEquals( 0,
                       rawS3Client.listObjectsV2( ListObjectsV2Request.builder().bucket( BUCKET ).prefix( fiskKey ).build() )
                           .contents()
                           .size() );
    }

    @Test
    void acmeCanDeleteItsOwnBinary()
        throws Exception
    {
        byte[] content = randomBytes( 4096 );
        BinariesGrpc.BinariesBlockingStub acme = blockingBinaries( token( "acme", Scope.RUNTIME ) );
        String hash = uploadViaPutBinary( asyncBinaries( token( "acme", Scope.RUNTIME ) ), content );

        Ack ack = acme.deleteBinary( DeleteBinaryRequest.newBuilder().setHash( hash ).build() );
        assertEquals( 0, ack.getOutboxSeq() ); // Binaries RPCs don't feed the outbox

        assertFalse( acme.binaryExists( BinaryExistsRequest.newBuilder().setHash( hash ).build() ).getExists() );
        StatusRuntimeException thrown = assertThrows( StatusRuntimeException.class, () -> acme.getBinary(
            GetBinaryRequest.newBuilder().setHash( hash ).build() ).hasNext() );
        assertEquals( Status.Code.NOT_FOUND, thrown.getStatus().getCode() );
    }

    // ---- 4. no-token auth wiring (Binaries goes through the same interceptor) -----------

    @Test
    void noTokenFailsUnauthenticatedOnBinaries()
    {
        BinariesGrpc.BinariesBlockingStub noToken = blockingBinaries( null );
        StatusRuntimeException thrown = assertThrows( StatusRuntimeException.class,
                                                        () -> noToken.binaryExists(
                                                            BinaryExistsRequest.newBuilder().setHash( "sha256:" + "1".repeat( 64 ) )
                                                                .build() ) );
        assertEquals( Status.Code.UNAUTHENTICATED, thrown.getStatus().getCode() );
    }

    // ---- 5. presignGet: working URL, scoped to the tenant prefix -------------------------

    @Test
    void presignGetReturnsAUrlScopedToTheTenantPrefixAndItWorks()
        throws Exception
    {
        byte[] content = randomBytes( 2048 );
        String hash = uploadViaPutBinary( asyncBinaries( token( "acme", Scope.RUNTIME ) ), content );

        PresignGetResponse response = blockingBinaries( token( "acme", Scope.RUNTIME ) ).presignGet(
            PresignGetRequest.newBuilder().setHash( hash ).setTtlSeconds( 300 ).build() );

        String url = response.getUrl();
        assertTrue( url.contains( "acme/binary/" ), "presigned URL must address acme's tenant-prefixed key: " + url );

        HttpURLConnection connection = (HttpURLConnection) URI.create( url ).toURL().openConnection();
        try
        {
            connection.setRequestMethod( "GET" );
            try (InputStream in = connection.getInputStream())
            {
                assertArrayEquals( content, in.readAllBytes() );
            }
        }
        finally
        {
            connection.disconnect();
        }
    }
}
