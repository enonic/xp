package com.enonic.nodb.server;

import java.net.URI;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;

import com.google.protobuf.ByteString;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.TenantProvisioner;
import com.enonic.nodb.engine.snapshot.SnapshotObjectStore;
import com.enonic.nodb.engine.snapshot.SnapshotService;
import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.CreateRepositoryRequest;
import com.enonic.nodb.proto.v1.CreateSnapshotRequest;
import com.enonic.nodb.proto.v1.DeleteSnapshotRequest;
import com.enonic.nodb.proto.v1.ListSnapshotsRequest;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.PayloadRef;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;
import com.enonic.nodb.proto.v1.SnapshotInfo;
import com.enonic.nodb.proto.v1.SnapshotsGrpc;
import com.enonic.nodb.proto.v1.Version;
import com.enonic.nodb.proto.v1.WriteBatchRequest;
import com.enonic.nodb.server.auth.DevKeys;
import com.enonic.nodb.server.auth.JwtIssuer;
import com.enonic.nodb.server.auth.JwtVerifier;
import com.enonic.nodb.server.auth.Scope;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.service.NodeStoreService;
import com.enonic.nodb.server.service.RepositoryAdminService;
import com.enonic.nodb.server.service.SnapshotsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 5 Gate A wire test: the {@code Snapshots} RPCs end-to-end over an in-process gRPC
 * server + real Postgres + real MinIO — tenant identity from the bearer token only (so the
 * cross-tenant assertion is an authorization proof, mirroring
 * {@code BinariesServiceIntegrationTest}), engine outcomes mapped per the proto's status
 * table (unknown repo → NOT_FOUND).
 */
@Testcontainers
class SnapshotsServiceIntegrationTest
{
    private static final String BUCKET = "nodb-snapshots-rpc-test";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    @Container
    private static final MinIOContainer MINIO = new MinIOContainer( "minio/minio:RELEASE.2024-11-07T00-52-20Z" );

    private static HikariDataSource dataSource;

    private static S3Client s3;

    private static KeyPair issuerKeyPair;

    private static Server grpcServer;

    private static ManagedChannel channel;

    @BeforeAll
    static void setUp()
        throws Exception
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( POSTGRES.getJdbcUrl() );
        config.setUsername( POSTGRES.getUsername() );
        config.setPassword( POSTGRES.getPassword() );
        config.setMaximumPoolSize( 8 );
        dataSource = new HikariDataSource( config );

        TenantProvisioner provisioner = new TenantProvisioner( dataSource, POSTGRES.getUsername() );
        provisioner.provision( new TenantContext( "acme" ) );
        provisioner.provision( new TenantContext( "fisk" ) );

        s3 = S3Client.builder()
            .region( Region.US_EAST_1 )
            .endpointOverride( URI.create( MINIO.getS3URL() ) )
            .credentialsProvider(
                StaticCredentialsProvider.create( AwsBasicCredentials.create( MINIO.getUserName(), MINIO.getPassword() ) ) )
            .serviceConfiguration( S3Configuration.builder().pathStyleAccessEnabled( true ).build() )
            .build();
        s3.createBucket( CreateBucketRequest.builder().bucket( BUCKET ).build() );

        issuerKeyPair = DevKeys.loadOrGenerate( Files.createTempDirectory( "nodb-snapshots-test-dev-keys" ) );
        TenantAuthInterceptor authInterceptor =
            new TenantAuthInterceptor( new JwtVerifier( (RSAPublicKey) issuerKeyPair.getPublic() ) );

        SnapshotService snapshotService = new SnapshotService( dataSource, new SnapshotObjectStore( s3, BUCKET ) );

