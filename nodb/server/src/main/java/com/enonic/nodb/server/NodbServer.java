package com.enonic.nodb.server;

import java.nio.file.Path;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.nodb.engine.binary.BinaryStore;
import com.enonic.nodb.server.auth.DevKeys;
import com.enonic.nodb.server.auth.JwtVerifier;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.service.BinariesService;
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
 * </ul>
 */
public final class NodbServer
{
    private static final Logger LOG = LoggerFactory.getLogger( NodbServer.class );

    private final Server server;

    private final HikariDataSource dataSource;

    private final BinaryStore binaryStore;

    NodbServer( Server server, HikariDataSource dataSource, BinaryStore binaryStore )
    {
        this.server = server;
        this.dataSource = dataSource;
        this.binaryStore = binaryStore;
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
        TenantAuthInterceptor authInterceptor = new TenantAuthInterceptor( new JwtVerifier( issuerPublicKey ) );

        Server server = ServerBuilder.forPort( port )
            .addService( ServerInterceptors.intercept( new NodeStoreService( dataSource ), authInterceptor ) )
            .addService( ServerInterceptors.intercept( new RepositoryAdminService( dataSource ), authInterceptor ) )
            .addService( ServerInterceptors.intercept( new BinariesService( binaryStore ), authInterceptor ) )
            .build()
            .start();

        // Log the actual bound port, not the requested one: callers may pass 0 (OS-assigned
        // ephemeral port, e.g. the bench harness), in which case `port` itself is useless.
        LOG.info( "NoDB server listening on port {}", server.getPort() );
        return new NodbServer( server, dataSource, binaryStore );
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

        Runtime.getRuntime().addShutdownHook( new Thread( () -> {
            LOG.info( "Shutting down NoDB server" );
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
