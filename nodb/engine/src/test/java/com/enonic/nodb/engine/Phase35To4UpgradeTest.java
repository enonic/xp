package com.enonic.nodb.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.enonic.nodb.engine.model.BranchEntryPage;
import com.enonic.nodb.engine.model.BranchEntryRecord;
import com.enonic.nodb.engine.model.CommitRecord;
import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.model.VersionQuery;
import com.enonic.nodb.engine.model.VersionQueryResult;
import com.enonic.nodb.engine.model.VersionRecord;
import com.enonic.nodb.engine.search.IndexFields;
import com.enonic.nodb.engine.search.Indexer;
import com.enonic.nodb.engine.search.OpenSearchClient;
import com.enonic.nodb.engine.search.OpenSearchConfig;
import com.enonic.nodb.engine.search.OutboxStore;
import com.enonic.nodb.engine.search.SearchDocument;
import com.enonic.nodb.engine.search.SearchDocumentStore;
import com.enonic.nodb.engine.search.SearchIndexAdmin;
import com.enonic.nodb.engine.search.SearchIndexNames;
import com.enonic.nodb.engine.search.SearchIndexStore;
import com.enonic.nodb.engine.store.BranchStore;
import com.enonic.nodb.engine.store.PayloadRef;
import com.enonic.nodb.engine.store.RepoKeys;
import com.enonic.nodb.engine.store.RepositoryLifecycle;
import com.enonic.nodb.engine.store.VersionStore;
import com.enonic.nodb.engine.store.WriteBatchRequest;
import com.enonic.nodb.engine.store.WriteBatchResponse;
import com.enonic.nodb.engine.store.WriteService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 5 gate P2 (nodb/BUILD-PHASE-5.md): the 3.5→4 tenant upgrade path, proven — the one
 * path Phase 4 Gate G explicitly did NOT exercise (its smoke ran on fresh volumes only).
 *
 * <p>The tenant under test is provisioned and USED at the Phase-3.5 state: migrations 001+002
 * only, no checksum rows in {@code nodb_system.tenant_migration} (the checksum table arrived
 * with Phase 4 gate P3, so a genuine 3.5-era tenant has a template_version and nothing else),
 * real content written through the real write paths ({@code WriteService.write}: repos,
 * branches, versions with binary metadata, payloads, a commit). Then the upgrade is the exact
 * operator action RUNNING.md documents — re-run provisioning — and the test proves each claim
 * of the recipe:
 * <ol>
 * <li>003 applies; 001/002 are ADOPTED as the checksum baseline (the pre-GA
 *     adopt-on-first-run rule) and 003 is recorded normally; a second run is a no-op;</li>
 * <li>the search index lifecycle works for a repo that PREDATES migration 003
 *     ({@code search_index} row + OpenSearch index/alias);</li>
 * <li><b>the wrinkle, asserted honestly:</b> a 3.5-era tenant has NO {@code search_document}
 *     rows (XP never shipped documents before Phase 4), so replay-from-documents — the ops
 *     port's rebuild — replays ZERO documents and cannot populate the index by itself. The
 *     step that populates it is XP re-shipping every node's document, i.e.
 *     {@code IndexService.reindex(initialize=true)}: walk the branch entries (the D2 SQL
 *     surface), ship each document through the normal IndexDocuments path
 *     ({@code search_document} upsert + outbox row in one transaction), let the indexer
 *     apply them. This test performs exactly that sequence at the engine level;</li>
 * <li>after the re-ship, a term query and a fulltext query answer over the pre-existing
 *     content;</li>
 * <li>the 3.5 surfaces (version history, branch listing, blob-key containment, payload FK
 *     integrity, commits) answer IDENTICALLY before and after the upgrade — 003 is additive
 *     and must not disturb them.</li>
 * </ol>
 *
 * <p>Deliberately self-contained (own containers rather than {@code SearchTestFixture}):
 * the fixture provisions tenants at the CURRENT migration head, and this test's whole point
 * is a tenant that must not start there. {@code refresh_interval: -1} for the same reason
 * the fixture states: a missing awaitRefresh must fail deterministically, not pass on a
 * timer.
 */
@Testcontainers
class Phase35To4UpgradeTest
{
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    /** Same stock image and settings as SearchTestFixture (D8: no plugins). */
    private static GenericContainer<?> opensearch;

