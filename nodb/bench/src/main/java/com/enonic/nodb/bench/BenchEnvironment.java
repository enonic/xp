package com.enonic.nodb.bench;

import java.nio.file.Files;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.enonic.nodb.client.NodbClient;
import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.TenantProvisioner;
import com.enonic.nodb.engine.binary.BinaryStore;
import com.enonic.nodb.engine.search.OpenSearchClient;
import com.enonic.nodb.engine.search.OpenSearchConfig;
import com.enonic.nodb.engine.search.SearchIndexNames;
import com.enonic.nodb.proto.v1.CreateRepositoryRequest;
import com.enonic.nodb.proto.v1.NodeSearchGrpc;
import com.enonic.nodb.server.NodbServer;
import com.enonic.nodb.server.auth.DevKeys;
import com.enonic.nodb.server.auth.JwtIssuer;
import com.enonic.nodb.server.auth.Scope;

/**
 * Bootstraps everything the bench needs to run "for real" (BUILD-SLICE-1.md step 6): a
 * postgres:17 testcontainer, a "bench" tenant provisioned into it, an RSA dev-issuer
 * keypair, a {@link NodbServer} bound to a real loopback TCP port (an ephemeral OS-assigned
 * port — same {@link NodbServer#create} the standalone binary uses, NOT the in-process
 * channel {@code NodbServerIntegrationTest} uses for gate 5 — the whole point of this
 * bench is an honest network+serialization+DB round-trip number, so it has to go over a
 * real socket), a repo+master branch, and a connected {@link NodbClient} holding a runtime
 * token. {@link #close()} tears all of it down in reverse order.
 *
 * <p>Phase 4 Gate G: also starts an OpenSearch 3.7.0 container (stock image, D8) and
 * configures the server with it (5-arg {@link NodbServer#create}), so the COMPLETE
 * PG + OpenSearch path exists: {@code CreateRepository} makes the per-repo index, the
 * outbox indexer runs, and the {@code NodeSearch} RPCs answer. The production default
 * {@code refresh_interval} (1s) is kept deliberately — this bench records what a
 * deployment would see, unlike the engine tests which pin {@code -1} so a missing
 * {@code awaitRefresh} fails deterministically.
 */
