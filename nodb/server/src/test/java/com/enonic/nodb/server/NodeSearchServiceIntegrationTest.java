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

import com.fasterxml.jackson.databind.JsonNode;
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
import io.grpc.Status;
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
import com.enonic.nodb.engine.search.SearchQueryExecutor;
import com.enonic.nodb.engine.search.SearchIndexNames;
import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.AwaitRefreshRequest;
import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.CreateRepositoryRequest;
import com.enonic.nodb.proto.v1.DeleteBranchEntriesRequest;
import com.enonic.nodb.proto.v1.CreateSearchIndexRequest;
import com.enonic.nodb.proto.v1.DeleteDocumentsRequest;
import com.enonic.nodb.proto.v1.DeleteSearchIndexRequest;
import com.enonic.nodb.proto.v1.IndexAck;
import com.enonic.nodb.proto.v1.IndexDoc;
import com.enonic.nodb.proto.v1.IndexDocumentsRequest;
import com.enonic.nodb.proto.v1.IndexField;
import com.enonic.nodb.proto.v1.IndexValue;
import com.enonic.nodb.proto.v1.NodeSearchGrpc;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.PayloadRef;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;
import com.enonic.nodb.proto.v1.SearchHit;
import com.enonic.nodb.proto.v1.SearchResult;
import com.enonic.nodb.proto.v1.SearchSourceRef;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
                                                                                                                searchIndexAdmin ),
                                                                             new SearchQueryExecutor( openSearchClient ),
                                                                             searchIndexAdmin ),
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
     * Phase 4 Gate F: purge = {@code DeleteSearchIndex} then {@code CreateSearchIndex}, which is what
     * XP's {@code IndexService.reindex(initialize = true)} issues. Asserts the three things that make
     * it a purge rather than a no-op: the documents are gone from OpenSearch, the STORED documents
     * are gone from {@code search_document} (leaving them would let a rebuild-from-documents
     * resurrect exactly what the purge removed), and a live alias exists again afterwards so the
     * reindex has somewhere to write.
     */
    @Test
    void purgingTheSearchIndexDropsTheDocumentsAndLeavesAFreshAlias()
        throws Exception
    {
        String repoId = createRepo( "wirepurge" );
        String alias = SearchIndexNames.alias( new TenantContext( "acme" ), repoId );

        IndexAck indexed = nodeSearch().indexDocuments( IndexDocumentsRequest.newBuilder()
                                                            .setRepoId( repoId )
                                                            .setBranch( "master" )
                                                            .addDocuments( document( "node-1", "stale" ) )
                                                            .build() );
        nodeSearch().awaitRefresh( AwaitRefreshRequest.newBuilder().setSeq( indexed.getOutboxSeq() ).addRepoIds( repoId ).build() );
        assertEquals( 1, count( alias ) );
        assertEquals( 1, storedDocumentCount( repoId ) );

        nodeSearch().deleteSearchIndex( DeleteSearchIndexRequest.newBuilder().setRepoId( repoId ).build() );
        assertFalse( openSearchClient.aliasExists( alias ), "the alias must be gone after the purge's delete half" );
        assertEquals( 0, storedDocumentCount( repoId ), "the stored documents must go with the index, or a rebuild resurrects them" );

        nodeSearch().createSearchIndex( CreateSearchIndexRequest.newBuilder().setRepoId( repoId ).build() );
        assertTrue( openSearchClient.aliasExists( alias ) );
        assertEquals( 0, count( alias ), "a purged index starts empty" );
    }

    /**
     * Idempotence is what lets ONE pair of RPCs serve both the repository lifecycle (where the index
     * already exists / is already gone, because the storage admin ran first) and the purge (where the
     * calls must really happen). Without it, every repository create would race on ALREADY_EXISTS and
     * every repository delete would fail on NOT_FOUND.
     */
    @Test
    void searchIndexCreateAndDeleteAreIdempotent()
    {
        String repoId = createRepo( "wireidem" );
        String alias = SearchIndexNames.alias( new TenantContext( "acme" ), repoId );

        // createRepo already made it; a second create is a no-op, not a failure.
        assertTrue( openSearchClient.aliasExists( alias ) );
        assertDoesNotThrow( () -> nodeSearch().createSearchIndex( CreateSearchIndexRequest.newBuilder().setRepoId( repoId ).build() ) );
        assertTrue( openSearchClient.aliasExists( alias ) );

        nodeSearch().deleteSearchIndex( DeleteSearchIndexRequest.newBuilder().setRepoId( repoId ).build() );
        assertDoesNotThrow( () -> nodeSearch().deleteSearchIndex( DeleteSearchIndexRequest.newBuilder().setRepoId( repoId ).build() ) );

        // A repository that never existed at all: still a successful delete, never NOT_FOUND.
        assertDoesNotThrow(
            () -> nodeSearch().deleteSearchIndex( DeleteSearchIndexRequest.newBuilder().setRepoId( "no-such-repo" ).build() ) );
    }

    private static int storedDocumentCount( String repoId )
        throws Exception
    {
        try (java.sql.Connection connection = dataSource.getConnection())
        {
            connection.createStatement().execute( "SET search_path TO \"acme\"" );
            try (java.sql.PreparedStatement statement = connection.prepareStatement(
                "SELECT count(*) FROM search_document sd JOIN repository r ON r.repo_key = sd.repo_key WHERE r.repo_id = ?" ))
            {
                statement.setString( 1, repoId );
                try (java.sql.ResultSet resultSet = statement.executeQuery())
                {
                    resultSet.next();
                    return resultSet.getInt( 1 );
                }
            }
        }
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

    // --- Search (Gate B) ---------------------------------------------------------------

    /**
     * The query path end to end over gRPC: canonical DSL in, hits with EXPLICIT repo/branch
     * attribution out. The term is sent in mixed case on purpose — the text variant is a
     * lowercase-normalized keyword, so a translator that forwards the value raw returns zero
     * hits with no error, which is the failure mode this whole port is built to avoid.
     */
    @Test
    void searchTranslatesTheCanonicalDslAndAttributesHitsExplicitly()
    {
        String repoId = createRepo( "querypath" );
        indexAndRefresh( repoId, document( "node-1", "Hello Brave World" ) );

        SearchResult result = nodeSearch().search( searchRequest( repoId )
                                                       .setQuery( "{\"term\":{\"field\":\"data.title\",\"value\":\"Hello Brave World\"}}" )
                                                       .addReturnFields( "data.title" )
                                                       .build() );

        assertEquals( 1, result.getTotalHits() );
        assertEquals( 1, result.getHitsCount() );

        SearchHit hit = result.getHits( 0 );
        assertEquals( "node-1", hit.getId(), "the composite <nodeId>@<branch> _id must not leak to callers" );
        assertEquals( repoId, hit.getRepoId() );
        assertEquals( "master", hit.getBranch() );
        assertEquals( 1, hit.getReturnValuesCount() );
        assertEquals( "data.title", hit.getReturnValues( 0 ).getName() );
        // _source keeps the value XP shipped; only the INDEXED keyword is lowercase-normalized,
        // which is why the mixed-case term above matched at all.
        assertEquals( "Hello Brave World", hit.getReturnValues( 0 ).getValues( 0 ).getStringValue() );
    }

    @Test
    void searchTranslatesNumericAndDateTypedPredicates()
    {
        String repoId = createRepo( "querytypes" );
        indexAndRefresh( repoId, document( "node-1", "typed" ) );

        assertEquals( 1, nodeSearch().search( searchRequest( repoId )
                                                  .setQuery( "{\"range\":{\"field\":\"data.count\",\"gte\":41.0,\"lte\":43.0}}" )
                                                  .build() ).getTotalHits() );

        assertEquals( 1, nodeSearch().search( searchRequest( repoId )
                                                  .setQuery( "{\"range\":{\"field\":\"_ts\",\"type\":\"dateTime\"," +
                                                                 "\"gt\":\"2020-01-01T00:00:00Z\"}}" )
                                                  .build() ).getTotalHits() );

        assertEquals( 0, nodeSearch().search( searchRequest( repoId )
                                                  .setQuery( "{\"range\":{\"field\":\"_ts\",\"type\":\"dateTime\"," +
                                                                 "\"lt\":\"2020-01-01T00:00:00Z\"}}" )
                                                  .build() ).getTotalHits() );
    }

    @Test
    void searchTranslatesBooleanNestingAndInAndExistsAndSort()
    {
        String repoId = createRepo( "querystructured" );
        indexAndRefresh( repoId, document( "node-1", "alpha" ) );
        indexAndRefresh( repoId, document( "node-2", "beta" ) );

        assertEquals( 2, nodeSearch().search( searchRequest( repoId )
                                                  .setQuery( "{\"in\":{\"field\":\"data.title\",\"values\":[\"alpha\",\"beta\"]}}" )
                                                  .build() ).getTotalHits() );

        assertEquals( 1, nodeSearch().search( searchRequest( repoId ).setQuery(
            "{\"boolean\":{\"must\":[{\"exists\":{\"field\":\"data.title\"}}," +
                "{\"boolean\":{\"mustNot\":{\"term\":{\"field\":\"data.title\",\"value\":\"beta\"}}}}]}}" ).build() )
                          .getTotalHits() );

        SearchResult sorted = nodeSearch().search( searchRequest( repoId )
                                                      .setQuery( "{\"matchAll\":{}}" )
                                                      .addSort( "{\"field\":\"data.title\",\"direction\":\"DESC\"}" )
                                                      .build() );
        assertEquals( List.of( "node-2", "node-1" ), sorted.getHitsList().stream().map( SearchHit::getId ).toList() );
        assertTrue( Float.isNaN( sorted.getMaxScore() ), "a field-sorted result reports a NaN max score, as the ES path did" );
    }

    /** GET_ALL: size -1 pages by batch_size rather than honouring the default page size. */
    @Test
    void searchWithSizeMinusOneReturnsEverything()
    {
        String repoId = createRepo( "queryall" );
        for ( int i = 0; i < 7; i++ )
        {
            indexAndRefresh( repoId, document( "node-" + i, "page" ) );
        }

        SearchResult result = nodeSearch().search(
            searchRequest( repoId ).setQuery( "{\"matchAll\":{}}" ).setSize( -1 ).setBatchSize( 2 ).build() );

        assertEquals( 7, result.getTotalHits() );
        assertEquals( 7, result.getHitsCount() );
    }

    /**
     * The ACL filter is applied unconditionally, including for admin. An unrelated principal set
     * therefore sees nothing — the ES path's "no filter at all for role:system.admin" shortcut has
     * no equivalent here, and neither does a match-all fallback for an unknown principal.
     */
    @Test
    void searchAlwaysAppliesTheAclFilter()
    {
        String repoId = createRepo( "queryacl" );
        indexAndRefresh( repoId, document( "node-1", "secret" ) );

        assertEquals( 0, nodeSearch().search( com.enonic.nodb.proto.v1.SearchRequest.newBuilder()
                                                  .setFormatVersion( 1 )
                                                  .addSources( SearchSourceRef.newBuilder()
                                                                   .setRepoId( repoId )
                                                                   .setBranch( "master" )
                                                                   .addPrincipals( "user:system:nobody" ) )
                                                  .setQuery( "{\"matchAll\":{}}" )
                                                  .setSize( 10 )
                                                  .build() ).getTotalHits() );

        assertEquals( 1, nodeSearch().search( searchRequest( repoId ).setQuery( "{\"matchAll\":{}}" ).build() ).getTotalHits(),
                      "the admin read key the projection injects is what makes an admin context see the document" );
    }

    /** A branch the document does not live in must not match: branch is a filter, not a hint. */
    @Test
    void searchIsScopedToTheRequestedBranch()
    {
        String repoId = createRepo( "querybranch" );
        indexAndRefresh( repoId, document( "node-1", "draftonly" ) );

        assertEquals( 0, nodeSearch().search( com.enonic.nodb.proto.v1.SearchRequest.newBuilder()
                                                  .setFormatVersion( 1 )
                                                  .addSources( SearchSourceRef.newBuilder()
                                                                   .setRepoId( repoId )
                                                                   .setBranch( "draft" )
                                                                   .addPrincipals( IndexFields.ADMIN_PRINCIPAL ) )
                                                  .setQuery( "{\"matchAll\":{}}" )
                                                  .setSize( 10 )
                                                  .build() ).getTotalHits() );
    }

    /** Multi-source: one request, several aliases, per-source ACL, attributed hits. */
    @Test
    void searchFansOutAcrossSourcesAndAttributesEachHit()
    {
        String first = createRepo( "querymultia" );
        String second = createRepo( "querymultib" );
        indexAndRefresh( first, document( "node-a", "shared" ) );
        indexAndRefresh( second, document( "node-b", "shared" ) );

        SearchResult result = nodeSearch().search( com.enonic.nodb.proto.v1.SearchRequest.newBuilder()
                                                      .setFormatVersion( 1 )
                                                      .addSources( SearchSourceRef.newBuilder()
                                                                       .setRepoId( first )
                                                                       .setBranch( "master" )
                                                                       .addPrincipals( IndexFields.ADMIN_PRINCIPAL ) )
                                                      .addSources( SearchSourceRef.newBuilder()
                                                                       .setRepoId( second )
                                                                       .setBranch( "master" )
                                                                       .addPrincipals( IndexFields.ADMIN_PRINCIPAL ) )
                                                      .setQuery( "{\"term\":{\"field\":\"data.title\",\"value\":\"shared\"}}" )
                                                      .setSize( 10 )
                                                      .build() );

        assertEquals( 2, result.getTotalHits() );
        assertEquals( List.of( first, second ),
                      result.getHitsList().stream().map( SearchHit::getRepoId ).sorted().toList() );
    }

    @Test
    void searchRejectsAnUnknownEnvelopeVersionAndUntranslatedConstructs()
    {
        String repoId = createRepo( "queryreject" );

        assertEquals( Status.Code.INVALID_ARGUMENT, statusOf( () -> nodeSearch().search(
            searchRequest( repoId ).setFormatVersion( 99 ).setQuery( "{\"matchAll\":{}}" ).build() ) ) );

        assertEquals( Status.Code.INVALID_ARGUMENT, statusOf( () -> nodeSearch().search(
                          searchRequest( repoId ).setQuery( "{\"somethingNew\":{\"field\":\"data.title\"}}" ).build() ) ),
                      "an untranslated construct must fail loudly, never return plausible wrong hits" );

        // Gate E moved this fence DOWN rather than removing it: aggregations are translated now, so
        // what must still fail loudly is an aggregation naming a type NoDB does not implement, an
        // aggregation naming no type at all, and a sub-aggregation under a METRIC aggregation --
        // which is where ES 2.4's vanished AbstractAggregationBuilder guard silently dropped it.
        assertEquals( Status.Code.INVALID_ARGUMENT, statusOf( () -> nodeSearch().search(
            searchRequest( repoId ).setQuery( "{\"matchAll\":{}}" ).setAggregations( "{\"x\":{}}" ).build() ) ) );
        assertEquals( Status.Code.INVALID_ARGUMENT, statusOf( () -> nodeSearch().search(
                          searchRequest( repoId ).setQuery( "{\"matchAll\":{}}" )
                              .setAggregations( "{\"x\":{\"cardinality\":{\"field\":\"data.category\"}}}" )
                              .build() ) ), "an unimplemented aggregation type must fail loudly, never come back empty" );
        assertEquals( Status.Code.INVALID_ARGUMENT, statusOf( () -> nodeSearch().search(
            searchRequest( repoId ).setQuery( "{\"matchAll\":{}}" )
                .setAggregations( "{\"x\":{\"min\":{\"field\":\"data.population\"},\"aggregations\":" +
                                      "{\"y\":{\"max\":{\"field\":\"data.population\"}}}}}" )
                .build() ) ), "a sub-aggregation under a metric aggregation must be rejected, not dropped" );
    }

    // ---------------------------------------------------------------- Gate E: aggregations

    /**
     * Every aggregation family against a real OpenSearch, checked at BUCKET level on the tagged wire
     * document. The engine tests prove the JSON shapes in isolation; what only a real engine can prove
     * is that the bucket KEYS come back in the spellings XP's result types have always carried — which
     * is where the two traps live:
     * <ul>
     * <li>a numeric histogram key must be {@code "0"}/{@code "500"}, not {@code "0.0"}, because ES
     * 2.4's raw formatter collapsed an integral double to a long;</li>
     * <li>a date range or date histogram key must be an ISO instant with millis, which only happens
     * because the translator always sends a {@code format} — the field's own
     * {@code epoch_millis||strict_date_optional_time} would key them by epoch millis instead.</li>
     * </ul>
     * Both are silent-wrong-value failures, not errors.
     */
    @Test
    void everyAggregationFamilyComesBackTaggedAndBucketedFromTheRealEngine()
    {
        String repoId = createRepo( "queryaggs" );
        indexAndRefresh( repoId, aggregationDocument( "node-1", "c1", 100, "2020-01-15T00:00:00Z", 59.91273, 10.74609 ) );
        indexAndRefresh( repoId, aggregationDocument( "node-2", "c1", 300, "2020-02-20T00:00:00Z", 60.39299, 5.32415 ) );
        indexAndRefresh( repoId, aggregationDocument( "node-3", "c1", 700, "2021-01-10T00:00:00Z", 52.52001, 13.40495 ) );
        indexAndRefresh( repoId, aggregationDocument( "node-4", "c2", 900, "2021-05-05T00:00:00Z", 40.71427, -74.00597 ) );

        // terms, ordered by doc count DESC
        JsonNode terms = aggregation( repoId, "{\"byCategory\":{\"terms\":{\"field\":\"data.category\",\"size\":10," +
            "\"minDocCount\":1,\"order\":{\"type\":\"DOC_COUNT\",\"direction\":\"DESC\"}}}}", "byCategory" );
        assertEquals( "terms", terms.path( "type" ).asText() );
        assertEquals( "string", terms.path( "keyType" ).asText() );
        assertEquals( List.of( "c1:3", "c2:1" ), buckets( terms ) );

        // the four metric families
        JsonNode stats = aggregation( repoId, "{\"popStats\":{\"stats\":{\"field\":\"data.population\"}}}", "popStats" );
        assertEquals( "stats", stats.path( "type" ).asText() );
        assertEquals( "none", stats.path( "keyType" ).asText() );
        assertEquals( 4, stats.path( "count" ).asInt() );
        assertEquals( 100.0, stats.path( "min" ).asDouble() );
        assertEquals( 900.0, stats.path( "max" ).asDouble() );
        assertEquals( 2000.0, stats.path( "sum" ).asDouble() );
        assertEquals( 500.0, stats.path( "avg" ).asDouble() );

        assertEquals( 100.0,
                      aggregation( repoId, "{\"a\":{\"min\":{\"field\":\"data.population\"}}}", "a" ).path( "value" ).asDouble() );
        assertEquals( 900.0,
                      aggregation( repoId, "{\"a\":{\"max\":{\"field\":\"data.population\"}}}", "a" ).path( "value" ).asDouble() );
        // value_count resolves to the TEXT variant, exactly as XP's factory does (StaticIndexValueType
        // .STRING) -- so it counts data.population._text and every value has one.
        assertEquals( 4.0,
                      aggregation( repoId, "{\"a\":{\"valueCount\":{\"field\":\"data.population\"}}}", "a" ).path( "value" ).asDouble() );

        // numeric range: the engine's own generated keys, byte-identical to ES 2.4's
        JsonNode numericRange = aggregation( repoId, "{\"a\":{\"numericRange\":{\"field\":\"data.population\"," +
            "\"ranges\":[{\"to\":500},{\"from\":500}]}}}", "a" );
        assertEquals( List.of( "*-500.0:2", "500.0-*:2" ), buckets( numericRange ) );

        // numeric histogram: "0" and "500", NOT "0.0"/"500.0"
        JsonNode histogram =
            aggregation( repoId, "{\"a\":{\"histogram\":{\"field\":\"data.population\",\"interval\":500,\"minDocCount\":1}}}", "a" );
        assertEquals( "double", histogram.path( "keyType" ).asText() );
        assertEquals( List.of( "0:2", "500:2" ), buckets( histogram ),
                      "an integral histogram key is a LONG, as ES 2.4's raw formatter rendered it" );

        // date range: ISO keys with millis, and the bounds re-tagged as epoch millis
        JsonNode dateRange = aggregation( repoId, "{\"a\":{\"dateRange\":{\"field\":\"data.founded\"," +
            "\"ranges\":[{\"to\":\"2021-01-01T00:00:00Z\"},{\"from\":\"2021-01-01T00:00:00Z\"}]}}}", "a" );
        assertEquals( List.of( "*-2021-01-01T00:00:00.000Z:2", "2021-01-01T00:00:00.000Z-*:2" ), buckets( dateRange ) );
        assertEquals( 1609459200000L, dateRange.path( "buckets" ).get( 0 ).path( "toMillis" ).asLong() );

        // date histogram, calendar interval: one bucket per month that has a document
        JsonNode monthly =
            aggregation( repoId, "{\"a\":{\"dateHistogram\":{\"field\":\"data.founded\",\"interval\":\"1M\",\"minDocCount\":1}}}", "a" );
        assertEquals( "instant", monthly.path( "keyType" ).asText() );
        assertEquals( List.of( "2020-01-01T00:00:00.000Z:1", "2020-02-01T00:00:00.000Z:1", "2021-01-01T00:00:00.000Z:1",
                               "2021-05-01T00:00:00.000Z:1" ), buckets( monthly ) );
        assertEquals( 1577836800000L, monthly.path( "buckets" ).get( 0 ).path( "keyMillis" ).asLong(),
                      "the instant tag is what makes this a DateHistogramBucket rather than a plain one" );

        // …and the ambiguous interval: 1d is CALENDAR here, which the engine accepts as either
        JsonNode daily =
            aggregation( repoId, "{\"a\":{\"dateHistogram\":{\"field\":\"data.founded\",\"interval\":\"1d\",\"minDocCount\":1}}}", "a" );
        assertEquals( List.of( "2020-01-15T00:00:00.000Z:1", "2020-02-20T00:00:00.000Z:1", "2021-01-10T00:00:00.000Z:1",
                               "2021-05-05T00:00:00.000Z:1" ), buckets( daily ) );

        // a format XP sets wins over the default
        assertEquals( List.of( "2020-01:1", "2020-02:1", "2021-01:1", "2021-05:1" ), buckets( aggregation( repoId,
            "{\"a\":{\"dateHistogram\":{\"field\":\"data.founded\",\"interval\":\"1M\",\"minDocCount\":1," +
                "\"format\":\"yyyy-MM\"}}}", "a" ) ) );

        // geo distance from Oslo
        JsonNode geo = aggregation( repoId, "{\"a\":{\"geoDistance\":{\"field\":\"data.location\"," +
            "\"origin\":{\"lat\":59.91273,\"lon\":10.74609},\"unit\":\"km\"," +
            "\"ranges\":[{\"to\":100},{\"from\":100,\"to\":1000},{\"from\":1000}]}}}", "a" );
        assertEquals( List.of( "*-100.0:1", "100.0-1000.0:2", "1000.0-*:1" ), buckets( geo ) );

        // sub-aggregations: a metric nested under a bucket, one level and two
        JsonNode nested = aggregation( repoId, "{\"byCategory\":{\"terms\":{\"field\":\"data.category\"," +
            "\"order\":{\"type\":\"TERM\",\"direction\":\"ASC\"}},\"aggregations\":{\"popMax\":{\"max\":" +
            "{\"field\":\"data.population\"}}}}}", "byCategory" );
        assertEquals( 700.0, nested.path( "buckets" ).get( 0 ).path( "aggregations" ).get( 0 ).path( "value" ).asDouble() );
        assertEquals( "popMax", nested.path( "buckets" ).get( 0 ).path( "aggregations" ).get( 0 ).path( "name" ).asText() );

        JsonNode deep = aggregation( repoId, "{\"byCategory\":{\"terms\":{\"field\":\"data.category\"," +
            "\"order\":{\"type\":\"TERM\",\"direction\":\"ASC\"}},\"aggregations\":{\"byMonth\":{\"dateHistogram\":" +
            "{\"field\":\"data.founded\",\"interval\":\"1M\",\"minDocCount\":1},\"aggregations\":{\"popSum\":" +
            "{\"stats\":{\"field\":\"data.population\"}}}}}}}", "byCategory" );
        JsonNode innerMonth = deep.path( "buckets" ).get( 0 ).path( "aggregations" ).get( 0 );
        assertEquals( "dateHistogram", innerMonth.path( "type" ).asText() );
        assertEquals( "stats", innerMonth.path( "buckets" ).get( 0 ).path( "aggregations" ).get( 0 ).path( "type" ).asText() );

        // Post-filters narrow the hits but NOT the buckets -- the reason the envelope keeps the two
        // filter slots apart, and the one aggregation property a merged filter would silently break.
        SearchResult filtered = nodeSearch().search( searchRequest( repoId ).setQuery( "{\"matchAll\":{}}" )
                                                        .addPostFilters(
                                                            "{\"values\":{\"field\":\"data.category\",\"values\":[\"c2\"]}}" )
                                                        .setAggregations( "{\"byCategory\":{\"terms\":{\"field\":\"data.category\"," +
                                                                              "\"order\":{\"type\":\"TERM\",\"direction\":\"ASC\"}}}}" )
                                                        .build() );
        assertEquals( 1, filtered.getTotalHits() );
        assertEquals( List.of( "c1:3", "c2:1" ), buckets( named( filtered.getAggregations(), "byCategory" ) ) );
    }

    /**
     * <b>Why the XP-side renderer has to sort its ranges</b>, measured rather than assumed — and the
     * reason corpus row {@code AGG-13} came back with the same keys and counts in a DIFFERENT ORDER on
     * a repeat run before the fix.
     *
     * <p>{@code range} and {@code date_range} sort their own range array: a reversed request comes back
     * ascending and correctly counted, and so does one whose bounds are date math the engine has to
     * evaluate first. {@code geo_distance} does NOT — and the shared {@code RangeAggregator}
     * binary-searches that array assuming it is ordered, so an unsorted geo range list does not merely
     * answer in an odd order, it <b>silently loses documents</b>: the row below sends
     * {@code [{from:1000},{to:1000}]} against documents at 0 km and 5900 km and gets
     * {@code *-1000.0: 0}, dropping the one at the origin. No error, a plausible number.
     *
     * <p>Since XP holds its ranges in an {@code ImmutableSet} copied from a {@code HashSet} of objects
     * that never override {@code hashCode}, the order reaching the wire was identity-hash order and
     * varied per JVM run — so this was a live, silent, run-dependent MISCOUNT, not a cosmetic one. It is
     * now sorted in two places (the XP renderer for wire determinism, the NoDB translator for the engine
     * contract), and this row asserts the raw engine behaviour both fixes rest on by deliberately
     * bypassing the renderer.
     */
    @Test
    void rangeAggregationsSortTheirOwnRangesButAnUnsortedGeoDistanceListMiscounts()
    {
        String repoId = createRepo( "queryrangeorder" );
        indexAndRefresh( repoId, aggregationDocument( "node-1", "c1", 100, "2020-01-15T00:00:00Z", 59.91273, 10.74609 ) );
        indexAndRefresh( repoId, aggregationDocument( "node-2", "c2", 900, "2021-05-05T00:00:00Z", 40.71427, -74.00597 ) );

        assertEquals( List.of( "*-500.0:1", "500.0-*:1" ), buckets( aggregation( repoId,
            "{\"a\":{\"numericRange\":{\"field\":\"data.population\",\"ranges\":[{\"from\":500},{\"to\":500}]}}}", "a" ) ),
                      "a reversed range request comes back ascending: the engine sorts by resolved from" );

        assertEquals( List.of( "*-2021-01-01T00:00:00.000Z:1", "2021-01-01T00:00:00.000Z-*:1" ), buckets( aggregation( repoId,
            "{\"a\":{\"dateRange\":{\"field\":\"data.founded\",\"ranges\":[{\"from\":\"2021-01-01T00:00:00Z\"}," +
                "{\"to\":\"2021-01-01T00:00:00Z\"}]}}}", "a" ) ), "…and so does date_range, date math included" );

        // The hazard, straight at the engine with no translator in the way: an unsorted geo range list
        // loses the document at the origin entirely.
        ObjectNode raw = OpenSearchClient.mapper().createObjectNode();
        raw.put( "size", 0 );
        raw.set( "aggs", parseJson( "{\"a\":{\"geo_distance\":{\"field\":\"data.location._geopoint\"," +
                                        "\"origin\":{\"lat\":59.91273,\"lon\":10.74609},\"unit\":\"km\"," +
                                        "\"ranges\":[{\"from\":1000.0},{\"to\":1000.0}]}}}" ) );
        JsonNode unsorted = openSearchClient.search( SearchIndexNames.alias( new TenantContext( "acme" ), repoId ), raw );
        List<String> engineBuckets = new java.util.ArrayList<>();
        unsorted.path( "aggregations" )
            .path( "a" )
            .path( "buckets" )
            .forEach( bucket -> engineBuckets.add( bucket.path( "key" ).asText() + ":" + bucket.path( "doc_count" ).asLong() ) );
        assertEquals( List.of( "1000.0-*:1", "*-1000.0:0" ), engineBuckets,
                      "an unsorted geo_distance range list silently DROPS the document at the origin" );

        // …and the same request through the translator, which sorts it: right order, right counts.
        assertEquals( List.of( "*-1000.0:1", "1000.0-*:1" ), buckets( aggregation( repoId,
            "{\"a\":{\"geoDistance\":{\"field\":\"data.location\",\"origin\":{\"lat\":59.91273,\"lon\":10.74609}," +
                "\"unit\":\"km\",\"ranges\":[{\"from\":1000},{\"to\":1000}]}}}", "a" ) ),
                      "the translator sorts geo ranges, so an unsorted envelope cannot miscount" );
    }

    private static JsonNode parseJson( String json )
    {
        try
        {
            return OpenSearchClient.mapper().readTree( json );
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( e );
        }
    }

    /** {@code "<key>:<docCount>"} per bucket, in the order the engine returned them. */
    private static List<String> buckets( JsonNode aggregation )
    {
        List<String> out = new java.util.ArrayList<>();
        aggregation.path( "buckets" )
            .forEach( bucket -> out.add( bucket.path( "key" ).asText() + ":" + bucket.path( "docCount" ).asLong() ) );
        return out;
    }

    private static JsonNode aggregation( String repoId, String aggregations, String name )
    {
        SearchResult result = nodeSearch().search(
            searchRequest( repoId ).setQuery( "{\"matchAll\":{}}" ).setAggregations( aggregations ).build() );
        return named( result.getAggregations(), name );
    }

    private static JsonNode named( String tagged, String name )
    {
        try
        {
            for ( JsonNode aggregation : OpenSearchClient.mapper().readTree( tagged ) )
            {
                if ( name.equals( aggregation.path( "name" ).asText() ) )
                {
                    return aggregation;
                }
            }
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( "not a tagged aggregation document: " + tagged, e );
        }
        throw new AssertionError( "no aggregation named '" + name + "' in " + tagged );
    }

    /** One document carrying every value type the aggregation families need. */
    private static IndexDoc aggregationDocument( String nodeId, String category, double population, String founded, double lat,
                                                 double lon )
    {
        return IndexDoc.newBuilder()
            .setId( nodeId )
            .addFields( textField( "data.category", category ) )
            // The bare (text) variant AND the typed one, as XP emits for every indexed property --
            // which is what makes a value_count over the text variant meaningful.
            .addFields( textField( "data.population", Double.toString( population ) ) )
            .addFields( IndexField.newBuilder()
                            .setName( "data.population._number" )
                            .addValues( IndexValue.newBuilder().setDoubleValue( population ) ) )
            .addFields( IndexField.newBuilder()
                            .setName( "data.founded._datetime" )
                            .addValues( IndexValue.newBuilder().setInstantMillis( Instant.parse( founded ).toEpochMilli() ) ) )
            .addFields( textField( "data.location._geopoint", lat + "," + lon ) )
            .addFields( textField( IndexFields.PERMISSIONS_READ, "role:system.everyone" ) )
            .build();
    }

    // ---------------------------------------------------------------------------- Gate D: text

    /**
     * The text family end to end against a real OpenSearch: the analyzers the template declares,
     * the physical sub-fields the projection wrote, and the {@code simple_query_string} shape the
     * translator emits all have to agree, and none of that is provable by inspecting JSON. A wrong
     * physical name here returns zero hits with no error — the failure mode this gate exists to
     * exclude — so every assertion is a non-zero count on a query that must match.
     */
    @Test
    void theTextFamilyRunsAgainstTheRealAnalyzers()
    {
        String repoId = createRepo( "querytext" );
        indexAndRefresh( repoId, textDocument( "node-fisk", "Fisk og grønnsaker i Bergen", "/tree/a/b/c/d" ) );

        assertEquals( 1, textHits( repoId, "{\"fulltext\":{\"fields\":[\"data.description\"],\"query\":\"fisk bergen\"," +
            "\"operator\":\"AND\"}}" ), "fulltext AND over the analyzed sub-field" );

        // asciifolding is in the analyzer chain, so fulltext() IS accent-insensitive -- while the
        // raw _text keyword is NOT. That asymmetry is behaviour (Gate 0(e), UNTYPED-05).
        assertEquals( 1, textHits( repoId, "{\"fulltext\":{\"fields\":[\"data.description\"],\"query\":\"gronnsaker\"}}" ),
                      "asciifolding: 'gronnsaker' must find 'grønnsaker'" );
        assertEquals( 0, textHits( repoId, "{\"term\":{\"field\":\"data.description\",\"value\":\"gronnsaker\"}}" ),
                      "…and the raw text variant must NOT fold" );

        assertEquals( 1, textHits( repoId, "{\"fulltext\":{\"fields\":[\"data.descri*\"],\"query\":\"fisk\"}}" ),
                      "a field wildcard must keep the star last or it matches nothing" );

        assertEquals( 1, textHits( repoId, "{\"ngram\":{\"fields\":[\"data.description\"],\"query\":\"fis ber\"," +
            "\"operator\":\"AND\"}}" ), "edge-ngram prefixes" );

        assertEquals( 1, textHits( repoId,
                                   "{\"stemmed\":{\"fields\":[\"data.description\"],\"query\":\"fisker\",\"language\":\"no\"}}" ),
                      "the Norwegian stemmer must reach ._stemmed_nb with the 'norwegian' analyzer" );

        assertEquals( 1, textHits( repoId, "{\"pathMatch\":{\"field\":\"_path\",\"path\":\"/tree/a/b/c/d\"}}" ) );
        assertEquals( 1, textHits( repoId, "{\"pathMatch\":{\"field\":\"_path\",\"path\":\"/tree/a/b/c/d\",\"minimumMatch\":3.0}}" ),
                      "minimumMatch adds a hard prefix floor, not a scoring hint" );
        assertEquals( 0, textHits( repoId, "{\"pathMatch\":{\"field\":\"_path\",\"path\":\"/other/x/y/z\",\"minimumMatch\":3.0}}" ),
                      "…and the floor must actually exclude" );

        assertEquals( 1, textHits( repoId, "{\"fulltext\":{\"fields\":[\"data.description\"],\"query\":\"\"}}" ),
                      "an empty fulltext query string is match_all" );

        // A TRAILING WILDCARD on a stemmed field. The query text is analyzed by the same stemmer as
        // the field, so `grønnsake*` stems to `grønnsak` before becoming a prefix -- which is what
        // makes it match the indexed `grønnsak`. Asserted because the alternative (prefixing the
        // RAW text) silently matches nothing: `grønnsake` is longer than the stem it should match.
        assertEquals( 1, textHits( repoId,
                                   "{\"stemmed\":{\"fields\":[\"data.description\"],\"query\":\"grønnsake*\",\"operator\":\"AND\"," +
                                       "\"language\":\"no\"}}" ), "a wildcard must be stemmed before it becomes a prefix" );
    }

    /**
     * D8 end to end. The sort is an ordinary keyword sort; what makes it a LANGUAGE sort is that the
     * indexed value is a hex ICU collation key computed by {@code CollationKeyResolver}. Norwegian
     * orders {@code æ < ø < å}, which is not the order the raw strings have — so this fails if the
     * sort silently fell back to {@code ._orderby} or to a field nobody wrote.
     */
    @Test
    void aCollateSortOrdersByThePrecomputedCollationKey()
    {
        String repoId = createRepo( "querycollate" );
        indexAndRefresh( repoId, collationDocument( "node-aa", "ånd" ) );
        indexAndRefresh( repoId, collationDocument( "node-ae", "ærlig" ) );
        indexAndRefresh( repoId, collationDocument( "node-oe", "øre" ) );

        assertEquals( List.of( "node-ae", "node-oe", "node-aa" ), sortedIds( repoId, "no", "ASC" ),
                      "Norwegian collation is æ < ø < å" );
        assertEquals( List.of( "node-aa", "node-oe", "node-ae" ), sortedIds( repoId, "no", "DESC" ) );
        assertEquals( sortedIds( repoId, "no", "ASC" ), sortedIds( repoId, "nb", "ASC" ),
                      "COLLATE no and COLLATE nb resolve to the same field" );

        // DUCET orders these differently from Norwegian, which is what proves the locale is really
        // selecting a field rather than being ignored: the root collation decomposes the letters
        // (æ→"ae", å→"a"+ring, ø→"o"), so it sorts ærlig < ånd < øre where Norwegian sorts them
        // ærlig < øre < ånd. The two orders share no adjacency, so neither can be the other by luck.
        assertEquals( List.of( "node-ae", "node-aa", "node-oe" ), sortedIds( repoId, "de", "ASC" ),
                      "German's order-by is DUCET, not ._orderby_de" );
        assertEquals( sortedIds( repoId, "de", "ASC" ), sortedIds( repoId, "xx", "ASC" ),
                      "an unmapped locale falls back to the same DUCET field" );
    }

    @Test
    void aGeoDistanceSortOrdersByDistanceFromThePoint()
    {
        String repoId = createRepo( "querygeo" );
        indexAndRefresh( repoId, geoDocument( "node-oslo", 59.91273, 10.74609 ) );
        indexAndRefresh( repoId, geoDocument( "node-bergen", 60.39299, 5.32415 ) );
        indexAndRefresh( repoId, geoDocument( "node-berlin", 52.52001, 13.40495 ) );

        String sort = "{\"field\":\"data.location\",\"type\":\"geoDistance\"," +
            "\"location\":{\"lat\":59.91273,\"lon\":10.74609},\"direction\":\"ASC\"}";
        SearchResult result =
            nodeSearch().search( searchRequest( repoId ).setQuery( "{\"matchAll\":{}}" ).addSort( sort ).build() );

        assertEquals( List.of( "node-oslo", "node-bergen", "node-berlin" ),
                      result.getHitsList().stream().map( SearchHit::getId ).toList() );
        assertEquals( "0.0", result.getHits( 0 ).getSortValues( 0 ), "the point itself is at distance zero" );
    }

    /**
     * Suggesters and highlighting on the wire. Both are opaque JSON slots in the envelope, and both
     * come back in a shape XP's client transcribes rather than maps — so the assertion that matters
     * is that the SERVER stripped the physical postfix, since the client does not.
     */
    @Test
    void suggestAndHighlightComeBackOnTheWire()
    {
        String repoId = createRepo( "querysuggest" );
        indexAndRefresh( repoId, textDocument( "node-fisk", "Fisk og laks i Oslo", "/tree/a" ) );

        SearchResult suggested = nodeSearch().search( searchRequest( repoId ).setQuery( "{\"matchAll\":{}}" ).setSuggest(
            "{\"descSuggest\":{\"text\":\"fisl\",\"term\":{\"field\":\"data.description\",\"suggestMode\":\"always\"," +
                "\"stringDistance\":\"jarowinkler\"}}}" ).build() );

        assertTrue( suggested.getSuggestions().contains( "\"descSuggest\"" ), suggested.getSuggestions() );
        assertTrue( suggested.getSuggestions().contains( "\"fisk\"" ),
                    "the term suggester must reach the analyzed sub-field and propose 'fisk': " + suggested.getSuggestions() );

        SearchResult highlighted = nodeSearch().search( searchRequest( repoId ).setQuery(
                "{\"fulltext\":{\"fields\":[\"data.description\"],\"query\":\"fisk\"}}" )
                                                           .setHighlight(
                                                               "{\"properties\":[{\"name\":\"data.description\"," +
                                                                   "\"settings\":{\"numOfFragments\":2}}]}" )
                                                           .build() );

        assertEquals( 1, highlighted.getHitsCount() );
        assertEquals( 1, highlighted.getHits( 0 ).getHighlightsCount(),
                      "the three expanded fields must collapse onto ONE canonical property name" );
        assertEquals( "data.description", highlighted.getHits( 0 ).getHighlights( 0 ).getName() );
        assertTrue( highlighted.getHits( 0 ).getHighlights( 0 ).getFragments( 0 ).contains( "<em>" ),
                    highlighted.getHits( 0 ).getHighlights( 0 ).getFragmentsList().toString() );
    }

    private static long textHits( String repoId, String query )
    {
        return nodeSearch().search( searchRequest( repoId ).setQuery( query ).build() ).getTotalHits();
    }

    private static List<String> sortedIds( String repoId, String language, String direction )
    {
        String sort = "{\"field\":\"data.word\",\"language\":\"" + language + "\",\"direction\":\"" + direction + "\"}";
        return nodeSearch().search( searchRequest( repoId ).setQuery( "{\"matchAll\":{}}" ).addSort( sort ).build() )
            .getHitsList()
            .stream()
            .map( SearchHit::getId )
            .toList();
    }

    /** Every text sub-field XP's index config produces for one string property, plus a path. */
    private static IndexDoc textDocument( String nodeId, String description, String path )
    {
        return IndexDoc.newBuilder()
            .setId( nodeId )
            .addFields( textField( "data.description", description ) )
            .addFields( textField( "data.description._analyzed", description ) )
            .addFields( textField( "data.description._ngram", description ) )
            .addFields( textField( "data.description._stemmed_nb", description ) )
            .addFields( textField( "_path", path ) )
            .addFields( textField( "_path._path", path ) )
            .addFields( textField( IndexFields.PERMISSIONS_READ, "role:system.everyone" ) )
            .build();
    }

    /**
     * The {@code ._orderby_<loc>} variants XP emits for a collated property: the SAME
     * already-lowercased order-by value on every locale field, which the projection then replaces
     * with that locale's collation key.
     */
    private static IndexDoc collationDocument( String nodeId, String word )
    {
        return IndexDoc.newBuilder()
            .setId( nodeId )
            .addFields( textField( "data.word", word ) )
            .addFields( textField( "data.word._orderby", word ) )
            .addFields( textField( "data.word._orderby_no", word ) )
            .addFields( textField( "data.word._orderby_ducet", word ) )
            .addFields( textField( IndexFields.PERMISSIONS_READ, "role:system.everyone" ) )
            .build();
    }

    private static IndexDoc geoDocument( String nodeId, double lat, double lon )
    {
        return IndexDoc.newBuilder()
            .setId( nodeId )
            .addFields( textField( "data.location", lat + "," + lon ) )
            .addFields( textField( "data.location._geopoint", lat + "," + lon ) )
            .addFields( textField( IndexFields.PERMISSIONS_READ, "role:system.everyone" ) )
            .build();
    }

    private static Status.Code statusOf( Runnable call )
    {
        try
        {
            call.run();
            return Status.Code.OK;
        }
        catch ( io.grpc.StatusRuntimeException e )
        {
            return e.getStatus().getCode();
        }
    }

    private static com.enonic.nodb.proto.v1.SearchRequest.Builder searchRequest( String repoId )
    {
        return com.enonic.nodb.proto.v1.SearchRequest.newBuilder()
            .setFormatVersion( 1 )
            .addSources( SearchSourceRef.newBuilder()
                             .setRepoId( repoId )
                             .setBranch( "master" )
                             .addPrincipals( IndexFields.ADMIN_PRINCIPAL ) )
            .setSize( 10 );
    }

    private static void indexAndRefresh( String repoId, IndexDoc doc )
    {
        IndexAck ack = nodeSearch().indexDocuments(
            IndexDocumentsRequest.newBuilder().setRepoId( repoId ).setBranch( "master" ).addDocuments( doc ).build() );
        nodeSearch().awaitRefresh(
            AwaitRefreshRequest.newBuilder().setSeq( ack.getOutboxSeq() ).addRepoIds( repoId ).setTimeoutMillis( 30_000 ).build() );
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
