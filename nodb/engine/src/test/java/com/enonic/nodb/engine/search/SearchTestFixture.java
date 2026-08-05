package com.enonic.nodb.engine.search;

import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.TenantProvisioner;
import com.enonic.nodb.engine.Tx;
import com.enonic.nodb.engine.store.RepositoryLifecycle;

/**
 * Shared Postgres + OpenSearch containers for the search tests.
 *
 * <p>Singleton containers started once per JVM rather than {@code @Container} per class:
 * OpenSearch takes tens of seconds to reach YELLOW, and this gate has several container-backed
 * classes. Same reuse rationale the existing {@code EngineStoreTest} states for its Postgres
 * container, extended across classes because the cost is an order of magnitude higher.
 *
 * <p><b>{@code refresh_interval: -1} in tests, and this is the point.</b> With the production 1s
 * interval, a code path that FORGETS to await a refresh still passes — it just gets lucky on a
 * timer, and the test suite silently stops testing the §3.3 contract. With refresh disabled, a
 * document is searchable only after an explicit refresh, so a missing {@code awaitRefresh} fails
 * deterministically (Gate 0(d) decision 3). Never ship {@code -1}.
 */
final class SearchTestFixture
{
    /**
     * The STOCK image (D8): NoDB computes ICU collation keys itself, so analysis-icu is needed for
     * nothing. Pinned to the minor Amazon OpenSearch Service runs — a floating tag would make the
     * mapping port's assertions depend on when the test ran.
     */
    static final String OPENSEARCH_IMAGE = "opensearchproject/opensearch:3.7.0";

    private static final AtomicInteger REPO_SEQUENCE = new AtomicInteger();

    private static PostgreSQLContainer<?> postgres;

    private static GenericContainer<?> opensearch;

    private static HikariDataSource dataSource;

    private static TenantProvisioner provisioner;

    private static OpenSearchClient client;

    private SearchTestFixture()
    {
    }

    static synchronized HikariDataSource dataSource()
    {
        if ( dataSource == null )
        {
            postgres = new PostgreSQLContainer<>( "postgres:17" );
            postgres.start();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl( postgres.getJdbcUrl() );
            config.setUsername( postgres.getUsername() );
            config.setPassword( postgres.getPassword() );
            config.setMaximumPoolSize( 8 );
            dataSource = new HikariDataSource( config );
            provisioner = new TenantProvisioner( dataSource, postgres.getUsername() );
        }
        return dataSource;
    }

    static synchronized OpenSearchClient openSearchClient()
    {
        if ( client == null )
        {
            opensearch = new GenericContainer<>( OPENSEARCH_IMAGE ).withExposedPorts( 9200 )
                .withEnv( "discovery.type", "single-node" )
                .withEnv( "DISABLE_SECURITY_PLUGIN", "true" )
                .withEnv( "DISABLE_INSTALL_DEMO_CONFIG", "true" )
                .withEnv( "OPENSEARCH_JAVA_OPTS", "-Xms512m -Xmx512m" )
                .waitingFor( Wait.forHttp( "/_cluster/health?wait_for_status=yellow&timeout=30s" )
                                 .forPort( 9200 )
                                 .forStatusCode( 200 )
                                 .withStartupTimeout( Duration.ofMinutes( 4 ) ) );
            opensearch.start();

            String url = "http://" + opensearch.getHost() + ":" + opensearch.getMappedPort( 9200 );
            client = new OpenSearchClient( OpenSearchConfig.of( url ).withRefreshInterval( "-1" ) );
        }
        return client;
    }

    static synchronized TenantContext provisionTenant( String tenantId )
        throws SQLException
    {
        dataSource();
        TenantContext tenant = new TenantContext( tenantId );
        provisioner.provision( tenant );
        return tenant;
    }

    /** A fresh repo (storage side only) with a unique id, so tests never share an index. */
    static String createRepo( TenantContext tenant, String prefix )
        throws SQLException
    {
        String repoId = prefix + "." + REPO_SEQUENCE.incrementAndGet();
        Tx.inTenantSchema( dataSource(), tenant, connection -> {
            long repoKey = RepositoryLifecycle.createRepository( connection, repoId, null );
            RepositoryLifecycle.createBranch( connection, repoKey, "master" );
            RepositoryLifecycle.createBranch( connection, repoKey, "draft" );
            return null;
        } );
        return repoId;
    }

    static long repoKey( TenantContext tenant, String repoId )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource(), tenant,
                              connection -> com.enonic.nodb.engine.store.RepoKeys.resolve( connection,
                                                                                           new com.enonic.nodb.engine.model.RepoRef(
                                                                                               repoId ) ) );
    }
}
