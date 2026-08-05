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
import com.enonic.nodb.engine.model.PayloadRecord;
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
    void payloadsBatchedMultiHashGetReturnsFoundOnlyMissingSimplyAbsent()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        byte[] bytes1 = ( "batch-1-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 );
        byte[] bytes2 = ( "batch-2-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 );

        String hash1 = Tx.inTenantTx( dataSource, acme, connection -> PayloadStore.putPayload( connection, bytes1 ) );
        String hash2 = Tx.inTenantTx( dataSource, acme, connection -> PayloadStore.putPayload( connection, bytes2 ) );
        String missingHash = "sha256:" + "3".repeat( 64 );

        List<PayloadRecord> found = Tx.inTenantTx( dataSource, acme,
                                                     connection -> PayloadStore.getPayloads( connection,
                                                                                              List.of( hash1, hash2, missingHash ) ) );

        assertEquals( 2, found.size(), "the missing hash must simply be absent, not an error" );
        Map<String, byte[]> byHash =
            found.stream().collect( Collectors.toMap( PayloadRecord::hash, PayloadRecord::bytes ) );
        assertArrayEquals( bytes1, byHash.get( hash1 ) );
        assertArrayEquals( bytes2, byHash.get( hash2 ) );

        List<PayloadRecord> empty = Tx.inTenantTx( dataSource, acme, connection -> PayloadStore.getPayloads( connection, List.of() ) );
        assertTrue( empty.isEmpty(), "an empty request must return an empty result without error" );
    }

    @Test
    void payloadsBatchedGetIsTenantIsolated()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        TenantContext fisk = new TenantContext( "fisk" );

        byte[] secretBytes = ( "acme-secret-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 );
        String secretHash = Tx.inTenantTx( dataSource, acme, connection -> PayloadStore.putPayload( connection, secretBytes ) );

        List<PayloadRecord> seenFromFisk =
            Tx.inTenantTx( dataSource, fisk, connection -> PayloadStore.getPayloads( connection, List.of( secretHash ) ) );
        assertTrue( seenFromFisk.isEmpty(), "tenant fisk must not see acme's payload even by exact hash" );
    }

    @Test
    void storingAVersionWithAnUnknownPayloadHashIsRejectedByTheForeignKeyAndPersistsNothing()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        long repoKey = createRepo( acme, "fk-violation-repo-" + UUID.randomUUID() );

        // Well-formed hash shape, but never stored via PayloadStore.putPayload -- exactly
        // the scenario Phase 3 Gate A's re-added FK (BUILD-PHASE-3.md #10b) must reject.
        String missingHash = "sha256:" + "0".repeat( 64 );
        VersionRecord version = new VersionRecord( UUID.randomUUID().toString(), UUID.randomUUID().toString(), "/fk-violation",
                                                     Instant.now(), missingHash, missingHash, missingHash, List.of(), null, Map.of() );

        SQLException thrown = assertThrows( SQLException.class, () -> Tx.inTenantTx( dataSource, acme, connection -> {
            VersionStore.store( connection, repoKey, version );
            return null;
        } ) );
        assertEquals( "23503", thrown.getSQLState(),
                      "must be a foreign_key_violation against payload(hash), not some other failure" );

        assertNull( Tx.inTenantTx( dataSource, acme, connection -> VersionStore.get( connection, repoKey, version.versionId() ) ),
                    "the rejected row must not have been persisted" );
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

        VersionRecord fetched = Tx.inTenantTx( dataSource, acme, connection -> VersionStore.get( connection, repoKey, version.versionId() ) );

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
        VersionRecord updatedVersion = writeMinimalVersionRecord( acme, repoKey, "/child-a" );
        String updatedVersionId = updatedVersion.versionId();
        Tx.inTenantTx( dataSource, acme, connection -> {
            BranchStore.store( connection, repoKey,
                                new BranchEntryRecord( "draft", child1NodeId, updatedVersionId, "/child-a", Instant.now() ) );
            return null;
        } );

        BranchEntryRecord byPath =
            Tx.inTenantTx( dataSource, acme, connection -> BranchStore.getByPath( connection, repoKey, "draft", "/child-a" ) );
        assertEquals( updatedVersionId, byPath.versionId() );
        assertEquals( child1NodeId, byPath.nodeId() );
        // Phase 1 Gate C N+1 fix: getByPath's JOINED_SELECT must recover the node_version
        // hash columns in the same query -- no separate GetVersion call needed by callers.
        assertEquals( updatedVersion.nodeDataHash(), byPath.nodeDataHash() );
        assertEquals( updatedVersion.indexConfigHash(), byPath.indexConfigHash() );
        assertEquals( updatedVersion.aclHash(), byPath.aclHash() );

        List<BranchEntryRecord> children = Tx.inTenantTx( dataSource, acme,
                                                           connection -> BranchStore.getChildren( connection, repoKey, "draft", "/",
                                                                                                   new Page( 0, 10 ) ) );
        assertEquals( 2, children.size() );
        assertEquals( "/child-a", children.get( 0 ).nodePath() );
        assertEquals( "/child-b", children.get( 1 ).nodePath() );
        // Same JOIN applies to getChildren.
        assertEquals( updatedVersion.nodeDataHash(), children.get( 0 ).nodeDataHash() );
        assertEquals( updatedVersion.indexConfigHash(), children.get( 0 ).indexConfigHash() );
        assertEquals( updatedVersion.aclHash(), children.get( 0 ).aclHash() );

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

        CommitRecord fetched = Tx.inTenantTx( dataSource, acme, connection -> CommitStore.get( connection, repoKey, commit.commitId() ) );
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

        VersionRecord version1 = writeMinimalVersionRecord( acme, repoKey, "/m1" );
        String versionId1 = version1.versionId();
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
        // Phase 1 Gate C N+1 fix: getByNodeIds' JOINED_SELECT must recover each entry's
        // node_version hash columns in the same query, per entry -- not just for a single get.
        BranchEntryRecord entry1 =
            found.stream().filter( e -> e.nodeId().equals( nodeId1 ) ).findFirst().orElseThrow();
        assertEquals( version1.nodeDataHash(), entry1.nodeDataHash() );
        assertEquals( version1.indexConfigHash(), entry1.indexConfigHash() );
        assertEquals( version1.aclHash(), entry1.aclHash() );

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
        VersionRecord version = writeMinimalVersionRecord( acme, repoKey, "/x" );
        String versionId = version.versionId();
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
        // Phase 1 Gate C N+1 fix: getByNodeId's JOINED_SELECT must recover the node_version
        // hash columns in the same query.
        assertEquals( version.nodeDataHash(), fetched.nodeDataHash() );
        assertEquals( version.indexConfigHash(), fetched.indexConfigHash() );
        assertEquals( version.aclHash(), fetched.aclHash() );
    }

    @Test
    void versionDeleteRemovesTheRow()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );
        long repoKey = createRepo( acme, "version-delete-repo-" + UUID.randomUUID() );
        String versionId = writeMinimalVersion( acme, repoKey, "/to-delete" );

        assertNotNull( Tx.inTenantTx( dataSource, acme, connection -> VersionStore.get( connection, repoKey, versionId ) ) );

        Tx.inTenantTx( dataSource, acme, connection -> {
            VersionStore.delete( connection, repoKey, versionId );
            return null;
        } );

        assertNull( Tx.inTenantTx( dataSource, acme, connection -> VersionStore.get( connection, repoKey, versionId ) ),
                    "version must be gone after delete" );

        // deleting an already-absent version id is a no-op, not an error.
        Tx.inTenantTx( dataSource, acme, connection -> {
            VersionStore.delete( connection, repoKey, versionId );
            return null;
        } );
    }

    @Test
    void versionIdentityIsRepoScopedWithinOneTenant()
        throws SQLException
    {
        // Phase 3.5 gate P2: version identity is (repo_key, version_id) — the SAME
        // version_id string stored in TWO repos of ONE tenant must stay independent.
        TenantContext acme = new TenantContext( "acme" );
        long repoKeyA = createRepo( acme, "p2-repo-a-" + UUID.randomUUID() );
        long repoKeyB = createRepo( acme, "p2-repo-b-" + UUID.randomUUID() );
        String sharedVersionId = UUID.randomUUID().toString();

        Tx.inTenantTx( dataSource, acme, connection -> {
            String hashA = PayloadStore.putPayload( connection, ( "p2-a-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 ) );
            VersionStore.store( connection, repoKeyA, new VersionRecord( sharedVersionId, UUID.randomUUID().toString(), "/p2-a",
                                                                           Instant.now(), hashA, hashA, hashA, List.of(), null, Map.of() ) );
            String hashB = PayloadStore.putPayload( connection, ( "p2-b-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 ) );
            VersionStore.store( connection, repoKeyB, new VersionRecord( sharedVersionId, UUID.randomUUID().toString(), "/p2-b",
                                                                           Instant.now(), hashB, hashB, hashB, List.of(), null, Map.of() ) );
            return null;
        } );

        VersionRecord fromA = Tx.inTenantTx( dataSource, acme, connection -> VersionStore.get( connection, repoKeyA, sharedVersionId ) );
        assertEquals( "/p2-a", fromA.nodePath(), "get by (repo A, version_id) must return repo A's row only" );
        VersionRecord fromB = Tx.inTenantTx( dataSource, acme, connection -> VersionStore.get( connection, repoKeyB, sharedVersionId ) );
        assertEquals( "/p2-b", fromB.nodePath(), "get by (repo B, version_id) must return repo B's row only" );

        Tx.inTenantTx( dataSource, acme, connection -> {
            VersionStore.delete( connection, repoKeyA, sharedVersionId );
            return null;
        } );

        assertNull( Tx.inTenantTx( dataSource, acme, connection -> VersionStore.get( connection, repoKeyA, sharedVersionId ) ),
                    "repo A's row must be gone after the repo-A delete" );
        VersionRecord stillInB =
            Tx.inTenantTx( dataSource, acme, connection -> VersionStore.get( connection, repoKeyB, sharedVersionId ) );
        assertNotNull( stillInB, "deleting the version in repo A must not touch repo B's row with the same version_id" );
        assertEquals( "/p2-b", stillInB.nodePath() );
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
        // querying fisk's repo_key must never see acme's row, and version lookups must not
        // cross the schema boundary.
        BranchEntryRecord seenFromFisk = Tx.inTenantTx( dataSource, fisk,
                                                         connection -> BranchStore.getByPath( connection, fiskRepoKey, "master",
                                                                                               "/secret" ) );
        assertNull( seenFromFisk, "tenant fisk must not see data written under tenant acme" );

        VersionRecord versionSeenFromFisk =
            Tx.inTenantTx( dataSource, fisk, connection -> VersionStore.get( connection, fiskRepoKey, versionId ) );
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
        return writeMinimalVersionRecord( tenant, repoKey, nodePath ).versionId();
    }

    /**
     * Same as {@link #writeMinimalVersion} but returns the full {@link VersionRecord} so
     * callers can assert the branch-entry read-side JOIN (BranchStore.JOINED_SELECT, Phase 1
     * Gate C N+1 fix) actually recovers the same hash values that were stored.
     */
    private static VersionRecord writeMinimalVersionRecord( TenantContext tenant, long repoKey, String nodePath )
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
            return version;
        } );
    }
}
