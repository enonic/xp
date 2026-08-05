package com.enonic.nodb.engine.search;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thin OpenSearch REST client over the JDK's own {@link HttpClient}, speaking JSON in and
 * JSON out.
 *
 * <p><b>Why not {@code opensearch-java} or the REST high-level client.</b> The Phase-4 wire
 * IS the JSON query DSL (BUILD-PHASE-4.md decision 1): XP renders NoQL to canonical DSL and
 * ships JSON, so the translator's job from Gate C on is JSON → JSON. A typed client would
 * force parse → builder → re-serialize on every request, drag in httpclient5, and put a
 * second, partially-overlapping query model between the wire schema and the engine —
 * precisely the "two parallel builder families" collapse this phase exists to perform. Raw
 * REST also keeps the whole surface auditable: every call below is one HTTP verb and one path.
 *
 * <p>Every method throws {@link OpenSearchException} with the engine's own response body on a
 * non-2xx, because OpenSearch's mapping errors are the actionable part. Only
 * {@link #ping()} swallows failure — it is the readiness probe, and a probe that throws is a
 * probe that crashed the health endpoint.
 */
public final class OpenSearchClient
    implements AutoCloseable
{
    private static final Logger LOG = LoggerFactory.getLogger( OpenSearchClient.class );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final OpenSearchConfig config;

    private final HttpClient httpClient;

    public OpenSearchClient( OpenSearchConfig config )
    {
        this.config = config;
        this.httpClient = HttpClient.newBuilder().connectTimeout( config.connectTimeout() ).build();
    }

    public static ObjectMapper mapper()
    {
        return MAPPER;
    }

    public OpenSearchConfig config()
    {
        return config;
    }

    // ---------------------------------------------------------------- health / readiness

    /**
     * Readiness probe: is the cluster reachable and at least YELLOW? A single-node dev
     * cluster with {@code number_of_replicas: 1} would never reach GREEN, which is exactly
     * why the template substitutes replicas per environment — but the probe still accepts
     * YELLOW so that a cloud cell with one replica temporarily unassigned reads as ready
     * rather than down.
     */
    public boolean ping()
    {
        try
        {
            JsonNode health = requestJson( "GET", "/_cluster/health", null );
            String status = health.path( "status" ).asText( "red" );
            return "green".equals( status ) || "yellow".equals( status );
        }
        catch ( RuntimeException e )
        {
            LOG.debug( "OpenSearch readiness probe failed", e );
            return false;
        }
    }

    public String clusterHealthStatus()
    {
        return requestJson( "GET", "/_cluster/health", null ).path( "status" ).asText( "red" );
    }

    // ------------------------------------------------------------------- index lifecycle

    public void createIndex( String indexName, ObjectNode body )
    {
        requestJson( "PUT", "/" + encode( indexName ), body );
    }

    public boolean indexExists( String nameOrAlias )
    {
        return status( "HEAD", "/" + encode( nameOrAlias ) ) == 200;
    }

    public boolean aliasExists( String alias )
    {
        return status( "HEAD", "/_alias/" + encode( alias ) ) == 200;
    }

    /**
     * Deletes an index (or every index behind an alias). {@code ignore_unavailable} is NOT
     * passed: a delete of something that is not there is a caller bug worth hearing about,
     * and {@link SearchIndexAdmin} already checks existence when the operation is meant to be
     * idempotent.
     */
    public void deleteIndex( String indexName )
    {
        requestJson( "DELETE", "/" + encode( indexName ), null );
    }

    /** Physical index names an alias currently resolves to. Empty when the alias is absent. */
    public List<String> indicesForAlias( String alias )
    {
        if ( status( "HEAD", "/_alias/" + encode( alias ) ) != 200 )
        {
            return List.of();
        }
        JsonNode response = requestJson( "GET", "/_alias/" + encode( alias ), null );
        List<String> names = new ArrayList<>();
        response.fieldNames().forEachRemaining( names::add );
        names.sort( String::compareTo );
        return List.copyOf( names );
    }

    /**
     * Atomic alias swap — the whole point of the generational layout. Alias actions are
     * applied in ONE cluster-state update, so a reader never sees zero indices or two behind
     * the alias mid-flip. Gate G's rebuild drill is built on this.
     */
    public void updateAliases( List<AliasAction> actions )
    {
        ArrayNode array = MAPPER.createArrayNode();
        for ( AliasAction action : actions )
        {
            ObjectNode entry = MAPPER.createObjectNode();
            ObjectNode target = entry.putObject( action.add() ? "add" : "remove" );
            target.put( "index", action.index() );
            target.put( "alias", action.alias() );
            array.add( entry );
        }
        ObjectNode body = MAPPER.createObjectNode();
        body.set( "actions", array );
        requestJson( "POST", "/_aliases", body );
    }

    /**
     * Every index in the cluster that is not cluster plumbing. Filtering system indices here
     * rather than at each call site is deliberate: OpenSearch 3.7's bundled plugins create
     * {@code .plugins-ml-config} and {@code top_queries-*} on boot, and any caller that
     * forgets is a caller whose rebuild drill deletes the ML plugin's config.
     */
    public List<String> listIndices()
    {
        JsonNode response = requestJson( "GET", "/_cat/indices?format=json&h=index&expand_wildcards=all", null );
        List<String> names = new ArrayList<>();
        for ( JsonNode row : response )
        {
            String name = row.path( "index" ).asText();
            if ( !name.isEmpty() && !SearchIndexNames.isSystemIndex( name ) )
            {
                names.add( name );
            }
        }
        names.sort( String::compareTo );
        return List.copyOf( names );
    }

    public ObjectNode getIndexSettings( String indexName )
    {
        return (ObjectNode) requestJson( "GET", "/" + encode( indexName ) + "/_settings?flat_settings=true", null );
    }

    public ObjectNode getMapping( String indexName )
    {
        return (ObjectNode) requestJson( "GET", "/" + encode( indexName ) + "/_mapping", null );
    }

    // --------------------------------------------------------------------------- indexing

    /**
     * Bulk apply. The body is NDJSON, hand-assembled by {@link BulkRequest} rather than by a
     * JSON library, because that is what the {@code _bulk} format is: newline-delimited
     * documents with no enclosing array.
     *
     * <p>Partial failure is the trap here — {@code _bulk} answers 200 with per-item errors —
     * so this method inspects {@code errors} and throws. An indexer that advanced its
     * checkpoint past a silently failed item would report read-your-writes success for a
     * document that is not in the index.
     */
    public void bulk( String ndjson )
    {
        JsonNode response = requestNdjson( "/_bulk", ndjson );
        if ( response.path( "errors" ).asBoolean( false ) )
        {
            StringBuilder failures = new StringBuilder();
            for ( JsonNode item : response.path( "items" ) )
            {
                JsonNode operation = item.elements().hasNext() ? item.elements().next() : null;
                if ( operation != null && operation.has( "error" ) )
                {
                    failures.append( operation.path( "_id" ).asText() ).append( ": " ).append( operation.path( "error" ) ).append( '\n' );
                }
            }
            throw new OpenSearchException( "Bulk request had item failures", 0, failures.toString() );
        }
    }

    /**
     * Forces the affected indices' in-flight writes to become searchable. This is the second
     * half of {@code refresh(SEARCH)}: the first half is waiting for the outbox checkpoint
     * (see {@link Indexer#awaitRefresh}). Missing indices are ignored — a repo whose index
     * has not been created yet has nothing to refresh, and that must not fail a caller's
     * read-your-writes barrier.
     */
    public void refresh( Collection<String> indices )
    {
        if ( indices.isEmpty() )
        {
            return;
        }
        String target = String.join( ",", indices );
        requestJson( "POST", "/" + encode( target ) + "/_refresh?ignore_unavailable=true", null );
    }

    public JsonNode search( String target, ObjectNode body )
    {
        return requestJson( "POST", "/" + encode( target ) + "/_search", body );
    }

    /**
     * {@code _delete_by_query}. {@code conflicts=proceed} because a concurrent reindex of the same
     * document is a version conflict, not a failure to delete: the outbox replays, and the
     * alternative (abort the whole request) would leave a branch half-deleted.
     */
    public void deleteByQuery( String indexName, ObjectNode query )
    {
        ObjectNode body = MAPPER.createObjectNode();
        body.set( "query", query );
        requestJson( "POST", "/" + encode( indexName ) + "/_delete_by_query?refresh=true&conflicts=proceed", body );
    }

    public long count( String target )
    {
        return requestJson( "GET", "/" + encode( target ) + "/_count?ignore_unavailable=true", null ).path( "count" ).asLong( 0 );
    }

    // ----------------------------------------------------------------------------- plumbing

    public JsonNode requestJson( String method, String path, ObjectNode body )
    {
        String payload = body == null ? null : body.toString();
        HttpResponse<String> response = send( method, path, payload, "application/json" );
        return parse( response );
    }

    private JsonNode requestNdjson( String path, String ndjson )
    {
        HttpResponse<String> response = send( "POST", path, ndjson, "application/x-ndjson" );
        return parse( response );
    }

    private JsonNode parse( HttpResponse<String> response )
    {
        String body = response.body() == null ? "" : response.body();
        if ( response.statusCode() / 100 != 2 )
        {
            throw new OpenSearchException( "OpenSearch request failed", response.statusCode(), body );
        }
        if ( body.isBlank() )
        {
            return MAPPER.createObjectNode();
        }
        try
        {
            return MAPPER.readTree( body );
        }
        catch ( IOException e )
        {
            throw new OpenSearchException( "Unparseable OpenSearch response", e );
        }
    }

    private int status( String method, String path )
    {
        return send( method, path, null, "application/json" ).statusCode();
    }

    private HttpResponse<String> send( String method, String path, String body, String contentType )
    {
        HttpRequest.BodyPublisher publisher =
            body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString( body, StandardCharsets.UTF_8 );

        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri( URI.create( config.baseUrl() + path ) )
            .timeout( config.requestTimeout() )
            .header( "Content-Type", contentType )
            .header( "Accept", "application/json" )
            .method( method, publisher );

        try
        {
            return httpClient.send( builder.build(), HttpResponse.BodyHandlers.ofString( StandardCharsets.UTF_8 ) );
        }
        catch ( IOException e )
        {
            throw new OpenSearchException( method + " " + path + " failed", e );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new OpenSearchException( method + " " + path + " interrupted", e );
        }
    }

    /**
     * Index and alias names are already validated by {@link SearchIndexNames} (tenant
     * {@code ^[a-z][a-z0-9]{2,30}$}, repo {@code ^[a-z0-9][a-z0-9_.-]*$}) so nothing needing
     * escaping can reach here — but {@code +} is a legal index-name character that means
     * "space" when unescaped in a URL path, and {@code +g<N>} puts one in every physical
     * name. Encoding it is not optional. Commas separate multi-index targets and must survive.
     */
    private static String encode( String name )
    {
        StringBuilder sb = new StringBuilder( name.length() + 8 );
        for ( int i = 0; i < name.length(); i++ )
        {
            char c = name.charAt( i );
            if ( c == '+' )
            {
                sb.append( "%2B" );
            }
            else if ( c == ',' || c == '?' || c == '=' || c == '&' || c == '*' || c == '_' || c == '.' || c == '-' || c == '/' ||
                Character.isLetterOrDigit( c ) )
            {
                sb.append( c );
            }
            else
            {
                sb.append( URLEncoder.encode( String.valueOf( c ), StandardCharsets.UTF_8 ) );
            }
        }
        return sb.toString();
    }

    @Override
    public void close()
    {
        httpClient.close();
    }

    /** One entry of an atomic {@code _aliases} action list. */
    public record AliasAction(boolean add, String index, String alias)
    {
        public static AliasAction add( String index, String alias )
        {
            return new AliasAction( true, index, alias );
        }

        public static AliasAction remove( String index, String alias )
        {
            return new AliasAction( false, index, alias );
        }
    }
}
