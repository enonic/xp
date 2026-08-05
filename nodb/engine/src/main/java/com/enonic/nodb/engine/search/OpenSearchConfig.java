package com.enonic.nodb.engine.search;

import java.time.Duration;

/**
 * OpenSearch connection + per-environment index knobs, env-var configured like every other
 * NoDB dependency ({@code NODB_PG_URL}, {@code NODB_S3_*}).
 *
 * <p>{@code NODB_OPENSEARCH_URL} is the switch: absent means "this NoDB has no search
 * backend", which is a legitimate state for the whole hybrid window (search still runs on
 * XP's embedded ES until Phase 4 Gate F). {@link #isConfigured()} is what {@code NodbServer}
 * keys off to decide whether to register {@code NodeSearch} at all, and what the readiness
 * probe keys off to decide whether OpenSearch reachability is part of "ready".
 *
 * <p>{@code replicas} and {@code refreshInterval} are the only two settings substituted into
 * the index template at create time; everything else is identical across environments so
 * that analysis and collation are environment-independent (a sort order that depends on
 * where you deployed is not a sort order).
 */
public record OpenSearchConfig(String url, int replicas, String refreshInterval, Duration connectTimeout, Duration requestTimeout)
{
    /**
     * Dev/cloud default. XP's search-settings.json hardcodes {@code number_of_replicas: 1},
     * which on a single-node dev or CI cluster parks every index at YELLOW forever and makes
     * a {@code wait_for_status=green} probe unusable. Cloud overrides this to 1.
     */
    public static final int DEFAULT_REPLICAS = 0;

    /**
     * OpenSearch's default, and also ES 2.4's — closest to today's observable visibility, so
     * the Gate F comparison is not perturbed by a refresh-policy change on top of an engine
     * change. The §3.3 read-your-writes contract does not depend on it: {@code awaitRefresh}
     * waits for the checkpoint and then issues an explicit refresh. This value is only an
     * upper bound on incidental staleness for readers that did not await.
     *
     * <p>TESTS SET {@code -1} (see {@code OpenSearchTestSupport}) so that any code path
     * missing an {@code awaitRefresh} fails deterministically instead of passing on a 1s
     * timer. {@code -1} must never ship.
     */
    public static final String DEFAULT_REFRESH_INTERVAL = "1s";

    public static OpenSearchConfig fromEnv()
    {
        return new OpenSearchConfig( env( "NODB_OPENSEARCH_URL", null ),
                                     Integer.parseInt( env( "NODB_OPENSEARCH_REPLICAS", Integer.toString( DEFAULT_REPLICAS ) ) ),
                                     env( "NODB_OPENSEARCH_REFRESH_INTERVAL", DEFAULT_REFRESH_INTERVAL ),
                                     Duration.ofMillis( Long.parseLong( env( "NODB_OPENSEARCH_CONNECT_TIMEOUT_MS", "5000" ) ) ),
                                     Duration.ofMillis( Long.parseLong( env( "NODB_OPENSEARCH_REQUEST_TIMEOUT_MS", "30000" ) ) ) );
    }

    /** Convenience for tests and for the default dev/cloud shape. */
    public static OpenSearchConfig of( String url )
    {
        return new OpenSearchConfig( url, DEFAULT_REPLICAS, DEFAULT_REFRESH_INTERVAL, Duration.ofSeconds( 5 ), Duration.ofSeconds( 30 ) );
    }

    public OpenSearchConfig withRefreshInterval( String interval )
    {
        return new OpenSearchConfig( url, replicas, interval, connectTimeout, requestTimeout );
    }

    public boolean isConfigured()
    {
        return url != null && !url.isBlank();
    }

    /** Trailing-slash-normalized base URL; throws when unconfigured, so misuse is loud. */
    public String baseUrl()
    {
        if ( !isConfigured() )
        {
            throw new IllegalStateException( "NODB_OPENSEARCH_URL is not configured" );
        }
        return url.endsWith( "/" ) ? url.substring( 0, url.length() - 1 ) : url;
    }

    private static String env( String name, String defaultValue )
    {
        String value = System.getenv( name );
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
