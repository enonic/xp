package com.enonic.nodb.server;

import java.nio.file.Files;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
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
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.AbstractStub;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.TenantProvisioner;
import com.enonic.nodb.engine.search.IndexFields;
import com.enonic.nodb.engine.search.Indexer;
import com.enonic.nodb.engine.search.OpenSearchClient;
import com.enonic.nodb.engine.search.OpenSearchConfig;
import com.enonic.nodb.engine.search.SearchIndexAdmin;
import com.enonic.nodb.engine.search.SearchIndexNames;
import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.AwaitRefreshRequest;
import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.CreateRepositoryRequest;
import com.enonic.nodb.proto.v1.DeleteBranchEntriesRequest;
import com.enonic.nodb.proto.v1.DeleteDocumentsRequest;
import com.enonic.nodb.proto.v1.IndexAck;
import com.enonic.nodb.proto.v1.IndexDoc;
import com.enonic.nodb.proto.v1.IndexDocumentsRequest;
import com.enonic.nodb.proto.v1.IndexField;
import com.enonic.nodb.proto.v1.IndexValue;
import com.enonic.nodb.proto.v1.NodeSearchGrpc;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.PayloadRef;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;
import com.enonic.nodb.proto.v1.StoreBranchEntryRequest;
import com.enonic.nodb.proto.v1.Version;
import com.enonic.nodb.proto.v1.WriteBatchRequest;
import com.enonic.nodb.server.auth.DevKeys;
import com.enonic.nodb.server.auth.JwtIssuer;
import com.enonic.nodb.server.auth.JwtVerifier;
import com.enonic.nodb.server.auth.Scope;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.service.NodeSearchService;
import com.enonic.nodb.server.service.NodeStoreService;
import com.enonic.nodb.server.service.RepositoryAdminService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The {@code NodeSearch} write path over gRPC: {@code IndexDocuments} → outbox → indexer →
 * {@code AwaitRefresh} → searchable, plus {@code DeleteDocuments}, plus risk 10a asserted at the
 * RPC boundary ({@code Ack.outbox_seq} on the per-op store/delete calls) and the per-repo index
 * created by {@code CreateRepository}.
 *
 * <p>The engine tests prove the mechanics; this class proves the WIRING — that the proto shape
 * carries a real XP-shaped document without losing types, that the seq a caller gets back is the
 * one {@code AwaitRefresh} accepts, and that a repo created over gRPC comes up with the ported
 * mappings rather than none.
 */
class NodeSearchServiceIntegrationTest
{
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    /** STOCK image (D8) — no analysis-icu anywhere in this stack. */
    private static final GenericContainer<?> OPENSEARCH =
        new GenericContainer<>( "opensearchproject/opensearch:3.7.0" ).withExposedPorts( 9200 )
            .withEnv( "discovery.type", "single-node" )
            .withEnv( "DISABLE_SECURITY_PLUGIN", "true" )
            .withEnv( "DISABLE_INSTALL_DEMO_CONFIG", "true" )
            .withEnv( "OPENSEARCH_JAVA_OPTS", "-Xms512m -Xmx512m" )
            .waitingFor( Wait.forHttp( "/_cluster/health?wait_for_status=yellow&timeout=30s" )
                             .forPort( 9200 )
                             .forStatusCode( 200 )
                             .withStartupTimeout( Duration.ofMinutes( 4 ) ) );

    private static HikariDataSource dataSource;

    private static KeyPair issuerKeyPair;

    private static Server grpcServer;

    private static ManagedChannel channel;

    private static OpenSearchClient openSearchClient;