    private static HikariDataSource dataSource;

    private static TenantProvisioner provisioner;

    private static OpenSearchClient client;

    private static SearchIndexAdmin admin;

    private static List<MigrationRunner.Migration> migrations;

    @BeforeAll
    static void setUp()
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( POSTGRES.getJdbcUrl() );
        config.setUsername( POSTGRES.getUsername() );
        config.setPassword( POSTGRES.getPassword() );
        config.setMaximumPoolSize( 8 );
        dataSource = new HikariDataSource( config );
        provisioner = new TenantProvisioner( dataSource, POSTGRES.getUsername() );
        migrations = MigrationRunner.loadMigrations();

        opensearch = new GenericContainer<>( "opensearchproject/opensearch:3.7.0" ).withExposedPorts( 9200 )
            .withEnv( "discovery.type", "single-node" )
            .withEnv( "DISABLE_SECURITY_PLUGIN", "true" )
            .withEnv( "DISABLE_INSTALL_DEMO_CONFIG", "true" )
            .withEnv( "OPENSEARCH_JAVA_OPTS", "-Xms512m -Xmx512m" )
            .waitingFor( Wait.forHttp( "/_cluster/health?wait_for_status=yellow&timeout=30s" )
                             .forPort( 9200 )
                             .forStatusCode( 200 )
                             .withStartupTimeout( Duration.ofMinutes( 4 ) ) );
        opensearch.start();
        client = new OpenSearchClient(
            OpenSearchConfig.of( "http://" + opensearch.getHost() + ":" + opensearch.getMappedPort( 9200 ) )
                .withRefreshInterval( "-1" ) );
        admin = new SearchIndexAdmin( dataSource, client );
    }

    @AfterAll
    static void tearDown()
    {
        dataSource.close();
        opensearch.stop();
    }

    @Test
    void aTenantUsedAtPhase35UpgradesInPlaceToPhase4()
        throws Exception
    {
        TenantContext tenant = new TenantContext( "p5upgrade" );
        String repoId = "upgrade.repo";

        // ------------------------------------------------------------------ the Phase-3.5 state
        // Only 001+002 applied (the TenantProvisioner test seam MigrationIntegrityTest uses),
        // then the checksum rows removed: gate P3's table did not exist when 3.5 tenants were
        // provisioned, so template_version-without-checksums IS the genuine 3.5 shape.
        provisioner.provision( tenant, migrations.subList( 0, 2 ) );
        forgetRecordedChecksums( "p5upgrade" );

        assertEquals( 2, templateVersion( "p5upgrade" ) );
        assertTrue( recordedVersions( "p5upgrade" ).isEmpty(), "a 3.5-era tenant recorded no migration checksums" );
        Set<String> tables = tablesIn( "p5upgrade" );
        assertFalse( tables.contains( "search_document" ), "search_document arrives with 003" );
        assertFalse( tables.contains( "search_index" ), "search_index arrives with 003" );
        assertTrue( tables.contains( "outbox" ), "the outbox predates 003 (001_init)" );

        // ---------------------------------------------- real content through the real write paths
        Tx.inTenantSchema( dataSource, tenant, connection -> {
            long key = RepositoryLifecycle.createRepository( connection, repoId, null );
            RepositoryLifecycle.createBranch( connection, key, "draft" );
            RepositoryLifecycle.createBranch( connection, key, "master" );
            return null;
        } );
        long repoKey = Tx.inTenantTx( dataSource, tenant, connection -> RepoKeys.resolve( connection, new RepoRef( repoId ) ) );

        Instant t0 = Instant.parse( "2026-06-01T10:00:00Z" );

        // Batch 1 (draft): three nodes; node-3 carries binary metadata.
        WriteBatchResponse batch1 = Tx.inTenantTx( dataSource, tenant, connection -> WriteService.write( connection,
            new WriteBatchRequest( new RepoRef( repoId ),
                                   payloadsFor( "v-1", "v-2a", "v-3" ),
                                   List.of( version( "v-1", "node-1", "/welcome", t0, List.of(), null ),
                                            version( "v-2a", "node-2", "/report", t0.plusSeconds( 1 ), List.of(), null ),
                                            version( "v-3", "node-3", "/product", t0.plusSeconds( 2 ),
                                                     List.of( "binary-key-p35" ), null ) ),
                                   List.of( entry( "draft", "node-1", "v-1", "/welcome", t0 ),
                                            entry( "draft", "node-2", "v-2a", "/report", t0.plusSeconds( 1 ) ),
                                            entry( "draft", "node-3", "v-3", "/product", t0.plusSeconds( 2 ) ) ),
                                   null ) ) );
        assertNotNull( batch1.outboxSeq(), "3.5-era writes emit outbox rows even with no indexer alive" );

        // Batch 2 (draft): node-2 updated — a second version, so history has depth.
        WriteBatchResponse batch2 = Tx.inTenantTx( dataSource, tenant, connection -> WriteService.write( connection,
            new WriteBatchRequest( new RepoRef( repoId ),
                                   payloadsFor( "v-2b" ),
                                   List.of( version( "v-2b", "node-2", "/report", t0.plusSeconds( 10 ), List.of(), null ) ),
                                   List.of( entry( "draft", "node-2", "v-2b", "/report", t0.plusSeconds( 10 ) ) ),
                                   null ) ) );
        assertNotNull( batch2.outboxSeq() );

        // Batch 3: node-1 published to master (same version, new branch entry) with a commit.
        WriteBatchResponse batch3 = Tx.inTenantTx( dataSource, tenant, connection -> WriteService.write( connection,
            new WriteBatchRequest( new RepoRef( repoId ), List.of(), List.of(),
                                   List.of( entry( "master", "node-1", "v-1", "/welcome", t0.plusSeconds( 20 ) ) ),
                                   new CommitRecord( "commit-p35", "publish /welcome", "user:system:su",
                                                     t0.plusSeconds( 20 ) ) ) ) );
        assertNotNull( batch3.outboxSeq() );

        // The 3.5 surfaces, captured BEFORE the upgrade — they must answer identically after it.
        List<String> historyBefore = historyVersionIds( tenant, repoKey, "node-2" );
        assertEquals( List.of( "v-2b", "v-2a" ), historyBefore, "history is ts DESC" );
        List<String> draftBefore = listedPaths( tenant, repoKey, "draft" );
        List<String> masterBefore = listedPaths( tenant, repoKey, "master" );
        assertEquals( List.of( "/product", "/report", "/welcome" ), draftBefore.stream().sorted().toList() );
        assertEquals( List.of( "/welcome" ), masterBefore );

        // ------------------------------------------------------- the upgrade: re-run provisioning
        // Exactly what a TenantBootstrapTool re-run does (RUNNING.md's recipe step 1).
        provisioner.provision( tenant );

        assertEquals( migrations.size(), templateVersion( "p5upgrade" ), "003 must be applied by the upgrade run" );
        assertEquals( allVersions(), recordedVersions( "p5upgrade" ) );
        for ( int version = 1; version <= migrations.size(); version++ )
        {
            // 001/002 via the adopt-on-first-run rule, 003 recorded on apply — all against the
            // current files, so tampering is detected from now on.
            assertEquals( migrations.get( version - 1 ).name(), recordedName( "p5upgrade", version ) );
            assertEquals( migrations.get( version - 1 ).checksum(), recordedChecksum( "p5upgrade", version ) );
        }
        tables = tablesIn( "p5upgrade" );
        assertTrue( tables.contains( "search_document" ) );
        assertTrue( tables.contains( "search_index" ) );

        // Re-running the upgrade is a no-op, not an error — the operator retry posture.
        provisioner.provision( tenant );
        assertEquals( migrations.size(), templateVersion( "p5upgrade" ) );
        assertEquals( allVersions(), recordedVersions( "p5upgrade" ) );

        // --------------------------------- search infrastructure for the PRE-EXISTING repo
        String alias = admin.createIndex( tenant, repoId );
        assertEquals( SearchIndexNames.alias( tenant, repoId ), alias );
        assertTrue( client.aliasExists( alias ), "a repo that predates 003 gets its index and alias" );
        SearchIndexStore.SearchIndexRecord live =
            Tx.inTenantTx( dataSource, tenant, connection -> SearchIndexStore.live( connection, repoKey ) );
        assertNotNull( live, "search_index must hold the LIVE generation row for the pre-existing repo" );
        assertEquals( 1, live.generation() );

        try (Indexer indexer = new Indexer( dataSource, tenant, client, admin ))
        {
            // ------------------------- THE WRINKLE: nothing to replay, and that is the honest state
            // XP never shipped documents before Phase 4, so search_document is EMPTY for 3.5-era
            // content. The ops-port rebuild (replay-from-documents) therefore CANNOT populate this
            // index — it replays zero rows. The recipe must send operators to XP's reindex instead.
            assertEquals( 0, indexer.reindexFromDocuments( repoId ),
                          "a 3.5-era tenant has no search_document rows; replay alone cannot populate the index" );
            assertEquals( 0, client.count( alias ) );

            // The 3.5-era outbox backlog must drain past harmlessly (documents absent -> skipped),
            // never wedge the checkpoint below the backlog.
            long backlog = Tx.inTenantTx( dataSource, tenant, OutboxStore::maxSeq );
            long checkpoint = indexer.awaitRefresh( backlog, List.of( repoId ), 30_000 );
            assertTrue( checkpoint >= backlog, "the checkpoint must advance past the 3.5-era backlog" );
            assertEquals( 0, client.count( alias ), "backlog rows without shipped documents apply nothing" );

            // -------------------- the documented re-ship step: XP's reindex, at the engine level
            // IndexService.reindex(initialize=true) walks branch entries (the D2 SQL surface) and
            // ships every node's document through the normal IndexDocuments path — search_document
            // upsert + outbox row in ONE transaction, then the indexer applies and refreshes.
            Map<String, String> titles =
                Map.of( "node-1", "Welcome Page", "node-2", "Quarterly Report Final", "node-3", "Product Sheet" );
            long shipped = reshipAllBranchEntries( tenant, repoKey, titles );
            indexer.awaitRefresh( shipped, List.of( repoId ), 30_000 );
        }

        // ------------------------------------------ the smoke queries answer over 3.5-era content
        assertEquals( 4, client.count( alias ), "3 draft + 1 master documents" );
        assertEquals( 2, hits( alias, term( "data.title._text", "welcome page" ) ), "node-1 on draft AND master" );
        assertEquals( 1, hits( alias, term( "data.title._text", "quarterly report final" ) ) );
        assertEquals( 0, hits( alias, term( "data.title._text", "quarterly report" ) ),
                      "the index holds the CURRENT head, not the superseded 3.5 version" );
        assertEquals( 2, hits( alias, match( "data.title._fulltext", "welcome" ) ), "fulltext answers" );
        assertEquals( 1, hits( alias, match( "data.title._fulltext", "quarterly" ) ) );
        assertEquals( 1, hits( alias, term( IndexFields.BRANCH, "master" ) ) );
        assertEquals( 3, hits( alias, term( IndexFields.BRANCH, "draft" ) ) );

        // ------------------------------- the 3.5 surfaces survived the upgrade, byte-for-byte
        assertEquals( historyBefore, historyVersionIds( tenant, repoKey, "node-2" ) );
        assertEquals( draftBefore, listedPaths( tenant, repoKey, "draft" ) );
        assertEquals( masterBefore, listedPaths( tenant, repoKey, "master" ) );

        // Binary metadata still answers through the blob-key containment surface.
        VersionQueryResult byBinary = Tx.inTenantTx( dataSource, tenant, connection -> VersionStore.findVersions( connection, repoKey,
            new VersionQuery( null, null, null, null,
                              new VersionQuery.BlobKeyTerm( "binary-key-p35", VersionQuery.BlobKeyField.BINARY_KEYS ), null,
                              VersionQuery.Order.UNORDERED, 0, -1 ) ) );
        assertEquals( 1, byBinary.totalHits() );
        assertEquals( "v-3", byBinary.versions().get( 0 ).versionId() );

        // Payload FK integrity and the commit row: nothing 003 touches, so nothing changed.
        VersionRecord v1 = Tx.inTenantTx( dataSource, tenant, connection -> VersionStore.get( connection, repoKey, "v-1" ) );
        assertEquals( sha( "data-v-1" ), v1.nodeDataHash() );
        assertEquals( 12, countRows( "p5upgrade", "payload" ), "4 versions x 3 payloads, content-addressed" );
        assertEquals( 1, countRows( "p5upgrade", "node_commit" ) );
    }

    // ------------------------------------------------------------------------------ write helpers

    /** Inline payload bytes for each version id — exactly what the version hashes are computed from. */
    private static List<PayloadRef> payloadsFor( String... versionIds )
    {
        List<PayloadRef> payloads = new ArrayList<>();
        for ( String versionId : versionIds )
        {
            payloads.add( new PayloadRef.Inline( ( "data-" + versionId ).getBytes() ) );
            payloads.add( new PayloadRef.Inline( ( "icfg-" + versionId ).getBytes() ) );
            payloads.add( new PayloadRef.Inline( ( "acl-" + versionId ).getBytes() ) );
        }
        return payloads;
    }

    private static VersionRecord version( String versionId, String nodeId, String nodePath, Instant ts, List<String> binaryKeys,
                                          String commitId )
    {
        return new VersionRecord( versionId, nodeId, nodePath, ts, sha( "data-" + versionId ), sha( "icfg-" + versionId ),
                                  sha( "acl-" + versionId ), binaryKeys, commitId, Map.of() );
    }

    private static BranchEntryRecord entry( String branch, String nodeId, String versionId, String nodePath, Instant ts )
    {
        return new BranchEntryRecord( branch, nodeId, versionId, nodePath, ts );
    }

    private static String sha( String content )
    {
        try
        {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance( "SHA-256" );
            return "sha256:" + java.util.HexFormat.of().formatHex( digest.digest( content.getBytes() ) );
        }
        catch ( java.security.NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }

    // ---------------------------------------------------------------------------- reship helpers

    /**
     * The engine-level equivalent of {@code IndexService.reindex}: walk every branch's entries
     * through the D2 listing surface, ship each node's document the way
     * {@code NodeSearchService.indexDocuments} does (store + outbox row, one transaction per
     * branch batch). Returns the highest outbox seq shipped.
     */
    private static long reshipAllBranchEntries( TenantContext tenant, long repoKey, Map<String, String> titles )
        throws SQLException
    {
        long maxSeq = 0;
        for ( String branch : List.of( "draft", "master" ) )
        {
            BranchEntryPage page = Tx.inTenantTx( dataSource, tenant,
                                                  connection -> BranchStore.listEntries( connection, repoKey, branch, null, false, null,
                                                                                          null, 1000, false ) );
            if ( page.entries().isEmpty() )
            {
                continue;
            }
            List<String> nodeIds = page.entries().stream().map( BranchEntryRecord::nodeId ).toList();
            Long seq = Tx.inTenantTx( dataSource, tenant, connection -> {
                for ( BranchEntryRecord entry : page.entries() )
                {
                    SearchDocumentStore.store( connection, repoKey, branch, document( entry, titles.get( entry.nodeId() ) ) );
                }
                return OutboxStore.appendIndex( connection, repoKey, branch, nodeIds, null );
            } );
            if ( seq != null )
            {
                maxSeq = Math.max( maxSeq, seq );
            }
        }
        assertTrue( maxSeq > 0, "the re-ship must have shipped something" );
        return maxSeq;
    }

    /** The same XP-shaped canonical document IndexerTest ships. */
    private static SearchDocument document( BranchEntryRecord entry, String title )
    {
        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        fields.put( "data.title", List.of( new SearchDocument.Value.Text( title ) ) );
        fields.put( "data.title._analyzed", List.of( new SearchDocument.Value.Text( title ) ) );
        fields.put( "data.title._orderby", List.of( new SearchDocument.Value.Text( title.toLowerCase() ) ) );
        fields.put( "_name", List.of( new SearchDocument.Value.Text( entry.nodeId() ) ) );
        fields.put( IndexFields.PERMISSIONS_READ, List.of( new SearchDocument.Value.Text( "role:system.everyone" ) ) );
        return new SearchDocument( entry.nodeId(), null, fields );
    }

    // ----------------------------------------------------------------------------- read surfaces

    private static List<String> historyVersionIds( TenantContext tenant, long repoKey, String nodeId )
        throws SQLException
    {
        VersionQueryResult result = Tx.inTenantTx( dataSource, tenant, connection -> VersionStore.findVersions( connection, repoKey,
            new VersionQuery( nodeId, null, null, null, null, null, VersionQuery.Order.TS_DESC_ID_ASC, 0, -1 ) ) );
        return result.versions().stream().map( VersionRecord::versionId ).toList();
    }

    private static List<String> listedPaths( TenantContext tenant, long repoKey, String branch )
        throws SQLException
    {
        BranchEntryPage page = Tx.inTenantTx( dataSource, tenant,
                                              connection -> BranchStore.listEntries( connection, repoKey, branch, null, false, null, null,
                                                                                      1000, true ) );
        assertEquals( page.entries().size(), page.totalHits(), "the up-front total must match the page for small repos" );
        return page.entries().stream().map( BranchEntryRecord::nodePath ).toList();
    }

    // ----------------------------------------------------------------------------- query helpers

    private static long hits( String target, ObjectNode query )
    {
        ObjectNode body = OpenSearchClient.mapper().createObjectNode();
        body.set( "query", query );
        body.put( "track_total_hits", true );
        return client.search( target, body ).path( "hits" ).path( "total" ).path( "value" ).asLong();
    }

    private static ObjectNode term( String field, String value )
    {
        ObjectNode query = OpenSearchClient.mapper().createObjectNode();
        query.putObject( "term" ).put( field, value );
        return query;
    }

    private static ObjectNode match( String field, String value )
    {
        ObjectNode query = OpenSearchClient.mapper().createObjectNode();
        query.putObject( "match" ).put( field, value );
        return query;
    }

    // ------------------------------------------------------------------------- migration bookkeeping

    private static Set<Integer> allVersions()
    {
        Set<Integer> versions = new HashSet<>();
        for ( int version = 1; version <= migrations.size(); version++ )
        {
            versions.add( version );
        }
        return versions;
    }

    private static void forgetRecordedChecksums( String tenantId )
        throws SQLException
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement( "DELETE FROM nodb_system.tenant_migration WHERE tenant_id = ?" ))
        {
            statement.setString( 1, tenantId );
            statement.executeUpdate();
        }
    }

    private static Set<Integer> recordedVersions( String tenantId )
        throws SQLException
    {
        Set<Integer> versions = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement( "SELECT version FROM nodb_system.tenant_migration WHERE tenant_id = ?" ))
        {
            statement.setString( 1, tenantId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                while ( resultSet.next() )
                {
                    versions.add( resultSet.getInt( 1 ) );
                }
            }
        }
        return versions;
    }

    private static String recordedName( String tenantId, int version )
        throws SQLException
    {
        return recordedColumn( tenantId, version, "name" );
    }

    private static String recordedChecksum( String tenantId, int version )
        throws SQLException
    {
        return recordedColumn( tenantId, version, "checksum" );
    }

    private static String recordedColumn( String tenantId, int version, String column )
        throws SQLException
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT " + column + " FROM nodb_system.tenant_migration WHERE tenant_id = ? AND version = ?" ))
        {
            statement.setString( 1, tenantId );
            statement.setInt( 2, version );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? resultSet.getString( 1 ) : null;
            }
        }
    }

    private static int templateVersion( String tenantId )
        throws SQLException
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement( "SELECT template_version FROM nodb_system.tenant WHERE tenant_id = ?" ))
        {
            statement.setString( 1, tenantId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? resultSet.getInt( 1 ) : 0;
            }
        }
    }

    private static Set<String> tablesIn( String schema )
        throws SQLException
    {
        Set<String> tables = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement( "SELECT table_name FROM information_schema.tables WHERE table_schema = ?" ))
        {
            statement.setString( 1, schema );
            try (ResultSet resultSet = statement.executeQuery())
            {
                while ( resultSet.next() )
                {
                    tables.add( resultSet.getString( 1 ) );
                }
            }
        }
        return tables;
    }

    private static long countRows( String schema, String table )
        throws SQLException
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement( "SELECT count(*) FROM " + Identifiers.quote( schema ) + "." +
                                                                            Identifiers.quote( table ) ))
        {
            try (ResultSet resultSet = statement.executeQuery())
            {
                resultSet.next();
                return resultSet.getLong( 1 );
            }
        }
    }
}
