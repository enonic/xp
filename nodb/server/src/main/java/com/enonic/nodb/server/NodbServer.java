package com.enonic.nodb.server;

import java.nio.file.Path;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.nodb.engine.binary.BinaryStore;
import com.enonic.nodb.engine.search.Indexer;
import com.enonic.nodb.engine.search.OpenSearchClient;
import com.enonic.nodb.engine.search.OpenSearchConfig;
import com.enonic.nodb.engine.search.SearchIndexAdmin;
import com.enonic.nodb.server.auth.DevKeys;
import com.enonic.nodb.server.auth.JwtVerifier;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.service.BinariesService;
import com.enonic.nodb.server.service.NodeSearchService;
import com.enonic.nodb.server.service.NodeStoreService;
import com.enonic.nodb.server.service.RepositoryAdminService;

/**
 * The standalone {@code enonic/nodb} server binding (DESIGN.md §7): engine + gRPC, no
 * OSGi/Spring. Wires a Postgres pool, the JWT auth interceptor (trivial dev issuer's
 * public key — see {@link com.enonic.nodb.server.auth.NodbTokenTool}), and the service
 * impls implemented so far: NodeStore's data-plane RPCs, RepositoryAdmin's management-
 * plane RPCs (slice 1), and Binaries' data-plane RPCs over S3 (Phase 2 Gate A,
 * BUILD-PHASE-2.md). NodeSearch/ChangeFeed/BulkTransfer/Snapshots are out of scope so far
 * and are simply never registered on this server.
 *
 * <p>Configuration is env-var only (no config file yet, matching this slice's scope):
 * <ul>
 *   <li>{@code NODB_PG_URL} — JDBC URL (default {@code jdbc:postgresql://localhost:5432/nodb})
 *   <li>{@code NODB_PG_USER} / {@code NODB_PG_PASSWORD} — the pooled service role's credentials
 *   <li>{@code NODB_PORT} — gRPC port (default 7700)
 *   <li>{@code NODB_KEYS_DIR} — dev issuer keypair directory (default {@code ./.nodb-dev-keys});
 *       the server only ever reads the public key from here, never the private key
 *   <li>{@code NODB_S3_*} — object store config for the {@code Binaries} service; see
 *       {@link BinaryStore#fromEnv}
 *   <li>{@code NODB_OPENSEARCH_URL} (+ {@code NODB_OPENSEARCH_REPLICAS} /
 *       {@code _REFRESH_INTERVAL} / {@code _CONNECT_TIMEOUT_MS} / {@code _REQUEST_TIMEOUT_MS}) —
 *       the search backend; see {@link OpenSearchConfig}. UNSET means no search backend, which is
 *       a legitimate state for the whole pre-Gate-F hybrid window: {@code NodeSearch} is then not
 *       registered at all and answers UNIMPLEMENTED.
 *   <li>{@code NODB_OPS_PORT} — {@code /health/live} + {@code /health/ready} (default 7701)
 * </ul>
 */
public final class NodbServer
{
    private static final Logger LOG = LoggerFactory.getLogger( NodbServer.class );

    private final Server server;

    private final HikariDataSource dataSource;

    private final BinaryStore binaryStore;

    /** {@code null} when {@code NODB_OPENSEARCH_URL} is unset — the pre-Gate-F hybrid window. */
    private final OpenSearchClient openSearchClient;

    /** One per tenant that has issued a NodeSearch RPC; empty when there is no search backend. */
    private final List<Indexer> indexers;

    NodbServer( Server server, HikariDataSource dataSource, BinaryStore binaryStore )
    {
        this( server, dataSource, binaryStore, null, new CopyOnWriteArrayList<>() );
    }

    NodbServer( Server server, HikariDataSource dataSource, BinaryStore binaryStore, OpenSearchClient openSearchClient,
                List<Indexer> indexers )
    {
        this.server = server;
        this.dataSource = dataSource;
        this.binaryStore = binaryStore;
        this.openSearchClient = openSearchClient;
        this.indexers = indexers;
    }

    /** {@code null} when no search backend is configured. Exposed for the readiness probe and tests. */
    public OpenSearchClient openSearchClient()
    {
        return openSearchClient;
    }

    /**
     * Readiness contribution for the search backend: reachable AND every tenant indexer this server
     * started is still running. "Reachable" alone would pass while nothing drains the outbox, which
     * is the same as {@code refresh(SEARCH)} being broken.
     */
    public boolean isSearchReady()
    {
        if ( openSearchClient == null )
        {
            return true;
        }
        return openSearchClient.ping() && indexers.stream().allMatch( Indexer::isRunning );
    }

    /**
     * Builds the {@code Binaries} service's {@link BinaryStore} from environment
     * configuration ({@code NODB_S3_*} — see {@link BinaryStore#fromEnv}). Existing callers
     * (this class's own {@code main}, the bench harness) that don't care about binaries
     * still get a working server: the client is built lazily/without eager I/O, so the
     * absence of real S3 config only surfaces as an error the first time a {@code
     * Binaries} RPC is actually invoked, exactly like an unreachable Postgres would.
     */
    public static NodbServer create( int port, HikariDataSource dataSource, RSAPublicKey issuerPublicKey )
        throws java.io.IOException
    {
        return create( port, dataSource, issuerPublicKey, BinaryStore.fromEnv() );
    }

