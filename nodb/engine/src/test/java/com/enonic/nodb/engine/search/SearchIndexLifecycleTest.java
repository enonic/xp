package com.enonic.nodb.engine.search;

import java.sql.SQLException;
import java.util.List;

import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.Tx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Index lifecycle round-trip (the gate's first verification item): create per repo with the ported
 * mappings, alias {@code <tenant>-<repo>} → {@code <tenant>-<repo>+g1}, exists, delete — plus
 * dual-tenant isolation by the {@code <tenant>-} prefix.
 */
class SearchIndexLifecycleTest
{
    private static HikariDataSource dataSource;

    private static OpenSearchClient client;

    private static SearchIndexAdmin admin;

    private static TenantContext acme;

    private static TenantContext fisk;

    @BeforeAll
    static void setUp()
        throws SQLException
    {
        dataSource = SearchTestFixture.dataSource();
        client = SearchTestFixture.openSearchClient();
        admin = new SearchIndexAdmin( dataSource, client );
        acme = SearchTestFixture.provisionTenant( "acme" );
        fisk = SearchTestFixture.provisionTenant( "fisk" );
    }

    @Test
    void createExistsDeleteRoundTripWithTheAliasOverGenerationOne()
        throws SQLException
    {
        String repoId = SearchTestFixture.createRepo( acme, "lifecycle" );

        assertFalse( admin.indexExists( acme, repoId ) );

        String alias = admin.createIndex( acme, repoId );
        assertEquals( "acme-" + repoId, alias );
        assertTrue( admin.indexExists( acme, repoId ) );

        // The alias resolves to the FIRST generation, and only to it.
        assertEquals( List.of( "acme-" + repoId + "+g1" ), client.indicesForAlias( alias ) );
        assertEquals( "acme-" + repoId + "+g1", admin.liveIndexName( acme, repoId ) );

        admin.deleteIndex( acme, repoId );
        assertFalse( admin.indexExists( acme, repoId ) );
        assertFalse( client.indexExists( "acme-" + repoId + "+g1" ) );
    }

    /**
     * Idempotent create: repo provisioning is retried in practice (a failed boot, a re-run
     * dev-stack), and a second create must not allocate a second generation.
     */
    @Test
    void createIsIdempotentAndDoesNotBurnAGeneration()
        throws SQLException
    {
        String repoId = SearchTestFixture.createRepo( acme, "idempotent" );
        admin.createIndex( acme, repoId );
        admin.createIndex( acme, repoId );

        assertEquals( "acme-" + repoId + "+g1", admin.liveIndexName( acme, repoId ) );
        assertEquals( 1, client.indicesForAlias( "acme-" + repoId ).size() );
    }

    /**
     * The metadata half of the lifecycle: {@code search_index} records the generation, the template
     * version and the projection version. The last of these is what makes the Gate 0(b) ACL finding
     * detectable — "which projection built this generation" must be answerable, because a document
     * missing the injected admin key does not error, it silently vanishes from admin queries.
     */
    @Test
    void theGenerationIsRecordedWithItsTemplateAndProjectionVersions()
        throws SQLException
    {
        String repoId = SearchTestFixture.createRepo( acme, "metadata" );
        admin.createIndex( acme, repoId );
        long repoKey = SearchTestFixture.repoKey( acme, repoId );

        SearchIndexStore.SearchIndexRecord live =
            Tx.inTenantTx( dataSource, acme, connection -> SearchIndexStore.live( connection, repoKey ) );

        assertNotNull( live );
        assertEquals( 1, live.generation() );
        assertEquals( "acme-" + repoId, live.aliasName() );
        assertEquals( "acme-" + repoId + "+g1", live.indexName() );
        assertEquals( IndexTemplate.load().templateVersion(), live.templateVersion() );
        assertEquals( IndexDocumentProjection.VERSION, live.projectionVersion() );
        assertEquals( SearchIndexStore.STATE_LIVE, live.state() );
    }

    /**
     * Dual-tenant isolation: the same repo id in two tenants produces two independent indices, and
     * neither tenant's enumeration sees the other's. The {@code <tenant>-} prefix is the whole
     * boundary — which works only because tenant ids contain no dash.
     */
    @Test
    void twoTenantsWithTheSameRepoIdGetIndependentIndices()
        throws SQLException
    {
        String repoId = "com.enonic.cms.default";
        Tx.inTenantSchema( dataSource, acme, connection -> com.enonic.nodb.engine.store.RepositoryLifecycle.createRepository( connection,
                                                                                                                             repoId,
                                                                                                                             null ) );
        Tx.inTenantSchema( dataSource, fisk, connection -> com.enonic.nodb.engine.store.RepositoryLifecycle.createRepository( connection,
                                                                                                                             repoId,
                                                                                                                             null ) );
        admin.createIndex( acme, repoId );
        admin.createIndex( fisk, repoId );

        assertEquals( "acme-" + repoId + "+g1", admin.liveIndexName( acme, repoId ) );
        assertEquals( "fisk-" + repoId + "+g1", admin.liveIndexName( fisk, repoId ) );

        assertTrue( admin.listTenantIndices( acme ).contains( "acme-" + repoId + "+g1" ) );
        assertFalse( admin.listTenantIndices( acme ).stream().anyMatch( name -> name.startsWith( "fisk-" ) ) );
        assertTrue( admin.listTenantIndices( fisk ).contains( "fisk-" + repoId + "+g1" ) );
        assertFalse( admin.listTenantIndices( fisk ).stream().anyMatch( name -> name.startsWith( "acme-" ) ) );

        // Deleting one tenant's index leaves the other's alone.
        admin.deleteIndex( acme, repoId );
        assertFalse( admin.indexExists( acme, repoId ) );
        assertTrue( admin.indexExists( fisk, repoId ) );
        admin.deleteIndex( fisk, repoId );
    }

    /** Gate 0(d) decision 7: the bundled plugins' system indices must never appear as tenant data. */
    @Test
    void indexEnumerationIgnoresTheBundledPluginsSystemIndices()
    {
        assertTrue( client.listIndices().stream().noneMatch( SearchIndexNames::isSystemIndex ),
                    "listIndices() must already have filtered them: " + client.listIndices() );
    }
}
