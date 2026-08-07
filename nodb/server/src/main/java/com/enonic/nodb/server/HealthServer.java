package com.enonic.nodb.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import javax.sql.DataSource;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code /health/live} and {@code /health/ready} on the ops port (DESIGN.md §7.1).
 *
 * <p>Live = the process is up. Ready = Postgres reachable AND, when a search backend is
 * configured, OpenSearch reachable and the indexer running. That composition is the point of this
 * class for Gate A: an OpenSearch that is up but has no indexer draining its outbox honours
 * neither {@code refresh(SEARCH)} nor any read-your-writes expectation built on it, so "OpenSearch
 * responds" alone would be a probe that passes while the contract is broken.
 *
 * <p>Deliberately the JDK's own {@code com.sun.net.httpserver} rather than Jetty or a metrics
 * framework: two endpoints, no dependency, no lifecycle to reason about. Prometheus/Micrometer
 * metrics on the same port (the rest of §7.1) are Phase-6 observability work, not a Gate-A
 * prerequisite — this class exists so the OpenSearch dependency has somewhere to report, which is
 * what the gate asks for.
 */
public final class HealthServer
    implements AutoCloseable
{
    public static final int DEFAULT_PORT = 7701;

    private static final Logger LOG = LoggerFactory.getLogger( HealthServer.class );

    private final HttpServer httpServer;

    private HealthServer( HttpServer httpServer )
    {
        this.httpServer = httpServer;
    }

    /**
     * @param searchReady {@code null} when no search backend is configured, in which case search
     *                    reachability is not part of readiness at all — as opposed to being
     *                    reported as unhealthy, which would make every pre-Gate-F deployment fail
     *                    its probe.
     */
    public static HealthServer start( int port, DataSource dataSource, BooleanSupplier searchReady )
        throws IOException
    {
        return start( port, dataSource, searchReady, null );
    }

    public static HealthServer start( int port, DataSource dataSource, BooleanSupplier searchReady, SearchIndexRebuild rebuild )
        throws IOException
    {
        HttpServer server = HttpServer.create( new InetSocketAddress( port ), 0 );
        server.createContext( "/health/live", exchange -> respond( exchange, 200, "{\"status\":\"UP\"}" ) );
        server.createContext( "/health/ready", exchange -> {
            List<String> failures = new ArrayList<>();
            if ( !postgresReachable( dataSource ) )
            {
                failures.add( "postgres" );
            }
            if ( searchReady != null && !searchReady.getAsBoolean() )
            {
                failures.add( "opensearch" );
            }
            if ( failures.isEmpty() )
            {
                respond( exchange, 200, "{\"status\":\"UP\"}" );
            }
            else
            {
                respond( exchange, 503, "{\"status\":\"DOWN\",\"failed\":[\"" + String.join( "\",\"", failures ) + "\"]}" );
            }
        } );
        if ( rebuild != null )
        {
            server.createContext( "/admin/rebuild-search-index", exchange -> {
                if ( !"POST".equalsIgnoreCase( exchange.getRequestMethod() ) )
                {
                    respond( exchange, 405, "{\"error\":\"POST only\"}" );
                    return;
                }
                Map<String, String> params = queryParams( exchange.getRequestURI().getRawQuery() );
                String tenant = params.get( "tenant" );
                String repo = params.get( "repo" );
                if ( tenant == null || tenant.isBlank() || repo == null || repo.isBlank() )
                {
                    respond( exchange, 400, "{\"error\":\"required query parameters: tenant, repo\"}" );
                    return;
                }
                try
                {
                    long startedAt = System.nanoTime();
                    int replayed = rebuild.rebuild( tenant, repo );
                    long tookMillis = ( System.nanoTime() - startedAt ) / 1_000_000;
                    LOG.info( "Rebuilt search index of tenant {} repo {}: {} documents replayed in {} ms", tenant, repo, replayed,
                              tookMillis );
                    respond( exchange, 200,
                             "{\"tenant\":\"" + tenant + "\",\"repo\":\"" + repo + "\",\"replayed\":" + replayed + ",\"tookMillis\":" +
                                 tookMillis + "}" );
                }
                catch ( IllegalArgumentException | IllegalStateException e )
                {
                    respond( exchange, 409, "{\"error\":\"" + jsonEscape( e.getMessage() ) + "\"}" );
                }
                catch ( Exception e )
                {
                    LOG.warn( "Search index rebuild failed for tenant {} repo {}", tenant, repo, e );
                    respond( exchange, 500, "{\"error\":\"" + jsonEscape( e.getMessage() ) + "\"}" );
                }
            } );
        }
        server.setExecutor( null );
        server.start();
        LOG.info( "Health endpoints on port {} (/health/live, /health/ready{})", port,
                  rebuild != null ? ", /admin/rebuild-search-index" : "" );
        return new HealthServer( server );
    }

    @FunctionalInterface
    public interface SearchIndexRebuild
    {
        int rebuild( String tenantId, String repoId )
            throws Exception;
    }

    private static Map<String, String> queryParams( String rawQuery )
    {
        Map<String, String> params = new LinkedHashMap<>();
        if ( rawQuery == null || rawQuery.isBlank() )
        {
            return params;
        }
        for ( String pair : rawQuery.split( "&" ) )
        {
            int eq = pair.indexOf( '=' );
            if ( eq > 0 )
            {
                params.put( URLDecoder.decode( pair.substring( 0, eq ), StandardCharsets.UTF_8 ),
                            URLDecoder.decode( pair.substring( eq + 1 ), StandardCharsets.UTF_8 ) );
            }
        }
        return params;
    }

    private static String jsonEscape( String message )
    {
        return message == null ? "" : message.replace( "\\", "\\\\" ).replace( "\"", "\\\"" );
    }

    public int getPort()
    {
        return httpServer.getAddress().getPort();
    }

    private static boolean postgresReachable( DataSource dataSource )
    {
        try (Connection connection = dataSource.getConnection())
        {
            return connection.isValid( 2 );
        }
        catch ( Exception e )
        {
            LOG.debug( "Postgres readiness probe failed", e );
            return false;
        }
    }

    private static void respond( HttpExchange exchange, int status, String body )
        throws IOException
    {
        byte[] bytes = body.getBytes( StandardCharsets.UTF_8 );
        exchange.getResponseHeaders().add( "Content-Type", "application/json" );
        exchange.sendResponseHeaders( status, bytes.length );
        try (OutputStream out = exchange.getResponseBody())
        {
            out.write( bytes );
        }
    }

    @Override
    public void close()
    {
        httpServer.stop( 0 );
    }
}