        String serverName = InProcessServerBuilder.generateName();
        grpcServer = InProcessServerBuilder.forName( serverName )
            .directExecutor()
            .addService( ServerInterceptors.intercept( new NodeStoreService( dataSource ), authInterceptor ) )
            .addService( ServerInterceptors.intercept( new RepositoryAdminService( dataSource ), authInterceptor ) )
            .addService( ServerInterceptors.intercept( new SnapshotsService( snapshotService ), authInterceptor ) )
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
        if ( dataSource != null )
        {
            dataSource.close();
        }
        if ( s3 != null )
        {
            s3.close();
        }
    }

    @Test
    void snapshotLifecycleOverTheWire()
    {
        String acme = token( "acme" );
        repositoryAdmin( acme ).createRepository( CreateRepositoryRequest.newBuilder().setRepoId( "wire.repo" ).build() );
        writeOneNode( acme, "wire.repo", "n1", "wire-content" );

        // Repo-scoped create.
        SnapshotInfo repoScoped =
            snapshots( acme ).createSnapshot( CreateSnapshotRequest.newBuilder().setRepoId( "wire.repo" ).build() );
        assertEquals( "COMPLETE", repoScoped.getState() );
        assertEquals( "REPO", repoScoped.getScope() );
        assertEquals( "wire.repo", repoScoped.getRepoId() );
        assertEquals( 1, repoScoped.getVersionCount() );
        assertEquals( 1, repoScoped.getHeadCount() );
        assertEquals( 3, repoScoped.getHashCount(), "three distinct payload segments, no binaries" );
        assertTrue( repoScoped.getTotalBytes() > 0 );
        assertTrue( repoScoped.getExpiresAtMillis() > repoScoped.getCreatedAtMillis() );
        assertTrue( repoScoped.getManifestSha256().startsWith( "sha256:" ) );

        // Tenant-scoped create.
        SnapshotInfo tenantScoped = snapshots( acme ).createSnapshot( CreateSnapshotRequest.newBuilder().build() );
        assertEquals( "TENANT", tenantScoped.getScope() );
        assertEquals( "", tenantScoped.getRepoId() );
        assertEquals( 1, tenantScoped.getVersionCount() );

        // List serves both, newest first.
        List<SnapshotInfo> listed = list( acme );
        assertEquals( 2, listed.size() );
        assertEquals( tenantScoped.getSnapshotId(), listed.get( 0 ).getSnapshotId() );
        assertEquals( repoScoped.getSnapshotId(), listed.get( 1 ).getSnapshotId() );

        // Delete: refuses nothing, idempotent second run.
        assertTrue( snapshots( acme ).deleteSnapshot(
            DeleteSnapshotRequest.newBuilder().setSnapshotId( repoScoped.getSnapshotId() ).build() ).getDeleted() );
        assertFalse( snapshots( acme ).deleteSnapshot(
            DeleteSnapshotRequest.newBuilder().setSnapshotId( repoScoped.getSnapshotId() ).build() ).getDeleted() );
        assertEquals( 1, list( acme ).size() );
    }

    /** Tenant identity comes ONLY from the token: fisk can neither see nor delete acme's snapshots. */
    @Test
    void snapshotsAreInvisibleAcrossTenants()
    {
        String acme = token( "acme" );
        String fisk = token( "fisk" );
        repositoryAdmin( acme ).createRepository( CreateRepositoryRequest.newBuilder().setRepoId( "iso.repo" ).build() );
        writeOneNode( acme, "iso.repo", "n1", "acme-only" );

        SnapshotInfo acmeSnapshot =
            snapshots( acme ).createSnapshot( CreateSnapshotRequest.newBuilder().setRepoId( "iso.repo" ).build() );

        assertEquals( List.of(), list( fisk ), "a fisk token lists nothing of acme's" );
        assertFalse( snapshots( fisk ).deleteSnapshot(
                         DeleteSnapshotRequest.newBuilder().setSnapshotId( acmeSnapshot.getSnapshotId() ).build() ).getDeleted(),
                     "a fisk token cannot address acme's snapshot — its own registry has no such row" );
        assertTrue( list( acme ).stream().anyMatch( info -> info.getSnapshotId().equals( acmeSnapshot.getSnapshotId() ) ),
                    "acme's snapshot survives the cross-tenant delete attempt" );
    }

    @Test
    void creatingASnapshotOfAnUnknownRepoIsNotFound()
    {
        StatusRuntimeException thrown = assertThrows( StatusRuntimeException.class, () -> snapshots( token( "acme" ) ).createSnapshot(
            CreateSnapshotRequest.newBuilder().setRepoId( "no.such.repo" ).build() ) );
        assertEquals( Status.Code.NOT_FOUND, thrown.getStatus().getCode() );
    }

    // ---- fixture helpers --------------------------------------------------------------

    /** One node through the real WriteBatch RPC — payloads, version, branch entry, one transaction. */
    private static void writeOneNode( String bearerToken, String repoId, String nodeId, String content )
    {
        List<byte[]> segments =
            List.of( ( "data-" + content ).getBytes(), ( "icfg-" + content ).getBytes(), ( "acl-" + content ).getBytes() );
        WriteBatchRequest.Builder request = WriteBatchRequest.newBuilder().setRepoId( repoId );
        List<String> hashes = new ArrayList<>();
        for ( byte[] segment : segments )
        {
            request.addPayloads( PayloadRef.newBuilder().setInline( ByteString.copyFrom( segment ) ) );
            hashes.add( sha256Key( segment ) );
        }
        long now = Instant.now().toEpochMilli();
        request.addVersions( Version.newBuilder()
                                 .setVersionId( "v-" + nodeId )
                                 .setNodeId( nodeId )
                                 .setNodePath( "/" + nodeId )
                                 .setTimestampMillis( now )
                                 .setNodeDataHash( hashes.get( 0 ) )
                                 .setIndexConfigHash( hashes.get( 1 ) )
                                 .setAclHash( hashes.get( 2 ) ) );
        request.addBranchEntries( BranchEntry.newBuilder()
                                      .setBranch( "master" )
                                      .setNodeId( nodeId )
                                      .setVersionId( "v-" + nodeId )
                                      .setNodePath( "/" + nodeId )
                                      .setTimestampMillis( now ) );
        nodeStore( bearerToken ).writeBatch( request.build() );
    }

    private static List<SnapshotInfo> list( String bearerToken )
    {
        List<SnapshotInfo> infos = new ArrayList<>();
        Iterator<SnapshotInfo> iterator = snapshots( bearerToken ).listSnapshots( ListSnapshotsRequest.newBuilder().build() );
        while ( iterator.hasNext() )
        {
            infos.add( iterator.next() );
        }
        return infos;
    }

    private static String sha256Key( byte[] bytes )
    {
        try
        {
            return "sha256:" + HexFormat.of().formatHex( MessageDigest.getInstance( "SHA-256" ).digest( bytes ) );
        }
        catch ( java.security.NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }

    // ---- token/stub helpers (NodbServerIntegrationTest's conventions) -------------------

    private static String token( String tenant )
    {
        return JwtIssuer.mint( (RSAPrivateKey) issuerKeyPair.getPrivate(), (RSAPublicKey) issuerKeyPair.getPublic(), tenant,
                               Scope.RUNTIME, "test-subject", Duration.ofMinutes( 30 ) );
    }

    private static SnapshotsGrpc.SnapshotsBlockingStub snapshots( String bearerToken )
    {
        return withAuth( SnapshotsGrpc.newBlockingStub( channel ), bearerToken );
    }

    private static NodeStoreGrpc.NodeStoreBlockingStub nodeStore( String bearerToken )
    {
        return withAuth( NodeStoreGrpc.newBlockingStub( channel ), bearerToken );
    }

    private static RepositoryAdminGrpc.RepositoryAdminBlockingStub repositoryAdmin( String bearerToken )
    {
        return withAuth( RepositoryAdminGrpc.newBlockingStub( channel ), bearerToken );
    }

    private static <T extends AbstractStub<T>> T withAuth( T stub, String bearerToken )
    {
        Metadata headers = new Metadata();
        headers.put( Metadata.Key.of( "authorization", Metadata.ASCII_STRING_MARSHALLER ), "Bearer " + bearerToken );
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
}
