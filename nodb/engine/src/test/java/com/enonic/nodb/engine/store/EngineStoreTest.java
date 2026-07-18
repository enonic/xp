package com.enonic.nodb.engine.store;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.TenantProvisioner;
import com.enonic.nodb.engine.Tx;
import com.enonic.nodb.engine.model.BranchEntryRecord;
import com.enonic.nodb.engine.model.CommitRecord;
import com.enonic.nodb.engine.model.Page;
import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.model.VersionRecord;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gate 3: engine store round-trips + dual-tenant isolation. One Postgres container reused
 * across all tests in this class (per BUILD-SLICE-1.md build-environment guidance).
 */
@Testcontainers
class EngineStoreTest
{
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    private static HikariDataSource dataSource;

    private static TenantProvisioner provisioner;

    @BeforeAll
    static void setUp()
        throws SQLException
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( POSTGRES.getJdbcUrl() );
        config.setUsername( POSTGRES.getUsername() );
        config.setPassword( POSTGRES.getPassword() );
        config.setMaximumPoolSize( 8 );
        dataSource = new HikariDataSource( config );

        provisioner = new TenantProvisioner( dataSource, POSTGRES.getUsername() );
        provisioner.provision( new TenantContext( "acme" ) );
        provisioner.provision( new TenantContext( "fisk" ) );
    }

    @AfterAll
    static void tearDown()
    {
        dataSource.close();
    }

    private static long createRepo( TenantContext tenant, String repoId )
        throws SQLException
    {
        // Repo creation is DDL (partition CREATE TABLE); tenant roles deliberately have no
        // DDL rights, so this runs schema-scoped under the service role (see Tx.inTenantSchema).
        return Tx.inTenantSchema( dataSource, tenant, connection -> RepositoryLifecycle.createRepository( connection, repoId, null ) );
    }

    @Test
    void payloadRoundTripAndDedup()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        byte[] bytes = ( "payload-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 );

        String hash1 = Tx.inTenantTx( dataSource, acme, connection -> PayloadStore.putPayload( connection, bytes ) );
        String hash2 = Tx.inTenantTx( dataSource, acme, connection -> PayloadStore.putPayload( connection, bytes ) );

        assertEquals( hash1, hash2 );
        assertTrue( hash1.startsWith( "sha256:" ) );

        byte[] roundTripped = Tx.inTenantTx( dataSource, acme, connection -> PayloadStore.getPayload( connection, hash1 ) );
        assertArrayEquals( bytes, roundTripped );

        long count = Tx.inTenantTx( dataSource, acme, connection -> {
            try (PreparedStatement statement = connection.prepareStatement( "SELECT count(*) FROM payload WHERE hash = ?" ))
            {
                statement.setString( 1, hash1 );
                try (ResultSet resultSet = statement.executeQuery())
                {
                    resultSet.next();
                    return resultSet.getLong( 1 );
                }
            }
        } );
        assertEquals( 1L, count, "same bytes stored twice must dedup to one row" );
    }

    @Test
    void versionStoreAndGetRoundTrip()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        long repoKey = createRepo( acme, "version-repo-" + UUID.randomUUID() );

        VersionRecord version = Tx.inTenantTx( dataSource, acme, connection -> {
            String dataHash = PayloadStore.putPayload( connection, "node-data".getBytes( StandardCharsets.UTF_8 ) );
            String indexHash = PayloadStore.putPayload( connection, "index-config".getBytes( StandardCharsets.UTF_8 ) );
            String aclHash = PayloadStore.putPayload( connection, "acl".getBytes( StandardCharsets.UTF_8 ) );

            VersionRecord v = new VersionRecord( UUID.randomUUID().toString(), UUID.randomUUID().toString(), "/a/b",
                                                  Instant.now(), dataHash, indexHash, aclHash, List.of( "s3-key-1", "s3-key-2" ), null,
                                                  Map.of( "foo", "bar", "baz", "qux \"quoted\"" ) );
            VersionStore.store( connection, repoKey, v );
            return v;
        } );

        VersionRecord fetched = Tx.inTenantTx( dataSource, acme, connection -> VersionStore.get( connection, version.versionId() ) );

        assertEquals( version.versionId(), fetched.versionId() );
        assertEquals( version.nodeId(), fetched.nodeId() );
        assertEquals( version.nodePath(), fetched.nodePath() );
        assertEquals( version.nodeDataHash(), fetched.nodeDataHash() );
        assertEquals( version.indexConfigHash(), fetched.indexConfigHash() );
        assertEquals( version.aclHash(), fetched.aclHash() );
        assertEquals( version.binaryKeys(), fetched.binaryKeys() );
        assertNull( fetched.commitId() );
        assertEquals( version.attributes(), fetched.attributes() );
    }

    @Test
    void branchUpsertGetByPathAndChildren()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        long repoKey = createRepo( acme, "branch-repo-" + UUID.randomUUID() );

        Tx.inTenantTx( dataSource, acme, connection -> {
            RepositoryLifecycle.createBranch( connection, repoKey, "draft" );
            return null;
        } );

        String rootVersionId = writeMinimalVersion( acme, repoKey, "/" );
        String childVersionId1 = writeMinimalVersion( acme, repoKey, "/child-a" );
        String childVersionId2 = writeMinimalVersion( acme, repoKey, "/child-b" );

        String rootNodeId = UUID.randomUUID().toString();
        String child1NodeId = UUID.randomUUID().toString();
        String child2NodeId = UUID.randomUUID().toString();

        Tx.inTenantTx( dataSource, acme, connection -> {
            BranchStore.store( connection, repoKey,
                                new BranchEntryRecord( "draft", rootNodeId, rootVersionId, "/", Instant.now() ) );
            BranchStore.store( connection, repoKey,
                                new BranchEntryRecord( "draft", child1NodeId, childVersionId1, "/child-a", Instant.now() ) );
            BranchStore.store( connection, repoKey,
                                new BranchEntryRecord( "draft", child2NodeId, childVersionId2, "/child-b", Instant.now() ) );
            return null;
        } );

        // upsert: re-store child1 with a new version id, same PK (repo_key, branch, node_id)
        String updatedVersionId = writeMinimalVersion( acme, repoKey, "/child-a" );
        Tx.inTenantTx( dataSource, acme, connection -> {
            BranchStore.store( connection, repoKey,
                                new BranchEntryRecord( "draft", child1NodeId, updatedVersionId, "/child-a", Instant.now() ) );
            return null;
        } );

        BranchEntryRecord byPath =
            Tx.inTenantTx( dataSource, acme, connection -> BranchStore.getByPath( connection, repoKey, "draft", "/child-a" ) );
        assertEquals( updatedVersionId, byPath.versionId() );
        assertEquals( child1NodeId, byPath.nodeId() );

        List<BranchEntryRecord> children = Tx.inTenantTx( dataSource, acme,
                                                           connection -> BranchStore.getChildren( connection, repoKey, "draft", "/",
                                                                                                   new Page( 0, 10 ) ) );
        assertEquals( 2, children.size() );
        assertEquals( "/child-a", children.get( 0 ).nodePath() );
        assertEquals( "/child-b", children.get( 1 ).nodePath() );

        Tx.inTenantTx( dataSource, acme, connection -> {
            BranchStore.delete( connection, repoKey, "draft", List.of( child2NodeId ) );
            return null;
        } );
        List<BranchEntryRecord> afterDelete = Tx.inTenantTx( dataSource, acme,
                                                              connection -> BranchStore.getChildren( connection, repoKey, "draft", "/",
                                                                                                      new Page( 0, 10 ) ) );
        assertEquals( 1, afterDelete.size() );
        assertEquals( "/child-a", afterDelete.get( 0 ).nodePath() );
    }

    @Test
    void commitStoreAndGetRoundTrip()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        long repoKey = createRepo( acme, "commit-repo-" + UUID.randomUUID() );

        CommitRecord commit = new CommitRecord( UUID.randomUUID().toString(), "a message", "user:system:admin", Instant.now() );
        Tx.inTenantTx( dataSource, acme, connection -> {
            CommitStore.store( connection, repoKey, commit );
            return null;
        } );

        CommitRecord fetched = Tx.inTenantTx( dataSource, acme, connection -> CommitStore.get( connection, commit.commitId() ) );
        assertEquals( commit.commitId(), fetched.commitId() );
        assertEquals( commit.message(), fetched.message() );
        assertEquals( commit.committer(), fetched.committer() );
    }

    @Test
    void existsByNodeIdIsTrueAfterStoreAndFalseOtherwise()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        long repoKey = createRepo( acme, "exists-repo-" + UUID.randomUUID() );

        String versionId = writeMinimalVersion( acme, repoKey, "/exists" );
        String nodeId = UUID.randomUUID().toString();

        boolean beforeStore =
            Tx.inTenantTx( dataSource, acme, connection -> BranchStore.existsByNodeId( connection, repoKey, "master", nodeId ) );
        assertFalse( beforeStore, "must not exist before it is ever stored" );

        Tx.inTenantTx( dataSource, acme, connection -> {
            BranchStore.store( connection, repoKey, new BranchEntryRecord( "master", nodeId, versionId, "/exists", Instant.now() ) );
            return null;
        } );

        boolean afterStore =
            Tx.inTenantTx( dataSource, acme, connection -> BranchStore.existsByNodeId( connection, repoKey, "master", nodeId ) );
        assertTrue( afterStore, "must exist once stored — a real check, not just getByNodeId() != null" );

        boolean wrongBranch =
            Tx.inTenantTx( dataSource, acme, connection -> BranchStore.existsByNodeId( connection, repoKey, "draft", nodeId ) );
        assertFalse( wrongBranch, "must not exist under a branch it was never stored into" );
    }

    @Test
    void getByNodeIdsReturnsOnlyFoundEntriesInNoParticularOrder()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        long repoKey = createRepo( acme, "multiget-repo-" + UUID.randomUUID() );

        String versionId1 = writeMinimalVersion( acme, repoKey, "/m1" );
        String versionId2 = writeMinimalVersion( acme, repoKey, "/m2" );
        String nodeId1 = UUID.randomUUID().toString();
        String nodeId2 = UUID.randomUUID().toString();
        String missingNodeId = UUID.randomUUID().toString();

        Tx.inTenantTx( dataSource, acme, connection -> {
            BranchStore.store( connection, repoKey, new BranchEntryRecord( "master", nodeId1, versionId1, "/m1", Instant.now() ) );
            BranchStore.store( connection, repoKey, new BranchEntryRecord( "master", nodeId2, versionId2, "/m2", Instant.now() ) );
            return null;
        } );

        List<BranchEntryRecord> found = Tx.inTenantTx( dataSource, acme,
                                                         connection -> BranchStore.getByNodeIds( connection, repoKey, "master",
                                                                                                  List.of( nodeId1, nodeId2,
                                                                                                           missingNodeId ) ) );
        assertEquals( 2, found.size(), "the missing id must simply be absent, not an error" );
        assertEquals( Set.of( nodeId1, nodeId2 ), found.stream().map( BranchEntryRecord::nodeId ).collect( Collectors.toSet() ) );

        List<BranchEntryRecord> empty =
            Tx.inTenantTx( dataSource, acme, connection -> BranchStore.getByNodeIds( connection, repoKey, "master", List.of() ) );
        assertTrue( empty.isEmpty() );
    }

    @Test
    void getBranchesWithNodeReturnsAllBranchesContainingIt()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        long repoKey = createRepo( acme, "branches-with-node-repo-" + UUID.randomUUID() );
        String versionId = writeMinimalVersion( acme, repoKey, "/shared" );
        String nodeId = UUID.randomUUID().toString();

        Tx.inTenantTx( dataSource, acme, connection -> {
            RepositoryLifecycle.createBranch( connection, repoKey, "draft" );
            return null;
        } );
        Tx.inTenantTx( dataSource, acme, connection -> {
            // "review" is never explicitly created — BranchStore.store auto-vivifies it.
            BranchStore.store( connection, repoKey, new BranchEntryRecord( "draft", nodeId, versionId, "/shared", Instant.now() ) );
            BranchStore.store( connection, repoKey, new BranchEntryRecord( "review", nodeId, versionId, "/shared", Instant.now() ) );
            return null;
        } );

        List<String> branches =
            Tx.inTenantTx( dataSource, acme, connection -> BranchStore.getBranchesWithNode( connection, repoKey, nodeId ) );
        assertEquals( Set.of( "draft", "review" ), Set.copyOf( branches ) );

        List<String> noneForUnknownNode = Tx.inTenantTx( dataSource, acme,
                                                           connection -> BranchStore.getBranchesWithNode( connection, repoKey,
                                                                                                           UUID.randomUUID().toString() ) );
        assertTrue( noneForUnknownNode.isEmpty() );
    }

    @Test
    void storeIntoNeverSeenBranchAutoVivifiesTheBranchRow()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        long repoKey = createRepo( acme, "auto-vivify-repo-" + UUID.randomUUID() );
        String versionId = writeMinimalVersion( acme, repoKey, "/x" );
        String nodeId = UUID.randomUUID().toString();

        // No RepositoryLifecycle.createBranch call for "never-seen" — branch_entry's FK to
        // `branch` would otherwise reject this write (BUILD-PHASE-1.md's Gate 0 finding).
        Tx.inTenantTx( dataSource, acme, connection -> {
            BranchStore.store( connection, repoKey, new BranchEntryRecord( "never-seen", nodeId, versionId, "/x", Instant.now() ) );
            return null;
        } );

        BranchEntryRecord fetched =
            Tx.inTenantTx( dataSource, acme, connection -> BranchStore.getByNodeId( connection, repoKey, "never-seen", nodeId ) );
        assertNotNull( fetched, "first write to an unseen branch must succeed without a prior explicit branch-create call" );
        assertEquals( "never-seen", fetched.branch() );
    }

    @Test
    void versionDeleteRemovesTheRow()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        long repoKey = createRepo( acme, "version-delete-repo-" + UUID.randomUUID() );
        String versionId = writeMinimalVersion( acme, repoKey, "/to-delete" );

        assertNotNull( Tx.inTenantTx( dataSource, acme, connection -> VersionStore.get( connection, versionId ) ) );

        Tx.inTenantTx( dataSource, acme, connection -> {
            VersionStore.delete( connection, versionId );
            return null;
        } );

        assertNull( Tx.inTenantTx( dataSource, acme, connection -> VersionStore.get( connection, versionId ) ),
                    "version must be gone after delete" );

        // deleting an already-absent version id is a no-op, not an error.
        Tx.inTenantTx( dataSource, acme, connection -> {
            VersionStore.delete( connection, versionId );
            return null;
        } );
    }

    @Test
    void repositoryExistsReflectsCreateAndDeleteLifecycle()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        String repoId = "exists-lifecycle-repo-" + UUID.randomUUID();

        boolean beforeCreate =
            Tx.inTenantTx( dataSource, acme, connection -> RepositoryLifecycle.repositoryExists( connection, repoId ) );
        assertFalse( beforeCreate );

        createRepo( acme, repoId );
        boolean afterCreate =
            Tx.inTenantTx( dataSource, acme, connection -> RepositoryLifecycle.repositoryExists( connection, repoId ) );
        assertTrue( afterCreate );

        Tx.inTenantSchema( dataSource, acme, connection -> {
            RepositoryLifecycle.deleteRepository( connection, new RepoRef( repoId ) );
            return null;
        } );
        boolean afterDelete =
            Tx.inTenantTx( dataSource, acme, connection -> RepositoryLifecycle.repositoryExists( connection, repoId ) );
        assertFalse( afterDelete );
    }

    @Test
    void resolvingAnUnknownRepoIdThrowsUnknownRepoExceptionNotAGenericSqlException()
    {
        TenantContext acme = new TenantContext( "acme" );
        RepoRef unknownRepo = new RepoRef( "never-created-" + UUID.randomUUID() );

        SQLException thrown = assertThrows( SQLException.class,
                                             () -> Tx.inTenantTx( dataSource, acme,
                                                                   connection -> BranchStore.getByNodeId( connection, unknownRepo,
                                                                                                           "master", "whatever" ) ) );
        assertInstanceOf( UnknownRepoException.class, thrown,
                           "must be the dedicated type, not a message-text-matched generic SQLException" );
    }

    @Test
    void dualTenantIsolation()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        TenantContext fisk = new TenantContext( "fisk" );

        String repoId = "shared-name-repo-" + UUID.randomUUID();
        long acmeRepoKey = createRepo( acme, repoId );
        long fiskRepoKey = createRepo( fisk, repoId );

        Tx.inTenantTx( dataSource, acme, connection -> {
            RepositoryLifecycle.createBranch( connection, acmeRepoKey, "master" );
            return null;
        } );

        String versionId = writeMinimalVersion( acme, acmeRepoKey, "/secret" );
        String nodeId = UUID.randomUUID().toString();
        Tx.inTenantTx( dataSource, acme, connection -> {
            BranchStore.store( connection, acmeRepoKey,
                                new BranchEntryRecord( "master", nodeId, versionId, "/secret", Instant.now() ) );
            return null;
        } );

        // fisk has its own repo_key for the "same" external repoId (different schema entirely);
        // querying fisk's repo_key must never see acme's row, and version_id lookups (which
        // scan node_version without a repo_key predicate) must not cross the schema boundary.
        BranchEntryRecord seenFromFisk = Tx.inTenantTx( dataSource, fisk,
                                                         connection -> BranchStore.getByPath( connection, fiskRepoKey, "master",
                                                                                               "/secret" ) );
        assertNull( seenFromFisk, "tenant fisk must not see data written under tenant acme" );

        VersionRecord versionSeenFromFisk = Tx.inTenantTx( dataSource, fisk, connection -> VersionStore.get( connection, versionId ) );
        assertNull( versionSeenFromFisk, "tenant fisk must not see a version written under tenant acme" );

        boolean existsSeenFromFisk =
            Tx.inTenantTx( dataSource, fisk, connection -> BranchStore.existsByNodeId( connection, fiskRepoKey, "master", nodeId ) );
        assertFalse( existsSeenFromFisk, "tenant fisk must not see acme's node_id exist under its own (differently-keyed) repo" );

        List<String> branchesSeenFromFisk =
            Tx.inTenantTx( dataSource, fisk, connection -> BranchStore.getBranchesWithNode( connection, fiskRepoKey, nodeId ) );
        assertTrue( branchesSeenFromFisk.isEmpty(), "tenant fisk must see no branches for acme's node_id" );

        boolean repoExistsIsPerTenantSchemaNotGlobal =
            Tx.inTenantTx( dataSource, fisk, connection -> RepositoryLifecycle.repositoryExists( connection, repoId ) );
        assertTrue( repoExistsIsPerTenantSchemaNotGlobal, "fisk has its OWN repo row with this repo_id — existence is per-tenant-schema" );
    }

    private static String writeMinimalVersion( TenantContext tenant, long repoKey, String nodePath )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, tenant, connection -> {
            String dataHash = PayloadStore.putPayload( connection, ( "data-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 ) );
            String indexHash =
                PayloadStore.putPayload( connection, ( "index-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 ) );
            String aclHash = PayloadStore.putPayload( connection, ( "acl-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 ) );

            VersionRecord version = new VersionRecord( UUID.randomUUID().toString(), UUID.randomUUID().toString(), nodePath,
                                                        Instant.now(), dataHash, indexHash, aclHash, List.of(), null, Map.of() );
            VersionStore.store( connection, repoKey, version );
            return version.versionId();
        } );
    }
}