final class BenchEnvironment
    implements AutoCloseable
{
    static final String OPENSEARCH_IMAGE = "opensearchproject/opensearch:3.7.0";

    private final PostgreSQLContainer<?> postgres;

    private final GenericContainer<?> opensearch;

    private final HikariDataSource dataSource;

    private final NodbServer server;

    private final NodbClient client;

    private final ManagedChannel searchChannel;

    private final NodeSearchGrpc.NodeSearchBlockingStub nodeSearch;

    private final OpenSearchClient openSearchClient;

    private final String repoId;

    private final String searchAlias;

    private final String runtimeToken;

    private BenchEnvironment( PostgreSQLContainer<?> postgres, GenericContainer<?> opensearch, HikariDataSource dataSource,
                               NodbServer server, NodbClient client, ManagedChannel searchChannel,
                               NodeSearchGrpc.NodeSearchBlockingStub nodeSearch, OpenSearchClient openSearchClient, String repoId,
                               String searchAlias, String runtimeToken )
    {
        this.postgres = postgres;
        this.opensearch = opensearch;
        this.dataSource = dataSource;
        this.server = server;
        this.client = client;
        this.searchChannel = searchChannel;
        this.nodeSearch = nodeSearch;
        this.openSearchClient = openSearchClient;
        this.repoId = repoId;
        this.searchAlias = searchAlias;
        this.runtimeToken = runtimeToken;
    }

    static BenchEnvironment start()
        throws Exception
    {
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>( "postgres:17" );
        postgres.start();

        GenericContainer<?> opensearch = new GenericContainer<>( OPENSEARCH_IMAGE ).withExposedPorts( 9200 )
            .withEnv( "discovery.type", "single-node" )
            .withEnv( "DISABLE_SECURITY_PLUGIN", "true" )
            .withEnv( "DISABLE_INSTALL_DEMO_CONFIG", "true" )
            .withEnv( "OPENSEARCH_JAVA_OPTS", "-Xms512m -Xmx512m" )
            .waitingFor( Wait.forHttp( "/_cluster/health?wait_for_status=yellow&timeout=30s" )
                             .forPort( 9200 )
                             .forStatusCode( 200 )
                             .withStartupTimeout( Duration.ofMinutes( 4 ) ) );
        opensearch.start();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl( postgres.getJdbcUrl() );
        hikariConfig.setUsername( postgres.getUsername() );
        hikariConfig.setPassword( postgres.getPassword() );
        hikariConfig.setMaximumPoolSize( 16 );
        HikariDataSource dataSource = new HikariDataSource( hikariConfig );

        String tenantId = "bench";
        new TenantProvisioner( dataSource, postgres.getUsername() ).provision( new TenantContext( tenantId ) );

        OpenSearchConfig openSearchConfig =
            OpenSearchConfig.of( "http://" + opensearch.getHost() + ":" + opensearch.getMappedPort( 9200 ) );

        KeyPair issuerKeyPair = DevKeys.loadOrGenerate( Files.createTempDirectory( "nodb-bench-dev-keys" ) );
        NodbServer server =
            NodbServer.create( 0, dataSource, (RSAPublicKey) issuerKeyPair.getPublic(), BinaryStore.fromEnv(), openSearchConfig );

        String operatorToken = JwtIssuer.mint( (RSAPrivateKey) issuerKeyPair.getPrivate(), (RSAPublicKey) issuerKeyPair.getPublic(),
                                                 tenantId, Scope.OPERATOR, "bench-operator", Duration.ofHours( 2 ) );
        String runtimeToken = JwtIssuer.mint( (RSAPrivateKey) issuerKeyPair.getPrivate(), (RSAPublicKey) issuerKeyPair.getPublic(),
                                                tenantId, Scope.RUNTIME, "bench-runtime", Duration.ofHours( 2 ) );

        NodbClient adminClient = NodbClient.connect( "localhost", server.getPort(), operatorToken );
        String repoId = "bench-repo";
        adminClient.createRepository( CreateRepositoryRequest.newBuilder().setRepoId( repoId ).build() );
        adminClient.close();

        NodbClient client = NodbClient.connect( "localhost", server.getPort(), runtimeToken );

        ManagedChannel searchChannel = ManagedChannelBuilder.forAddress( "localhost", server.getPort() ).usePlaintext().build();
        NodeSearchGrpc.NodeSearchBlockingStub nodeSearch =
            NodeSearchGrpc.newBlockingStub( searchChannel ).withCallCredentials( bearerToken( runtimeToken ) );

        String searchAlias = SearchIndexNames.alias( new TenantContext( tenantId ), repoId );

        return new BenchEnvironment( postgres, opensearch, dataSource, server, client, searchChannel, nodeSearch,
                                     new OpenSearchClient( openSearchConfig ), repoId, searchAlias, runtimeToken );
    }

    NodbClient client()
    {
        return client;
    }

    String repoId()
    {
        return repoId;
    }

    NodeSearchGrpc.NodeSearchBlockingStub nodeSearch()
    {
        return nodeSearch;
    }

    OpenSearchClient openSearchClient()
    {
        return openSearchClient;
    }

    String searchAlias()
    {
        return searchAlias;
    }

    /**
     * PHASE-3 GATE-0 addition (BUILD-PHASE-3.md deliverable 2): {@link NodbClient}'s thin
     * surface deliberately only exposes the slice-1 core method list plus a few additions
     * (see its own class javadoc), so it has no {@code storeVersion}/{@code
     * storeBranchEntry} passthroughs. The Gate-0 A-vs-B payload-path bench needs the raw
     * {@code NodeStoreGrpc} stub to call those two RPCs directly (path A: the
     * decorator-shape 5-separate-RPCs save). Exposing the port lets that scratch bench
     * build its own authenticated stub over the same real loopback server without
     * duplicating {@link #start()}'s container/server/token bootstrapping.
     */
    int port()
    {
        return server.getPort();
    }

    /** See {@link #port()} — the matching bearer token for a raw stub built from that port. */
    String runtimeToken()
    {
        return runtimeToken;
    }

    private static CallCredentials bearerToken( String token )
    {
        Metadata.Key<String> authorizationKey = Metadata.Key.of( "authorization", Metadata.ASCII_STRING_MARSHALLER );
        String headerValue = "Bearer " + token;
        return new CallCredentials()
        {
            @Override
            public void applyRequestMetadata( RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier )
            {
                Metadata headers = new Metadata();
                headers.put( authorizationKey, headerValue );
                applier.apply( headers );
            }
        };
    }

    @Override
    public void close()
    {
        client.close();
        searchChannel.shutdown();
        try
        {
            if ( !searchChannel.awaitTermination( 5, TimeUnit.SECONDS ) )
            {
                searchChannel.shutdownNow();
            }
        }
        catch ( InterruptedException e )
        {
            searchChannel.shutdownNow();
            Thread.currentThread().interrupt();
        }
        openSearchClient.close();
        server.shutdown();
        dataSource.close();
        postgres.stop();
        opensearch.stop();
    }
}
