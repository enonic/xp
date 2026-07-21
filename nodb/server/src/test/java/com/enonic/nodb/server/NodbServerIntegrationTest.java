package com.enonic.nodb.server;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.TenantProvisioner;
import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.BranchRef;
import com.enonic.nodb.proto.v1.Commit;
import com.enonic.nodb.proto.v1.CreateRepositoryRequest;
import com.enonic.nodb.proto.v1.DeleteBranchEntriesRequest;
import com.enonic.nodb.proto.v1.DeleteRepositoryRequest;
import com.enonic.nodb.proto.v1.DeleteVersionRequest;
import com.enonic.nodb.proto.v1.ExistsBranchEntryRequest;
import com.enonic.nodb.proto.v1.GetBranchEntriesRequest;
import com.enonic.nodb.proto.v1.GetBranchEntryRequest;
import com.enonic.nodb.proto.v1.GetBranchesWithNodeRequest;
import com.enonic.nodb.proto.v1.GetChildrenRequest;
import com.enonic.nodb.proto.v1.GetCommitRequest;
import com.enonic.nodb.proto.v1.GetPayloadRequest;
import com.enonic.nodb.proto.v1.GetPayloadsRequest;
import com.enonic.nodb.proto.v1.GetVersionRequest;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.Payload;
import com.enonic.nodb.proto.v1.PayloadRef;
import com.enonic.nodb.proto.v1.PutPayloadRequest;
import com.enonic.nodb.proto.v1.PutPayloadResponse;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;
import com.enonic.nodb.proto.v1.RepositoryExistsRequest;
import com.enonic.nodb.proto.v1.StoreBranchEntryRequest;
import com.enonic.nodb.proto.v1.StoreCommitRequest;
import com.enonic.nodb.proto.v1.StoreVersionRequest;
import com.enonic.nodb.proto.v1.Version;
import com.enonic.nodb.proto.v1.WriteBatchRequest;
import com.enonic.nodb.proto.v1.WriteBatchResponse;
import com.enonic.nodb.server.auth.DevKeys;
import com.enonic.nodb.server.auth.JwtIssuer;
import com.enonic.nodb.server.auth.JwtVerifier;
import com.enonic.nodb.server.auth.Scope;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.service.NodeStoreService;
import com.enonic.nodb.server.service.RepositoryAdminService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gate 5: gRPC server end-to-end over an in-process channel — auth interceptor
 * (UNAUTHENTICATED for missing/invalid/expired/wrong-audience tokens), tenant scoping
 * (WriteBatch + GetBranchEntry round-trip lands in the token's tenant schema only, cross-
 * tenant reads are structurally impossible since no request carries a tenant field), and
 * scope enforcement (operator-only management RPCs). One Postgres container and one
 * in-process gRPC server/channel are reused across all tests in this class.
 */
@Testcontainers
class NodbServerIntegrationTest
{
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    private static HikariDataSource dataSource;

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
        config.setMaximumPoolSize( 16 );
        dataSource = new HikariDataSource( config );

        TenantProvisioner provisioner = new TenantProvisioner( dataSource, POSTGRES.getUsername() );
        provisioner.provision( new TenantContext( "acme" ) );
        provisioner.provision( new TenantContext( "fisk" ) );

        issuerKeyPair = DevKeys.loadOrGenerate( Files.createTempDirectory( "nodb-test-dev-keys" ) );

        TenantAuthInterceptor authInterceptor =
            new TenantAuthInterceptor( new JwtVerifier( (RSAPublicKey) issuerKeyPair.getPublic() ) );

