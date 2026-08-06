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
                                                                                                                searchIndexAdmin ),
                                                                             new SearchQueryExecutor( openSearchClient ) ),
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

        // Gate E's fence, asserted rather than assumed: suggest and highlight are translated now,
        // aggregations are not, and an aggregation must keep erroring instead of being dropped.
        assertEquals( Status.Code.INVALID_ARGUMENT, statusOf( () -> nodeSearch().search(
            searchRequest( repoId ).setQuery( "{\"matchAll\":{}}" ).setAggregations( "{\"x\":{}}" ).build() ) ) );
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