    @BeforeAll
    static void setUp()
        throws Exception
    {
        POSTGRES.start();
        OPENSEARCH.start();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( POSTGRES.getJdbcUrl() );
        config.setUsername( POSTGRES.getUsername() );
        config.setPassword( POSTGRES.getPassword() );
        config.setMaximumPoolSize( 16 );
        dataSource = new HikariDataSource( config );

        new TenantProvisioner( dataSource, POSTGRES.getUsername() ).provision( new TenantContext( "acme" ) );

        // refresh_interval -1: a missing awaitRefresh must fail deterministically, not pass on a timer.
        OpenSearchConfig openSearchConfig =
            OpenSearchConfig.of( "http://" + OPENSEARCH.getHost() + ":" + OPENSEARCH.getMappedPort( 9200 ) ).withRefreshInterval( "-1" );
        openSearchClient = new OpenSearchClient( openSearchConfig );
        SearchIndexAdmin searchIndexAdmin = new SearchIndexAdmin( dataSource, openSearchClient );

        issuerKeyPair = DevKeys.loadOrGenerate( Files.createTempDirectory( "nodb-search-test-keys" ) );
        TenantAuthInterceptor authInterceptor =
            new TenantAuthInterceptor( new JwtVerifier( (RSAPublicKey) issuerKeyPair.getPublic() ) );

        String serverName = InProcessServerBuilder.generateName();
        grpcServer = InProcessServerBuilder.forName( serverName )
            .directExecutor()
            .addService( ServerInterceptors.intercept( new NodeStoreService( dataSource ), authInterceptor ) )
            .addService( ServerInterceptors.intercept( new RepositoryAdminService( dataSource, searchIndexAdmin ), authInterceptor ) )
            .addService( ServerInterceptors.intercept( new NodeSearchService( dataSource, tenant -> new Indexer( dataSource, tenant,
                                                                                                                openSearchClient,
                                                                                                                searchIndexAdmin ) ),
                                                        authInterceptor ) )
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
        if ( openSearchClient != null )
        {
            openSearchClient.close();
        }
        if ( dataSource != null )
        {
            dataSource.close();
        }
        OPENSEARCH.stop();
        POSTGRES.stop();
    }

    // ------------------------------------------------------------------------------------ tests

    /** {@code CreateRepository} now also creates the repo's index with the ported mappings. */
    @Test
    void createRepositoryCreatesThePerRepoIndexWithItsAlias()
    {
        String repoId = createRepo( "adminindex" );
        assertTrue( openSearchClient.aliasExists( SearchIndexNames.alias( new TenantContext( "acme" ), repoId ) ) );
        assertEquals( List.of( "acme-" + repoId + "+g1" ),
                      openSearchClient.indicesForAlias( SearchIndexNames.alias( new TenantContext( "acme" ), repoId ) ) );
    }

    /**
     * The full wire path. The document carries a string, an analyzed variant, a typed number and a
     * typed instant, so a lossy value encoding on the wire would show up as a failed range query
     * rather than as a silently coerced keyword.
     */
    @Test
    void indexDocumentsThenAwaitRefreshMakesTheDocumentSearchable()
    {
        String repoId = createRepo( "wirepath" );
        String alias = SearchIndexNames.alias( new TenantContext( "acme" ), repoId );

        IndexAck ack = nodeSearch().indexDocuments( IndexDocumentsRequest.newBuilder()
                                                        .setRepoId( repoId )
                                                        .setBranch( "master" )
                                                        .addDocuments( document( "node-1", "Hello Brave World" ) )
                                                        .build() );
        assertTrue( ack.getOutboxSeq() > 0, "the RPC must return the seq it committed" );

        assertEquals( 0, count( alias ), "nothing is searchable before the refresh barrier" );

        Ack refreshed = nodeSearch().awaitRefresh(
            AwaitRefreshRequest.newBuilder().setSeq( ack.getOutboxSeq() ).addRepoIds( repoId ).setTimeoutMillis( 30_000 ).build() );
        assertTrue( refreshed.getOutboxSeq() >= ack.getOutboxSeq() );

        assertEquals( 1, count( alias ) );
        assertEquals( 1, hits( alias, "data.title._text", "hello brave world" ) );
        assertEquals( 1, hits( alias, IndexFields.BRANCH, "master" ) );
        assertEquals( 1, hits( alias, IndexFields.REPO, repoId ) );
        // The projection's injected admin read key, over the wire.
        assertEquals( 1, hits( alias, "_permissions.read._text", IndexFields.ADMIN_PRINCIPAL ) );
        // Typed values survived: a keyword-coerced number could not answer a range query.
        assertEquals( 1, rangeHits( alias, "data.count._number", 41, 43 ) );
        assertEquals( 1, rangeHits( alias, "_ts._datetime", 1_600_000_000_000L, 1_800_000_000_000L ) );
    }