        String serverName = InProcessServerBuilder.generateName();
        grpcServer = InProcessServerBuilder.forName( serverName )
            .directExecutor()
            .addService( ServerInterceptors.intercept( new NodeStoreService( dataSource ), authInterceptor ) )
            .addService( ServerInterceptors.intercept( new RepositoryAdminService( dataSource ), authInterceptor ) )
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
    }

    // ---- token helpers ----------------------------------------------------------------

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

    private static NodeStoreGrpc.NodeStoreBlockingStub nodeStore( String bearerToken )
    {
        return withAuth( NodeStoreGrpc.newBlockingStub( channel ), bearerToken );
    }

    private static RepositoryAdminGrpc.RepositoryAdminBlockingStub repositoryAdmin( String bearerToken )
    {
        return withAuth( RepositoryAdminGrpc.newBlockingStub( channel ), bearerToken );
    }

    // ---- fixture helpers ----------------------------------------------------------------

    private static String sha256Key( byte[] bytes )
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            byte[] hash = digest.digest( bytes );
            StringBuilder sb = new StringBuilder( "sha256:" );
            for ( byte b : hash )
            {
                sb.append( String.format( "%02x", b ) );
            }
            return sb.toString();
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }

    private static byte[] randomBytes()
    {
        return ( "content-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 );
    }

    /** Creates a repo (with its default "master" branch) under {@code tenant}'s schema via the operator-scope RPC. */
    private static String createRepo( String tenant )
    {
        String repoId = "repo-" + UUID.randomUUID();
        repositoryAdmin( token( tenant, Scope.OPERATOR ) ).createRepository( CreateRepositoryRequest.newBuilder()
                                                                                  .setRepoId( repoId )
                                                                                  .build() );
        return repoId;
    }

    private record WrittenNode(String nodeId, String versionId)
    {
    }

    /** Writes a version + branch entry via WriteBatch (the already-proven path) — a fixture helper, not itself under test. */
    private static WrittenNode writeNode( NodeStoreGrpc.NodeStoreBlockingStub stub, String repoId, String branch, String nodePath )
    {
        byte[] dataBytes = randomBytes();
        byte[] indexBytes = randomBytes();
        byte[] aclBytes = randomBytes();
        String nodeId = UUID.randomUUID().toString();
        String versionId = UUID.randomUUID().toString();
        long nowMillis = System.currentTimeMillis();

        Version version = Version.newBuilder()
            .setVersionId( versionId )
            .setNodeId( nodeId )
            .setNodePath( nodePath )
            .setTimestampMillis( nowMillis )
            .setNodeDataHash( sha256Key( dataBytes ) )
            .setIndexConfigHash( sha256Key( indexBytes ) )
            .setAclHash( sha256Key( aclBytes ) )
            .build();
        BranchEntry entry = BranchEntry.newBuilder()
            .setBranch( branch )
            .setNodeId( nodeId )
            .setVersionId( versionId )
            .setNodePath( nodePath )
            .setTimestampMillis( nowMillis )
            .build();
        stub.writeBatch( WriteBatchRequest.newBuilder()
                              .setRepoId( repoId )
                              .addPayloads( PayloadRef.newBuilder().setInline( com.google.protobuf.ByteString.copyFrom( dataBytes ) ) )
                              .addPayloads( PayloadRef.newBuilder().setInline( com.google.protobuf.ByteString.copyFrom( indexBytes ) ) )
                              .addPayloads( PayloadRef.newBuilder().setInline( com.google.protobuf.ByteString.copyFrom( aclBytes ) ) )
                              .addVersions( version )
                              .addBranchEntries( entry )
                              .build() );
        return new WrittenNode( nodeId, versionId );
    }

    /** Version only (no branch entry) via the standalone StoreVersion RPC under test — used both as a direct StoreVersion test and as setup for StoreBranchEntry/DeleteVersion tests that need a version to reference without an accompanying branch pointer. */
    private static WrittenNode storeVersionOnly( NodeStoreGrpc.NodeStoreBlockingStub stub, String repoId, String versionId,
                                                  String nodePath )
    {
        byte[] dataBytes = randomBytes();
        byte[] indexBytes = randomBytes();
        byte[] aclBytes = randomBytes();
        stub.putPayload( PutPayloadRequest.newBuilder().setBytes( com.google.protobuf.ByteString.copyFrom( dataBytes ) ).build() );
        stub.putPayload( PutPayloadRequest.newBuilder().setBytes( com.google.protobuf.ByteString.copyFrom( indexBytes ) ).build() );
        stub.putPayload( PutPayloadRequest.newBuilder().setBytes( com.google.protobuf.ByteString.copyFrom( aclBytes ) ).build() );

        String nodeId = UUID.randomUUID().toString();
        Version version = Version.newBuilder()
            .setVersionId( versionId )
            .setNodeId( nodeId )
            .setNodePath( nodePath )
            .setTimestampMillis( System.currentTimeMillis() )
            .setNodeDataHash( sha256Key( dataBytes ) )
            .setIndexConfigHash( sha256Key( indexBytes ) )
            .setAclHash( sha256Key( aclBytes ) )
            .build();
        stub.storeVersion( StoreVersionRequest.newBuilder().setRepoId( repoId ).setVersion( version ).build() );
        return new WrittenNode( nodeId, versionId );
    }

    private static long countInSchema( String schema, String table, String whereClause )
        throws SQLException
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement( "SELECT count(*) FROM " + schema + "." + table + " WHERE " + whereClause ))
        {
            try (ResultSet resultSet = statement.executeQuery())
            {
                resultSet.next();
                return resultSet.getLong( 1 );
            }
        }
    }

    // ---- 1. no token --------------------------------------------------------------------

    @Test
    void noTokenFailsUnauthenticated()
    {
        StatusRuntimeException thrown = assertThrows( StatusRuntimeException.class,
                                                        () -> nodeStore( null ).writeBatch( WriteBatchRequest.newBuilder()
                                                                                                 .setRepoId( "whatever" )
                                                                                                 .build() ) );
        assertEquals( Status.Code.UNAUTHENTICATED, thrown.getStatus().getCode() );
    }

    // ---- 2. invalid / expired / wrong-audience tokens -----------------------------------

    @Test
    void garbageTokenFailsUnauthenticated()
    {
        StatusRuntimeException thrown = assertThrows( StatusRuntimeException.class,
                                                        () -> nodeStore( "not-a-jwt-at-all" ).writeBatch( WriteBatchRequest.newBuilder()
                                                                                                               .setRepoId( "whatever" )
                                                                                                               .build() ) );
        assertEquals( Status.Code.UNAUTHENTICATED, thrown.getStatus().getCode() );
    }

    @Test
    void expiredTokenFailsUnauthenticated()
    {
        String expired = JwtIssuer.mint( (RSAPrivateKey) issuerKeyPair.getPrivate(), (RSAPublicKey) issuerKeyPair.getPublic(), "acme",
                                          Scope.RUNTIME, "test-subject", Duration.ofSeconds( -30 ) );
        StatusRuntimeException thrown = assertThrows( StatusRuntimeException.class,
                                                        () -> nodeStore( expired ).writeBatch( WriteBatchRequest.newBuilder()
                                                                                                    .setRepoId( "whatever" )
                                                                                                    .build() ) );
        assertEquals( Status.Code.UNAUTHENTICATED, thrown.getStatus().getCode() );
    }

    @Test
    void wrongAudienceTokenFailsUnauthenticated()
    {
        Algorithm algorithm =
            Algorithm.RSA256( (RSAPublicKey) issuerKeyPair.getPublic(), (RSAPrivateKey) issuerKeyPair.getPrivate() );
        String wrongAudience = JWT.create()
            .withAudience( "not-nodb" )
            .withClaim( "tenant", "acme" )
            .withClaim( "scope", "runtime" )
            .withExpiresAt( java.util.Date.from( java.time.Instant.now().plusSeconds( 300 ) ) )
            .sign( algorithm );

        StatusRuntimeException thrown = assertThrows( StatusRuntimeException.class,
                                                        () -> nodeStore( wrongAudience ).writeBatch( WriteBatchRequest.newBuilder()
                                                                                                          .setRepoId( "whatever" )
                                                                                                          .build() ) );
        assertEquals( Status.Code.UNAUTHENTICATED, thrown.getStatus().getCode() );
    }

    // ---- 3. tenant round-trip -------------------------------------------------------------

    @Test
    void acmeWriteBatchAndGetBranchEntryRoundTripLandsOnlyInAcmeSchema()
        throws SQLException
    {
        String repoId = createRepo( "acme" );
        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );

        byte[] dataBytes = randomBytes();
        byte[] indexBytes = randomBytes();
        byte[] aclBytes = randomBytes();
        String dataHash = sha256Key( dataBytes );
        String indexHash = sha256Key( indexBytes );
        String aclHash = sha256Key( aclBytes );

        String nodeId = UUID.randomUUID().toString();
        String versionId = UUID.randomUUID().toString();
        long nowMillis = System.currentTimeMillis();

        Version version = Version.newBuilder()
            .setVersionId( versionId )
            .setNodeId( nodeId )
            .setNodePath( "/from-acme" )
            .setTimestampMillis( nowMillis )
            .setNodeDataHash( dataHash )
            .setIndexConfigHash( indexHash )
            .setAclHash( aclHash )
            .build();
        BranchEntry branchEntry = BranchEntry.newBuilder()
            .setBranch( "master" )
            .setNodeId( nodeId )
            .setVersionId( versionId )
            .setNodePath( "/from-acme" )
            .setTimestampMillis( nowMillis )
            .build();

        WriteBatchRequest request = WriteBatchRequest.newBuilder()
            .setRepoId( repoId )
            .addPayloads( PayloadRef.newBuilder().setInline( com.google.protobuf.ByteString.copyFrom( dataBytes ) ) )
            .addPayloads( PayloadRef.newBuilder().setInline( com.google.protobuf.ByteString.copyFrom( indexBytes ) ) )
            .addPayloads( PayloadRef.newBuilder().setInline( com.google.protobuf.ByteString.copyFrom( aclBytes ) ) )
            .addVersions( version )
            .addBranchEntries( branchEntry )
            .build();

        WriteBatchResponse response = acme.writeBatch( request );
        assertEquals( 0, response.getNeedPayloadCount() );
        assertTrue( response.getOutboxSeq() > 0 );

        BranchEntry fetched = acme.getBranchEntry( GetBranchEntryRequest.newBuilder()
                                                         .setRepoId( repoId )
                                                         .setBranch( "master" )
                                                         .setNodeId( nodeId )
                                                         .build() );
        assertEquals( "/from-acme", fetched.getNodePath() );
        assertEquals( versionId, fetched.getVersionId() );

        // GetVersion, PutPayload/GetPayload round-trip on the same fixture.
        Version fetchedVersion = acme.getVersion( GetVersionRequest.newBuilder().setVersionId( versionId ).build() );
        assertEquals( dataHash, fetchedVersion.getNodeDataHash() );

        Payload fetchedPayload = acme.getPayload( GetPayloadRequest.newBuilder().setHash( dataHash ).build() );
        assertEquals( dataHash, fetchedPayload.getHash() );
        assertEquals( dataBytes.length, fetchedPayload.getBytes().size() );

        byte[] newContent = randomBytes();
        PutPayloadResponse putResponse = acme.putPayload( PutPayloadRequest.newBuilder()
                                                                .setBytes( com.google.protobuf.ByteString.copyFrom( newContent ) )
                                                                .build() );
        assertEquals( sha256Key( newContent ), putResponse.getHash() );

        // SQL-level: the write landed in acme's schema (and only there).
        assertEquals( 1, countInSchema( "acme", "branch_entry", "node_id = '" + nodeId + "'" ) );
        assertEquals( 0, countInSchema( "fisk", "branch_entry", "node_id = '" + nodeId + "'" ) );
    }

    // ---- 4. cross-tenant isolation --------------------------------------------------------

    @Test
    void crossTenantIsolationHoldsAtRpcAndSqlLevel()
        throws SQLException
    {
        String acmeRepoId = createRepo( "acme" );
        String fiskRepoId = createRepo( "fisk" ); // deliberately a DIFFERENT repo id — fisk has no repo named acmeRepoId at all

        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );
        NodeStoreGrpc.NodeStoreBlockingStub fisk = nodeStore( token( "fisk", Scope.RUNTIME ) );

        byte[] dataBytes = randomBytes();
        byte[] indexBytes = randomBytes();
        byte[] aclBytes = randomBytes();
        String nodeId = UUID.randomUUID().toString();
        String versionId = UUID.randomUUID().toString();
        long nowMillis = System.currentTimeMillis();

        Version version = Version.newBuilder()
            .setVersionId( versionId )
            .setNodeId( nodeId )
            .setNodePath( "/secret-acme-node" )
            .setTimestampMillis( nowMillis )
            .setNodeDataHash( sha256Key( dataBytes ) )
            .setIndexConfigHash( sha256Key( indexBytes ) )
            .setAclHash( sha256Key( aclBytes ) )
            .build();
        BranchEntry branchEntry = BranchEntry.newBuilder()
            .setBranch( "master" )
            .setNodeId( nodeId )
            .setVersionId( versionId )
            .setNodePath( "/secret-acme-node" )
            .setTimestampMillis( nowMillis )
            .build();
        acme.writeBatch( WriteBatchRequest.newBuilder()
                              .setRepoId( acmeRepoId )
                              .addPayloads( PayloadRef.newBuilder().setInline( com.google.protobuf.ByteString.copyFrom( dataBytes ) ) )
                              .addPayloads( PayloadRef.newBuilder().setInline( com.google.protobuf.ByteString.copyFrom( indexBytes ) ) )
                              .addPayloads( PayloadRef.newBuilder().setInline( com.google.protobuf.ByteString.copyFrom( aclBytes ) ) )
                              .addVersions( version )
                              .addBranchEntries( branchEntry )
                              .build() );

        // A fisk token addressing acme's own repo id: there is no such repo in fisk's schema
        // (repo ids are resolved within the caller's own tenant schema only) -> NOT_FOUND,
        // never a peek at acme's data.
        StatusRuntimeException getChildrenOnAcmeRepoId = assertThrows( StatusRuntimeException.class,
                                                                        () -> fisk.getChildren( GetChildrenRequest.newBuilder()
                                                                                                    .setRepoId( acmeRepoId )
                                                                                                    .setBranch( "master" )
                                                                                                    .setParentPath( "/" )
                                                                                                    .build() ).hasNext() );
        assertEquals( Status.Code.NOT_FOUND, getChildrenOnAcmeRepoId.getStatus().getCode() );

        // fisk's own (separate, empty) repo never shows acme's node under any addressing.
        Iterator<BranchEntry> fiskChildren = fisk.getChildren( GetChildrenRequest.newBuilder()
                                                                    .setRepoId( fiskRepoId )
                                                                    .setBranch( "master" )
                                                                    .setParentPath( "/" )
                                                                    .build() );
        assertTrue( !fiskChildren.hasNext(), "fisk's repo must not contain acme's node" );

        // Even a "crafted" request naming acme's exact node_id inside fisk's own repo finds nothing:
        // fisk's tenant role can only ever see rows in the fisk schema (SET LOCAL ROLE), so
        // there is no code path by which a fisk token can retrieve this node.
        StatusRuntimeException craftedLookup = assertThrows( StatusRuntimeException.class,
                                                              () -> fisk.getBranchEntry( GetBranchEntryRequest.newBuilder()
                                                                                              .setRepoId( fiskRepoId )
                                                                                              .setBranch( "master" )
                                                                                              .setNodeId( nodeId )
                                                                                              .build() ) );
        assertEquals( Status.Code.NOT_FOUND, craftedLookup.getStatus().getCode() );

        // SQL-level ground truth: the row physically exists only in acme's schema.
        assertEquals( 1, countInSchema( "acme", "branch_entry", "node_id = '" + nodeId + "'" ) );
        assertEquals( 0, countInSchema( "fisk", "branch_entry", "node_id = '" + nodeId + "'" ) );
    }

    // ---- 5. scope enforcement -------------------------------------------------------------

    @Test
    void repositoryLifecycleIsRuntimeScoped()
    {
        // Design correction (Phase 1 gate B): repo lifecycle within a tenant is an
        // ordinary RUNTIME operation in XP (content projects create repos from app
        // code) — intra-tenant authz is the runtime's job (two-layer model). Operator
        // scope guards TENANT-level ops (dumps/snapshots/bulk), none of which exist yet.
        String repoId = "repo-" + UUID.randomUUID();

        Ack ack = repositoryAdmin( token( "acme", Scope.RUNTIME ) ).createRepository( CreateRepositoryRequest.newBuilder()
                                                                                           .setRepoId( repoId )
                                                                                           .build() );
        assertNotNull( ack );

        // Operator scope remains equally valid on data/lifecycle RPCs.
        StatusRuntimeException alreadyExists = assertThrows( StatusRuntimeException.class,
                                                              () -> repositoryAdmin( token( "acme", Scope.OPERATOR ) ).createRepository(
                                                                  CreateRepositoryRequest.newBuilder().setRepoId( repoId ).build() ) );
        assertEquals( Status.Code.ALREADY_EXISTS, alreadyExists.getStatus().getCode() );

        repositoryAdmin( token( "acme", Scope.RUNTIME ) ).deleteRepository( com.enonic.nodb.proto.v1.DeleteRepositoryRequest.newBuilder()
                                                                                 .setRepoId( repoId )
                                                                                 .build() );
    }

    // ---- 6. Phase 1 Gate A: standalone per-op NodeStore RPCs -----------------------------

    @Test
    void storeBranchEntryStandaloneAutoVivifiesUnseenBranchAndIsTenantIsolated()
    {
        String acmeRepoId = createRepo( "acme" );
        String fiskRepoId = createRepo( "fisk" );
        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );
        NodeStoreGrpc.NodeStoreBlockingStub fisk = nodeStore( token( "fisk", Scope.RUNTIME ) );

        WrittenNode written = storeVersionOnly( acme, acmeRepoId, UUID.randomUUID().toString(), "/standalone-branch-entry" );
        BranchEntry entry = BranchEntry.newBuilder()
            .setBranch( "never-seen-branch" ) // no prior branch-create call: auto-vivification is under test
            .setNodeId( written.nodeId() )
            .setVersionId( written.versionId() )
            .setNodePath( "/standalone-branch-entry" )
            .setTimestampMillis( System.currentTimeMillis() )
            .build();

        Ack ack = acme.storeBranchEntry( StoreBranchEntryRequest.newBuilder().setRepoId( acmeRepoId ).setEntry( entry ).build() );
        assertNotNull( ack );

        BranchEntry fetched = acme.getBranchEntry( GetBranchEntryRequest.newBuilder()
                                                         .setRepoId( acmeRepoId )
                                                         .setBranch( "never-seen-branch" )
                                                         .setNodeId( written.nodeId() )
                                                         .build() );
        assertEquals( "/standalone-branch-entry", fetched.getNodePath() );

        // cross-tenant: fisk's own (separate) repo never sees it.
        StatusRuntimeException fiskLookup = assertThrows( StatusRuntimeException.class,
                                                            () -> fisk.getBranchEntry( GetBranchEntryRequest.newBuilder()
                                                                                            .setRepoId( fiskRepoId )
                                                                                            .setBranch( "never-seen-branch" )
                                                                                            .setNodeId( written.nodeId() )
                                                                                            .build() ) );
        assertEquals( Status.Code.NOT_FOUND, fiskLookup.getStatus().getCode() );

        // NOT_FOUND for an unknown repo id.
        StatusRuntimeException unknownRepo = assertThrows( StatusRuntimeException.class,
                                                             () -> acme.storeBranchEntry( StoreBranchEntryRequest.newBuilder()
                                                                                               .setRepoId(
                                                                                                   "no-such-repo-" + UUID.randomUUID() )
                                                                                               .setEntry( entry )
                                                                                               .build() ) );
        assertEquals( Status.Code.NOT_FOUND, unknownRepo.getStatus().getCode() );
    }

    @Test
    void deleteBranchEntriesStandaloneRemovesEntryAndIsTenantIsolated()
    {
        String acmeRepoId = createRepo( "acme" );
        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );
        WrittenNode node = writeNode( acme, acmeRepoId, "master", "/to-delete-standalone" );

        acme.deleteBranchEntries(
            DeleteBranchEntriesRequest.newBuilder().setRepoId( acmeRepoId ).setBranch( "master" ).addNodeIds( node.nodeId() ).build() );

        StatusRuntimeException notFound = assertThrows( StatusRuntimeException.class,
                                                           () -> acme.getBranchEntry( GetBranchEntryRequest.newBuilder()
                                                                                           .setRepoId( acmeRepoId )
                                                                                           .setBranch( "master" )
                                                                                           .setNodeId( node.nodeId() )
                                                                                           .build() ) );
        assertEquals( Status.Code.NOT_FOUND, notFound.getStatus().getCode() );

        // deleting an already-gone id is a no-op, not an error.
        acme.deleteBranchEntries(
            DeleteBranchEntriesRequest.newBuilder().setRepoId( acmeRepoId ).setBranch( "master" ).addNodeIds( node.nodeId() ).build() );

        // NOT_FOUND for an unknown repo id.
        StatusRuntimeException unknownRepo = assertThrows( StatusRuntimeException.class,
                                                             () -> acme.deleteBranchEntries( DeleteBranchEntriesRequest.newBuilder()
                                                                                                  .setRepoId( "no-such-repo-" +
                                                                                                                  UUID.randomUUID() )
                                                                                                  .setBranch( "master" )
                                                                                                  .addNodeIds( node.nodeId() )
                                                                                                  .build() ) );
        assertEquals( Status.Code.NOT_FOUND, unknownRepo.getStatus().getCode() );
    }

    @Test
    void existsBranchEntryIsTrueAfterWriteFalseOtherwiseAndTenantIsolated()
    {
        String acmeRepoId = createRepo( "acme" );
        String fiskRepoId = createRepo( "fisk" );
        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );
        NodeStoreGrpc.NodeStoreBlockingStub fisk = nodeStore( token( "fisk", Scope.RUNTIME ) );

        WrittenNode node = writeNode( acme, acmeRepoId, "master", "/exists-check" );

        boolean exists = acme.existsBranchEntry( ExistsBranchEntryRequest.newBuilder()
                                                      .setRepoId( acmeRepoId )
                                                      .setBranch( "master" )
                                                      .setNodeId( node.nodeId() )
                                                      .build() ).getExists();
        assertTrue( exists );

        boolean missing = acme.existsBranchEntry( ExistsBranchEntryRequest.newBuilder()
                                                       .setRepoId( acmeRepoId )
                                                       .setBranch( "master" )
                                                       .setNodeId( UUID.randomUUID().toString() )
                                                       .build() ).getExists();
        assertFalse( missing );

        // cross-tenant: fisk's own repo never has acme's node_id.
        boolean fiskSees = fisk.existsBranchEntry( ExistsBranchEntryRequest.newBuilder()
                                                        .setRepoId( fiskRepoId )
                                                        .setBranch( "master" )
                                                        .setNodeId( node.nodeId() )
                                                        .build() ).getExists();
        assertFalse( fiskSees );

        // NOT_FOUND for an unknown repo id.
        StatusRuntimeException unknownRepo = assertThrows( StatusRuntimeException.class,
                                                             () -> acme.existsBranchEntry( ExistsBranchEntryRequest.newBuilder()
                                                                                                .setRepoId( "no-such-repo-" +
                                                                                                                UUID.randomUUID() )
                                                                                                .setBranch( "master" )
                                                                                                .setNodeId( node.nodeId() )
                                                                                                .build() ) );
        assertEquals( Status.Code.NOT_FOUND, unknownRepo.getStatus().getCode() );
    }

    @Test
    void getBranchEntriesMultiGetReturnsOnlyFoundAndIsTenantIsolated()
    {
        String acmeRepoId = createRepo( "acme" );
        String fiskRepoId = createRepo( "fisk" );
        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );
        NodeStoreGrpc.NodeStoreBlockingStub fisk = nodeStore( token( "fisk", Scope.RUNTIME ) );

        WrittenNode node1 = writeNode( acme, acmeRepoId, "master", "/mg1" );
        WrittenNode node2 = writeNode( acme, acmeRepoId, "master", "/mg2" );
        String missingNodeId = UUID.randomUUID().toString();

        Iterator<BranchEntry> found = acme.getBranchEntries( GetBranchEntriesRequest.newBuilder()
                                                                  .setRepoId( acmeRepoId )
                                                                  .setBranch( "master" )
                                                                  .addNodeIds( node1.nodeId() )
                                                                  .addNodeIds( node2.nodeId() )
                                                                  .addNodeIds( missingNodeId )
                                                                  .build() );
        Set<String> foundNodeIds = new HashSet<>();
        found.forEachRemaining( e -> foundNodeIds.add( e.getNodeId() ) );
        assertEquals( Set.of( node1.nodeId(), node2.nodeId() ), foundNodeIds, "the missing id must simply be absent, not an error" );

        // cross-tenant: fisk's own repo has neither.
        Iterator<BranchEntry> fiskFound = fisk.getBranchEntries( GetBranchEntriesRequest.newBuilder()
                                                                      .setRepoId( fiskRepoId )
                                                                      .setBranch( "master" )
                                                                      .addNodeIds( node1.nodeId() )
                                                                      .addNodeIds( node2.nodeId() )
                                                                      .build() );
        assertFalse( fiskFound.hasNext() );

        // NOT_FOUND for an unknown repo id.
        StatusRuntimeException unknownRepo = assertThrows( StatusRuntimeException.class,
                                                             () -> acme.getBranchEntries( GetBranchEntriesRequest.newBuilder()
                                                                                               .setRepoId( "no-such-repo-" +
                                                                                                               UUID.randomUUID() )
                                                                                               .setBranch( "master" )
                                                                                               .addNodeIds( node1.nodeId() )
                                                                                               .build() ).hasNext() );
        assertEquals( Status.Code.NOT_FOUND, unknownRepo.getStatus().getCode() );
    }

    @Test
    void getBranchesWithNodeReturnsAllBranchesAndIsTenantIsolated()
    {
        String acmeRepoId = createRepo( "acme" );
        String fiskRepoId = createRepo( "fisk" );
        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );
        NodeStoreGrpc.NodeStoreBlockingStub fisk = nodeStore( token( "fisk", Scope.RUNTIME ) );

        WrittenNode node = writeNode( acme, acmeRepoId, "master", "/bwn" );
        // Same node_id/version_id copied into a second, never-explicitly-created branch —
        // exercises StoreBranchEntry's auto-vivification again, incidentally.
        BranchEntry secondBranchEntry = BranchEntry.newBuilder()
            .setBranch( "second" )
            .setNodeId( node.nodeId() )
            .setVersionId( node.versionId() )
            .setNodePath( "/bwn" )
            .setTimestampMillis( System.currentTimeMillis() )
            .build();
        acme.storeBranchEntry( StoreBranchEntryRequest.newBuilder().setRepoId( acmeRepoId ).setEntry( secondBranchEntry ).build() );

        Iterator<BranchRef> branches = acme.getBranchesWithNode(
            GetBranchesWithNodeRequest.newBuilder().setRepoId( acmeRepoId ).setNodeId( node.nodeId() ).build() );
        Set<String> branchNames = new HashSet<>();
        branches.forEachRemaining( b -> branchNames.add( b.getBranch() ) );
        assertEquals( Set.of( "master", "second" ), branchNames );

        // cross-tenant: fisk's own repo has no branches at all for acme's node_id.
        Iterator<BranchRef> fiskBranches = fisk.getBranchesWithNode(
            GetBranchesWithNodeRequest.newBuilder().setRepoId( fiskRepoId ).setNodeId( node.nodeId() ).build() );
        assertFalse( fiskBranches.hasNext() );

        // NOT_FOUND for an unknown repo id.
        StatusRuntimeException unknownRepo = assertThrows( StatusRuntimeException.class,
                                                             () -> acme.getBranchesWithNode( GetBranchesWithNodeRequest.newBuilder()
                                                                                                  .setRepoId( "no-such-repo-" +
                                                                                                                  UUID.randomUUID() )
                                                                                                  .setNodeId( node.nodeId() )
                                                                                                  .build() ).hasNext() );
        assertEquals( Status.Code.NOT_FOUND, unknownRepo.getStatus().getCode() );
    }

    @Test
    void storeVersionStandaloneRoundTripsAndIsTenantIsolated()
    {
        String acmeRepoId = createRepo( "acme" );
        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );
        NodeStoreGrpc.NodeStoreBlockingStub fisk = nodeStore( token( "fisk", Scope.RUNTIME ) );

        WrittenNode written = storeVersionOnly( acme, acmeRepoId, UUID.randomUUID().toString(), "/standalone-version" );

        Version fetched = acme.getVersion( GetVersionRequest.newBuilder().setVersionId( written.versionId() ).build() );
        assertEquals( "/standalone-version", fetched.getNodePath() );

        // cross-tenant: this exact version id was never written under fisk.
        StatusRuntimeException fiskLookup = assertThrows( StatusRuntimeException.class,
                                                            () -> fisk.getVersion(
                                                                GetVersionRequest.newBuilder().setVersionId( written.versionId() ).build() ) );
        assertEquals( Status.Code.NOT_FOUND, fiskLookup.getStatus().getCode() );

        // NOT_FOUND for an unknown repo id on StoreVersion itself.
        byte[] moreBytes = randomBytes();
        Version anotherVersion = Version.newBuilder()
            .setVersionId( UUID.randomUUID().toString() )
            .setNodeId( UUID.randomUUID().toString() )
            .setNodePath( "/x" )
            .setTimestampMillis( System.currentTimeMillis() )
            .setNodeDataHash( sha256Key( moreBytes ) )
            .setIndexConfigHash( sha256Key( moreBytes ) )
            .setAclHash( sha256Key( moreBytes ) )
            .build();
        StatusRuntimeException unknownRepo = assertThrows( StatusRuntimeException.class,
                                                             () -> acme.storeVersion( StoreVersionRequest.newBuilder()
                                                                                          .setRepoId(
                                                                                              "no-such-repo-" + UUID.randomUUID() )
                                                                                          .setVersion( anotherVersion )
                                                                                          .build() ) );
        assertEquals( Status.Code.NOT_FOUND, unknownRepo.getStatus().getCode() );
    }

    @Test
    void deleteVersionRemovesOnlyTheCallersRowAndIsTenantIsolated()
    {
        String acmeRepoId = createRepo( "acme" );
        String fiskRepoId = createRepo( "fisk" );
        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );
        NodeStoreGrpc.NodeStoreBlockingStub fisk = nodeStore( token( "fisk", Scope.RUNTIME ) );

        // Same version_id string under both tenants (PK is (repo_key, version_id), and
        // repo_key differs per-tenant-schema, so this is legal and proves schema isolation).
        String sharedVersionId = UUID.randomUUID().toString();
        storeVersionOnly( acme, acmeRepoId, sharedVersionId, "/dv-acme" );
        storeVersionOnly( fisk, fiskRepoId, sharedVersionId, "/dv-fisk" );

        acme.deleteVersion( DeleteVersionRequest.newBuilder().setVersionId( sharedVersionId ).build() );

        StatusRuntimeException goneForAcme = assertThrows( StatusRuntimeException.class,
                                                              () -> acme.getVersion(
                                                                  GetVersionRequest.newBuilder().setVersionId( sharedVersionId ).build() ) );
        assertEquals( Status.Code.NOT_FOUND, goneForAcme.getStatus().getCode() );

        Version stillThereForFisk = fisk.getVersion( GetVersionRequest.newBuilder().setVersionId( sharedVersionId ).build() );
        assertEquals( "/dv-fisk", stillThereForFisk.getNodePath(), "acme's delete must not remove fisk's row with the same version_id" );

        // deleting an already-gone id is a no-op, not an error.
        acme.deleteVersion( DeleteVersionRequest.newBuilder().setVersionId( sharedVersionId ).build() );
    }

    @Test
    void storeCommitStandaloneRoundTripsAndGetCommitIsTenantIsolatedAndNotFoundWhenMissing()
    {
        String acmeRepoId = createRepo( "acme" );
        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );
        NodeStoreGrpc.NodeStoreBlockingStub fisk = nodeStore( token( "fisk", Scope.RUNTIME ) );

        String commitId = UUID.randomUUID().toString();
        Commit commit = Commit.newBuilder()
            .setCommitId( commitId )
            .setMessage( "a message" )
            .setCommitter( "user:system:admin" )
            .setTimestampMillis( System.currentTimeMillis() )
            .build();

        acme.storeCommit( StoreCommitRequest.newBuilder().setRepoId( acmeRepoId ).setCommit( commit ).build() );

        Commit fetched = acme.getCommit( GetCommitRequest.newBuilder().setCommitId( commitId ).build() );
        assertEquals( "a message", fetched.getMessage() );
        assertEquals( "user:system:admin", fetched.getCommitter() );

        // cross-tenant: fisk never wrote this commit id.
        StatusRuntimeException fiskLookup = assertThrows( StatusRuntimeException.class,
                                                            () -> fisk.getCommit(
                                                                GetCommitRequest.newBuilder().setCommitId( commitId ).build() ) );
        assertEquals( Status.Code.NOT_FOUND, fiskLookup.getStatus().getCode() );

        // NOT_FOUND for a commit id that was simply never written.
        StatusRuntimeException missing = assertThrows( StatusRuntimeException.class,
                                                          () -> acme.getCommit( GetCommitRequest.newBuilder()
                                                                                     .setCommitId( UUID.randomUUID().toString() )
                                                                                     .build() ) );
        assertEquals( Status.Code.NOT_FOUND, missing.getStatus().getCode() );

        // NOT_FOUND for an unknown repo id on StoreCommit itself.
        Commit anotherCommit = commit.toBuilder().setCommitId( UUID.randomUUID().toString() ).build();
        StatusRuntimeException unknownRepo = assertThrows( StatusRuntimeException.class,
                                                             () -> acme.storeCommit( StoreCommitRequest.newBuilder()
                                                                                          .setRepoId(
                                                                                              "no-such-repo-" + UUID.randomUUID() )
                                                                                          .setCommit( anotherCommit )
                                                                                          .build() ) );
        assertEquals( Status.Code.NOT_FOUND, unknownRepo.getStatus().getCode() );
    }

    // ---- 8. Phase 3 Gate A: node_version -> payload FK + batched GetPayloads -------------

    @Test
    void storeVersionWithUnknownPayloadHashFailsPreconditionAndPersistsNothing()
    {
        String acmeRepoId = createRepo( "acme" );
        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );

        // Well-formed hash shape, never stored via PutPayload -- exactly the scenario the
        // re-added node_version -> payload FK (BUILD-PHASE-3.md #10b) must reject.
        String missingHash = "sha256:" + "1".repeat( 64 );
        String versionId = UUID.randomUUID().toString();
        Version version = Version.newBuilder()
            .setVersionId( versionId )
            .setNodeId( UUID.randomUUID().toString() )
            .setNodePath( "/fk-violation" )
            .setTimestampMillis( System.currentTimeMillis() )
            .setNodeDataHash( missingHash )
            .setIndexConfigHash( missingHash )
            .setAclHash( missingHash )
            .build();

        StatusRuntimeException thrown = assertThrows( StatusRuntimeException.class,
                                                        () -> acme.storeVersion( StoreVersionRequest.newBuilder()
                                                                                     .setRepoId( acmeRepoId )
                                                                                     .setVersion( version )
                                                                                     .build() ) );
        assertEquals( Status.Code.FAILED_PRECONDITION, thrown.getStatus().getCode() );

        // The rejected write persisted nothing -- not even a dangling version row.
        StatusRuntimeException notFound = assertThrows( StatusRuntimeException.class,
                                                          () -> acme.getVersion(
                                                              GetVersionRequest.newBuilder().setVersionId( versionId ).build() ) );
        assertEquals( Status.Code.NOT_FOUND, notFound.getStatus().getCode() );
    }

    @Test
    void writeBatchWithUnknownHashOnlyPayloadRefNeedsPayloadEvenWithFkOn()
        throws SQLException
    {
        String acmeRepoId = createRepo( "acme" );
        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );

        byte[] dataBytes = randomBytes();
        String dataHash = sha256Key( dataBytes );
        String missingHash = "sha256:" + "4".repeat( 64 );
        String nodeId = UUID.randomUUID().toString();
        String versionId = UUID.randomUUID().toString();
        long nowMillis = System.currentTimeMillis();

        Version version = Version.newBuilder()
            .setVersionId( versionId )
            .setNodeId( nodeId )
            .setNodePath( "/needpayload-fk-on" )
            .setTimestampMillis( nowMillis )
            .setNodeDataHash( dataHash )
            .setIndexConfigHash( missingHash )
            .setAclHash( missingHash )
            .build();
        BranchEntry entry = BranchEntry.newBuilder()
            .setBranch( "master" )
            .setNodeId( nodeId )
            .setVersionId( versionId )
            .setNodePath( "/needpayload-fk-on" )
            .setTimestampMillis( nowMillis )
            .build();

        WriteBatchResponse response = acme.writeBatch( WriteBatchRequest.newBuilder()
                                                            .setRepoId( acmeRepoId )
                                                            .addPayloads( PayloadRef.newBuilder()
                                                                              .setInline(
                                                                                  com.google.protobuf.ByteString.copyFrom( dataBytes ) ) )
                                                            .addPayloads( PayloadRef.newBuilder().setHash( missingHash ) )
                                                            .addVersions( version )
                                                            .addBranchEntries( entry )
                                                            .build() );

        assertEquals( List.of( missingHash ), response.getNeedPayloadList() );
        assertEquals( 0L, response.getOutboxSeq() );
        assertEquals( 0, countInSchema( "acme", "node_version", "version_id = '" + versionId + "'" ),
                      "nothing must persist when a hash-only ref is unknown, even though the FK is enabled" );
    }

    @Test
    void getPayloadsBatchedMultiHashReturnsFoundOmitsMissingAndIsTenantIsolated()
        throws SQLException
    {
        NodeStoreGrpc.NodeStoreBlockingStub acme = nodeStore( token( "acme", Scope.RUNTIME ) );
        NodeStoreGrpc.NodeStoreBlockingStub fisk = nodeStore( token( "fisk", Scope.RUNTIME ) );

        byte[] bytes1 = randomBytes();
        byte[] bytes2 = randomBytes();
        String hash1 = acme.putPayload( PutPayloadRequest.newBuilder()
                                             .setBytes( com.google.protobuf.ByteString.copyFrom( bytes1 ) )
                                             .build() ).getHash();
        String hash2 = acme.putPayload( PutPayloadRequest.newBuilder()
                                             .setBytes( com.google.protobuf.ByteString.copyFrom( bytes2 ) )
                                             .build() ).getHash();
        String missingHash = "sha256:" + "2".repeat( 64 );

        Iterator<Payload> found = acme.getPayloads(
            GetPayloadsRequest.newBuilder().addHashes( hash1 ).addHashes( hash2 ).addHashes( missingHash ).build() );
        java.util.Map<String, com.google.protobuf.ByteString> byHash = new java.util.HashMap<>();
        found.forEachRemaining( p -> byHash.put( p.getHash(), p.getBytes() ) );
        assertEquals( Set.of( hash1, hash2 ), byHash.keySet(), "the missing hash must simply be absent, not an error" );
        assertEquals( com.google.protobuf.ByteString.copyFrom( bytes1 ), byHash.get( hash1 ) );
        assertEquals( com.google.protobuf.ByteString.copyFrom( bytes2 ), byHash.get( hash2 ) );

        // cross-tenant: fisk never stored these hashes under its own schema.
        Iterator<Payload> fiskFound =
            fisk.getPayloads( GetPayloadsRequest.newBuilder().addHashes( hash1 ).addHashes( hash2 ).build() );
        assertFalse( fiskFound.hasNext(), "tenant fisk must not see acme's payloads even by exact hash" );

        // Empty request -> empty stream, not an error.
        Iterator<Payload> empty = acme.getPayloads( GetPayloadsRequest.newBuilder().build() );
        assertFalse( empty.hasNext() );
    }

    // ---- 7. Phase 1 Gate A: RepositoryExists + the ALREADY_EXISTS bug fix ----------------

    @Test
    void repositoryExistsReflectsLifecycleAndIsPerTenant()
    {
        String repoId = "repo-exists-" + UUID.randomUUID();

        boolean beforeCreate = repositoryAdmin( token( "acme", Scope.RUNTIME ) ).repositoryExists(
            RepositoryExistsRequest.newBuilder().setRepoId( repoId ).build() ).getExists();
        assertFalse( beforeCreate );

        repositoryAdmin( token( "acme", Scope.OPERATOR ) ).createRepository(
            CreateRepositoryRequest.newBuilder().setRepoId( repoId ).build() );

        boolean afterCreateForAcme = repositoryAdmin( token( "acme", Scope.RUNTIME ) ).repositoryExists(
            RepositoryExistsRequest.newBuilder().setRepoId( repoId ).build() ).getExists();
        assertTrue( afterCreateForAcme );

        // cross-tenant: fisk's own `repository` table has no row with this id.
        boolean sameIdForFisk = repositoryAdmin( token( "fisk", Scope.RUNTIME ) ).repositoryExists(
            RepositoryExistsRequest.newBuilder().setRepoId( repoId ).build() ).getExists();
        assertFalse( sameIdForFisk );

        repositoryAdmin( token( "acme", Scope.OPERATOR ) ).deleteRepository(
            DeleteRepositoryRequest.newBuilder().setRepoId( repoId ).build() );

        boolean afterDelete = repositoryAdmin( token( "acme", Scope.RUNTIME ) ).repositoryExists(
            RepositoryExistsRequest.newBuilder().setRepoId( repoId ).build() ).getExists();
        assertFalse( afterDelete );
    }

    @Test
    void createRepositoryTwiceFailsAlreadyExistsAndDeletingUnknownRepoFailsNotFound()
    {
        String repoId = "repo-already-exists-" + UUID.randomUUID();
        repositoryAdmin( token( "acme", Scope.OPERATOR ) ).createRepository(
            CreateRepositoryRequest.newBuilder().setRepoId( repoId ).build() );

        StatusRuntimeException alreadyExists = assertThrows( StatusRuntimeException.class,
                                                                () -> repositoryAdmin( token( "acme", Scope.OPERATOR ) ).createRepository(
                                                                    CreateRepositoryRequest.newBuilder().setRepoId( repoId ).build() ) );
        assertEquals( Status.Code.ALREADY_EXISTS, alreadyExists.getStatus().getCode() );

        // A different tenant creating a repo with the SAME id must succeed (separate schema)
        // — ALREADY_EXISTS is per-tenant, not a global uniqueness constraint.
        Ack fiskCreate = repositoryAdmin( token( "fisk", Scope.OPERATOR ) ).createRepository(
            CreateRepositoryRequest.newBuilder().setRepoId( repoId ).build() );
        assertNotNull( fiskCreate );
        repositoryAdmin( token( "fisk", Scope.OPERATOR ) ).deleteRepository(
            DeleteRepositoryRequest.newBuilder().setRepoId( repoId ).build() );

        StatusRuntimeException deleteUnknown = assertThrows( StatusRuntimeException.class,
                                                                () -> repositoryAdmin( token( "acme", Scope.OPERATOR ) ).deleteRepository(
                                                                    DeleteRepositoryRequest.newBuilder()
                                                                        .setRepoId( "no-such-repo-" + UUID.randomUUID() )
                                                                        .build() ) );
        assertEquals( Status.Code.NOT_FOUND, deleteUnknown.getStatus().getCode() );

        repositoryAdmin( token( "acme", Scope.OPERATOR ) ).deleteRepository(
            DeleteRepositoryRequest.newBuilder().setRepoId( repoId ).build() );
    }
}
