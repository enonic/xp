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

import com.enonic.nodb.server.auth.DevKeys;
import com.enonic.nodb.server.auth.JwtVerifier;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.service.NodeStoreService;
import com.enonic.nodb.server.service.RepositoryAdminService;

/**
 * The standalone {@code enonic/nodb} server binding (DESIGN.md §7): engine + gRPC, no
 * OSGi/Spring. Wires a Postgres pool, the JWT auth interceptor (trivial dev issuer's
 * public key — see {@link com.enonic.nodb.server.auth.NodbTokenTool}), and the two
 * service impls implemented this slice (NodeStore's data-plane RPCs, RepositoryAdmin's
 * management-plane RPCs). NodeSearch/ChangeFeed/BulkTransfer/Snapshots are out of scope
 * for slice 1 and are simply never registered on this server.
 *
 * <p>Configuration is env-var only (no config file yet, matching this slice's scope):
 * <ul>
 *   <li>{@code NODB_PG_URL} — JDBC URL (default {@code jdbc:postgresql://localhost:5432/nodb})
 *   <li>{@code NODB_PG_USER} / {@code NODB_PG_PASSWORD} — the pooled service role's credentials
 *   <li>{@code NODB_PORT} — gRPC port (default 7700)
 *   <li>{@code NODB_KEYS_DIR} — dev issuer keypair directory (default {@code ./.nodb-dev-keys});
 *       the server only ever reads the public key from here, never the private key
 * </ul>
 */
public final class NodbServer
{
    private static final Logger LOG = LoggerFactory.getLogger( NodbServer.class );

    private final Server server;

    private final HikariDataSource dataSource;

    NodbServer( Server server, HikariDataSource dataSource )
    {
        this.server = server;
        this.dataSource = dataSource;
    }

    public static NodbServer create( int port, HikariDataSource dataSource, RSAPublicKey issuerPublicKey )
        throws java.io.IOException
    {
        TenantAuthInterceptor authInterceptor = new TenantAuthInterceptor( new JwtVerifier( issuerPublicKey ) );

        Server server = ServerBuilder.forPort( port )
            .addService( ServerInterceptors.intercept( new NodeStoreService( dataSource ), authInterceptor ) )
            .addService( ServerInterceptors.intercept( new RepositoryAdminService( dataSource ), authInterceptor ) )
            .build()
            .start();

        LOG.info( "NoDB server listening on port {}", port );
        return new NodbServer( server, dataSource );
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
