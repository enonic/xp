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
import java.util.Iterator;
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
import com.enonic.nodb.proto.v1.CreateRepositoryRequest;
import com.enonic.nodb.proto.v1.GetBranchEntryRequest;
import com.enonic.nodb.proto.v1.GetChildrenRequest;
import com.enonic.nodb.proto.v1.GetPayloadRequest;
import com.enonic.nodb.proto.v1.GetVersionRequest;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.Payload;
import com.enonic.nodb.proto.v1.PayloadRef;
import com.enonic.nodb.proto.v1.PutPayloadRequest;
import com.enonic.nodb.proto.v1.PutPayloadResponse;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;
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
    void createRepositoryRequiresOperatorScope()
    {
        String repoId = "repo-" + UUID.randomUUID();

        StatusRuntimeException deniedForRuntime = assertThrows( StatusRuntimeException.class,
                                                                  () -> repositoryAdmin( token( "acme", Scope.RUNTIME ) ).createRepository(
                                                                      CreateRepositoryRequest.newBuilder().setRepoId( repoId ).build() ) );
        assertEquals( Status.Code.PERMISSION_DENIED, deniedForRuntime.getStatus().getCode() );

        Ack ack = repositoryAdmin( token( "acme", Scope.OPERATOR ) ).createRepository( CreateRepositoryRequest.newBuilder()
                                                                                            .setRepoId( repoId )
                                                                                            .build() );
        assertNotNull( ack );

        StatusRuntimeException deniedDeleteForRuntime = assertThrows( StatusRuntimeException.class,
                                                                        () -> repositoryAdmin( token( "acme", Scope.RUNTIME ) ).deleteRepository(
                                                                            com.enonic.nodb.proto.v1.DeleteRepositoryRequest.newBuilder()
                                                                                .setRepoId( repoId )
                                                                                .build() ) );
        assertEquals( Status.Code.PERMISSION_DENIED, deniedDeleteForRuntime.getStatus().getCode() );

        repositoryAdmin( token( "acme", Scope.OPERATOR ) ).deleteRepository( com.enonic.nodb.proto.v1.DeleteRepositoryRequest.newBuilder()
                                                                                  .setRepoId( repoId )
                                                                                  .build() );
    }
}