    public static NodbServer create( int port, HikariDataSource dataSource, RSAPublicKey issuerPublicKey, BinaryStore binaryStore )
        throws java.io.IOException
    {
        return create( port, dataSource, issuerPublicKey, binaryStore, OpenSearchConfig.fromEnv() );
    }

    /**
     * Phase 4 Gate A: registers {@code NodeSearch} only when a search backend is configured.
     *
     * <p>Registering it unconditionally would be worse than leaving it out. grpc-java's generated
     * base class answers UNIMPLEMENTED for any method a subclass does not override, and that is the
     * convention this server already uses for out-of-scope RPCs — so "no search backend" reads
     * exactly like "no search RPCs", which is the truth, instead of a service that accepts index
     * documents and silently stores them with nothing to apply them.
     */
    public static NodbServer create( int port, HikariDataSource dataSource, RSAPublicKey issuerPublicKey, BinaryStore binaryStore,
                                     OpenSearchConfig openSearchConfig )
        throws java.io.IOException
    {
        TenantAuthInterceptor authInterceptor = new TenantAuthInterceptor( new JwtVerifier( issuerPublicKey ) );

        OpenSearchClient openSearchClient = openSearchConfig.isConfigured() ? new OpenSearchClient( openSearchConfig ) : null;
        SearchIndexAdmin searchIndexAdmin = openSearchClient == null ? null : new SearchIndexAdmin( dataSource, openSearchClient );

        ServerBuilder<?> builder = ServerBuilder.forPort( port )
            .addService( ServerInterceptors.intercept( new NodeStoreService( dataSource ), authInterceptor ) )
            .addService( ServerInterceptors.intercept( new RepositoryAdminService( dataSource, searchIndexAdmin ), authInterceptor ) )
            .addService( ServerInterceptors.intercept( new BinariesService( binaryStore ), authInterceptor ) );

        // Shared with the NodeSearchService's indexer factory below and with the NodbServer this
        // method returns, so the readiness probe and shutdown both see every started indexer.
        List<Indexer> indexers = new CopyOnWriteArrayList<>();

        if ( openSearchClient != null )
        {
            builder.addService( ServerInterceptors.intercept( new NodeSearchService( dataSource, tenant -> {
                Indexer indexer = new Indexer( dataSource, tenant, openSearchClient, searchIndexAdmin );
                indexer.start();
                indexers.add( indexer );
                return indexer;
            } ), authInterceptor ) );
            LOG.info( "Search backend enabled: {}", openSearchConfig.baseUrl() );
        }
        else
        {
            LOG.info( "No search backend configured (NODB_OPENSEARCH_URL unset); NodeSearch RPCs answer UNIMPLEMENTED" );
        }

        NodbServer nodbServer = new NodbServer( builder.build().start(), dataSource, binaryStore, openSearchClient, indexers );

        // Log the actual bound port, not the requested one: callers may pass 0 (OS-assigned
        // ephemeral port, e.g. the bench harness), in which case `port` itself is useless.
        LOG.info( "NoDB server listening on port {}", nodbServer.getPort() );
        return nodbServer;
    }

    /** The bound TCP port — useful when {@link #create} was called with port 0 (OS-assigned ephemeral port), e.g. in tests/bench. */
    public int getPort()
    {
        return server.getPort();
    }

    public void awaitTermination()
        throws InterruptedException
    {
        server.awaitTermination();
    }

    public void shutdown()
    {
        server.shutdown();
        try
        {
            if ( !server.awaitTermination( 10, TimeUnit.SECONDS ) )
            {
                server.shutdownNow();
            }
        }
        catch ( InterruptedException e )
        {
            server.shutdownNow();
            Thread.currentThread().interrupt();
        }
        indexers.forEach( Indexer::close );
        if ( openSearchClient != null )
        {
            openSearchClient.close();
        }
        dataSource.close();
        binaryStore.close();
    }

    public static void main( String[] args )
        throws Exception
    {
        String pgUrl = env( "NODB_PG_URL", "jdbc:postgresql://localhost:5432/nodb" );
        String pgUser = env( "NODB_PG_USER", "nodb" );
        String pgPassword = env( "NODB_PG_PASSWORD", "" );
        int port = Integer.parseInt( env( "NODB_PORT", "7700" ) );
        Path keysDir = Path.of( env( "NODB_KEYS_DIR", "./.nodb-dev-keys" ) );

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( pgUrl );
        config.setUsername( pgUser );
        config.setPassword( pgPassword );
        config.setMaximumPoolSize( 16 );
        HikariDataSource dataSource = new HikariDataSource( config );

        RSAPublicKey publicKey = DevKeys.loadOrGeneratePublicKey( keysDir );
        NodbServer nodbServer = NodbServer.create( port, dataSource, publicKey );

        int healthPort = Integer.parseInt( env( "NODB_OPS_PORT", Integer.toString( HealthServer.DEFAULT_PORT ) ) );
        HealthServer healthServer = HealthServer.start( healthPort, dataSource,
                                                       nodbServer.openSearchClient() == null ? null : nodbServer::isSearchReady );

        Runtime.getRuntime().addShutdownHook( new Thread( () -> {
            LOG.info( "Shutting down NoDB server" );
            healthServer.close();
            nodbServer.shutdown();
        } ) );

        nodbServer.awaitTermination();
    }

    private static String env( String name, String defaultValue )
    {
        String value = System.getenv( name );
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