    @Test
    void deleteDocumentsRemovesThemThroughTheSameBarrier()
    {
        String repoId = createRepo( "wiredelete" );
        String alias = SearchIndexNames.alias( new TenantContext( "acme" ), repoId );

        IndexAck indexed = nodeSearch().indexDocuments( IndexDocumentsRequest.newBuilder()
                                                            .setRepoId( repoId )
                                                            .setBranch( "master" )
                                                            .addDocuments( document( "node-1", "doomed" ) )
                                                            .build() );
        nodeSearch().awaitRefresh( AwaitRefreshRequest.newBuilder().setSeq( indexed.getOutboxSeq() ).addRepoIds( repoId ).build() );
        assertEquals( 1, count( alias ) );

        IndexAck deleted = nodeSearch().deleteDocuments(
            DeleteDocumentsRequest.newBuilder().setRepoId( repoId ).setBranch( "master" ).addNodeIds( "node-1" ).build() );
        nodeSearch().awaitRefresh( AwaitRefreshRequest.newBuilder().setSeq( deleted.getOutboxSeq() ).addRepoIds( repoId ).build() );

        assertEquals( 0, count( alias ) );
    }

    /**
     * The readiness endpoint, including the search contribution (DESIGN §7.1: ready = PG reachable,
     * indexer running, OS reachable). An OpenSearch that responds while nothing drains the outbox
     * honours neither {@code refresh(SEARCH)} nor anything built on it, so "OpenSearch answers" on
     * its own would be a probe that passes with the contract broken.
     */
    @Test
    void readinessReportsPostgresAndTheSearchBackendTogether()
        throws Exception
    {
        try (HealthServer health = HealthServer.start( 0, dataSource, () -> openSearchClient.ping() ))
        {
            assertEquals( 200, httpStatus( health.getPort(), "/health/live" ) );
            assertEquals( 200, httpStatus( health.getPort(), "/health/ready" ) );
        }

        // The same probe with an unreachable search backend must report DOWN, not UP.
        try (HealthServer health = HealthServer.start( 0, dataSource, () -> false ))
        {
            assertEquals( 200, httpStatus( health.getPort(), "/health/live" ), "liveness is about the process, not its dependencies" );
            assertEquals( 503, httpStatus( health.getPort(), "/health/ready" ) );
        }
    }

    private static int httpStatus( int port, String path )
        throws Exception
    {
        try (java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient())
        {
            return httpClient.send(
                java.net.http.HttpRequest.newBuilder( java.net.URI.create( "http://localhost:" + port + path ) ).build(),
                java.net.http.HttpResponse.BodyHandlers.discarding() ).statusCode();
        }
    }

    /**
     * Risk 10a at the RPC boundary: the per-op store/delete RPCs report the outbox seq they
     * committed. Before this gate {@code Ack.outbox_seq} was documented as "only WriteBatch
     * populates this meaningfully" and these two returned 0 — i.e. the caller had no seq to await
     * and the node was invisible to search with no error.
     */
    @Test
    void perOpBranchEntryRpcsReportAnOutboxSeq()
    {
        String repoId = createRepo( "risk10arpc" );
        String versionId = "v-rpc";
        String nodeId = "node-rpc";

        byte[] data = ( "data-" + versionId ).getBytes();
        byte[] icfg = ( "icfg-" + versionId ).getBytes();
        byte[] acl = ( "acl-" + versionId ).getBytes();

        nodeStore().writeBatch( WriteBatchRequest.newBuilder()
                                    .setRepoId( repoId )
                                    .addPayloads( inline( data ) )
                                    .addPayloads( inline( icfg ) )
                                    .addPayloads( inline( acl ) )
                                    .addVersions( Version.newBuilder()
                                                      .setVersionId( versionId )
                                                      .setNodeId( nodeId )
                                                      .setNodePath( "/" + nodeId )
                                                      .setTimestampMillis( Instant.now().toEpochMilli() )
                                                      .setNodeDataHash( sha256( data ) )
                                                      .setIndexConfigHash( sha256( icfg ) )
                                                      .setAclHash( sha256( acl ) ) )
                                    .build() );

        Ack stored = nodeStore().storeBranchEntry( StoreBranchEntryRequest.newBuilder()
                                                       .setRepoId( repoId )
                                                       .setEntry( BranchEntry.newBuilder()
                                                                      .setBranch( "master" )
                                                                      .setNodeId( nodeId )
                                                                      .setVersionId( versionId )
                                                                      .setNodePath( "/" + nodeId )
                                                                      .setTimestampMillis( Instant.now().toEpochMilli() ) )
                                                       .build() );
        assertTrue( stored.getOutboxSeq() > 0, "StoreBranchEntry must report an outbox seq (risk 10a)" );

        Ack removed = nodeStore().deleteBranchEntries(
            DeleteBranchEntriesRequest.newBuilder().setRepoId( repoId ).setBranch( "master" ).addNodeIds( nodeId ).build() );
        assertTrue( removed.getOutboxSeq() > stored.getOutboxSeq(), "DeleteBranchEntries must report a LATER outbox seq (risk 10a)" );
    }

    // ---------------------------------------------------------------------------------- helpers

    private static IndexDoc document( String nodeId, String title )
    {
        return IndexDoc.newBuilder()
            .setId( nodeId )
            .addFields( textField( "data.title", title ) )
            .addFields( textField( "data.title._analyzed", title ) )
            .addFields( textField( "data.title._orderby", title.toLowerCase() ) )
            .addFields( textField( "data.title._orderby_no", "ærlig" ) )
            .addFields( textField( IndexFields.PERMISSIONS_READ, "role:system.everyone" ) )
            .addFields( IndexField.newBuilder()
                            .setName( "data.count._number" )
                            .addValues( IndexValue.newBuilder().setDoubleValue( 42 ) ) )
            .addFields( IndexField.newBuilder()
                            .setName( "_ts._datetime" )
                            .addValues( IndexValue.newBuilder().setInstantMillis( 1_700_000_000_000L ) ) )
            .build();
    }

    private static IndexField textField( String name, String value )
    {
        return IndexField.newBuilder().setName( name ).addValues( IndexValue.newBuilder().setStringValue( value ) ).build();
    }

    private static PayloadRef inline( byte[] bytes )
    {
        return PayloadRef.newBuilder().setInline( com.google.protobuf.ByteString.copyFrom( bytes ) ).build();
    }

    private static String sha256( byte[] bytes )
    {
        try
        {
            return "sha256:" + HexFormat.of().formatHex( MessageDigest.getInstance( "SHA-256" ).digest( bytes ) );
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( e );
        }
    }

    private static String createRepo( String prefix )
    {
        String repoId = prefix + "." + System.nanoTime();
        repositoryAdmin().createRepository( CreateRepositoryRequest.newBuilder().setRepoId( repoId ).build() );
        return repoId;
    }

    private static long count( String alias )
    {
        return openSearchClient.count( alias );
    }

    private static long hits( String alias, String field, String value )
    {
        ObjectNode body = OpenSearchClient.mapper().createObjectNode();
        body.put( "track_total_hits", true );
        body.putObject( "query" ).putObject( "term" ).put( field, value );
        return openSearchClient.search( alias, body ).path( "hits" ).path( "total" ).path( "value" ).asLong();
    }

    private static long rangeHits( String alias, String field, long from, long to )
    {
        ObjectNode body = OpenSearchClient.mapper().createObjectNode();
        body.put( "track_total_hits", true );
        body.putObject( "query" ).putObject( "range" ).putObject( field ).put( "gte", from ).put( "lte", to );
        return openSearchClient.search( alias, body ).path( "hits" ).path( "total" ).path( "value" ).asLong();
    }

    private static NodeSearchGrpc.NodeSearchBlockingStub nodeSearch()
    {
        return withAuth( NodeSearchGrpc.newBlockingStub( channel ) );
    }

    private static NodeStoreGrpc.NodeStoreBlockingStub nodeStore()
    {
        return withAuth( NodeStoreGrpc.newBlockingStub( channel ) );
    }

    private static RepositoryAdminGrpc.RepositoryAdminBlockingStub repositoryAdmin()
    {
        return withAuth( RepositoryAdminGrpc.newBlockingStub( channel ) );
    }

    private static <T extends AbstractStub<T>> T withAuth( T stub )
    {
        String bearerToken = JwtIssuer.mint( (RSAPrivateKey) issuerKeyPair.getPrivate(), (RSAPublicKey) issuerKeyPair.getPublic(), "acme",
                                              Scope.OPERATOR, "test-subject", Duration.ofMinutes( 30 ) );
        Metadata headers = new Metadata();
        headers.put( Metadata.Key.of( "authorization", Metadata.ASCII_STRING_MARSHALLER ), "Bearer " + bearerToken );
        return stub.withInterceptors( new ClientInterceptor()
        {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall( MethodDescriptor<ReqT, RespT> method, CallOptions callOptions,
                                                                          Channel next )
            {
                return new ForwardingClientCall.SimpleForwardingClientCall<>( next.newCall( method, callOptions ) )
                {
                    @Override
                    public void start( Listener<RespT> responseListener, Metadata metadata )
                    {
                        metadata.merge( headers );
                        super.start( responseListener, metadata );
                    }
                };
            }
        } );
    }
}
