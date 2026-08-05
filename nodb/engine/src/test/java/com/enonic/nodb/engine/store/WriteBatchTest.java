package com.enonic.nodb.engine.store;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.sql.DataSource;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gate 4: WriteBatch atomicity/NEED_PAYLOAD, branch fork, repo drop, outbox monotonicity.
 * One Postgres container reused across all tests in this class.
 */
@Testcontainers
class WriteBatchTest
{
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    private static HikariDataSource dataSource;

    private static TenantContext acme;

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

        acme = new TenantContext( "acme" );
        new TenantProvisioner( dataSource, POSTGRES.getUsername() ).provision( acme );
    }

    @AfterAll
    static void tearDown()
    {
        dataSource.close();
    }

    private static long createRepo( String repoId )
        throws SQLException
    {
        // Partition DDL requires service-role privileges (tenant roles have DML-only
        // grants), so this runs schema-scoped rather than under Tx.inTenantTx.
        return Tx.inTenantSchema( dataSource, acme, connection -> RepositoryLifecycle.createRepository( connection, repoId, null ) );
    }

    private static void createBranch( long repoKey, String branch )
        throws SQLException
    {
        Tx.inTenantTx( dataSource, acme, connection -> {
            RepositoryLifecycle.createBranch( connection, repoKey, branch );
            return null;
        } );
    }

    private static VersionRecord newVersion( String nodePath, String dataHash, String indexHash, String aclHash )
    {
        return new VersionRecord( UUID.randomUUID().toString(), UUID.randomUUID().toString(), nodePath, Instant.now(), dataHash,
                                   indexHash, aclHash, List.of(), null, Map.of() );
    }

    private static byte[] randomBytes()
    {
        return ( "content-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 );
    }

    private static String predictedHash( byte[] bytes )
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            byte[] hash = digest.digest( bytes );
            StringBuilder sb = new StringBuilder( "sha256:" );
            for ( byte b : hash )
            {
                sb.append( String.format( "%02x", b ) );
            }
            return sb.toString();
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }

    @Test
    void atomicityRollsBackEverythingOnMidBatchFailure()
        throws SQLException
    {
        long repoKey = createRepo( "atomic-repo-" + UUID.randomUUID() );
        createBranch( repoKey, "draft" );
        String repoId = repoIdFor( repoKey );

        byte[] dataBytes = randomBytes();
        byte[] indexBytes = randomBytes();
        byte[] aclBytes = randomBytes();
        String dataHash = predictedHash( dataBytes );
        String indexHash = predictedHash( indexBytes );
        String aclHash = predictedHash( aclBytes );

        String version1Id = UUID.randomUUID().toString();
        String version2Id = UUID.randomUUID().toString();
        VersionRecord version1 =
            new VersionRecord( version1Id, UUID.randomUUID().toString(), "/a", Instant.now(), dataHash, indexHash, aclHash,
                                List.of(), null, Map.of() );
        // References a payload hash that is not in the batch: node_version's FK to
        // payload(hash) forces a failure partway through the batch. (Duplicate version
        // ids no longer fail: version store is an upsert, matching the ES document
        // index's overwrite semantics — the commit flow re-stores versions with
        // commit_id set.)
        VersionRecord version2 =
            new VersionRecord( version2Id, UUID.randomUUID().toString(), "/b", Instant.now(),
                                predictedHash( randomBytes() ), indexHash, aclHash,
                                List.of(), null, Map.of() );

        BranchEntryRecord entry1 = new BranchEntryRecord( "draft", version1.nodeId(), version1.versionId(), "/a", Instant.now() );
        BranchEntryRecord entry2 = new BranchEntryRecord( "draft", version2.nodeId(), version2Id, "/b", Instant.now() );
        CommitRecord commit = new CommitRecord( UUID.randomUUID().toString(), "msg", "user:system:admin", Instant.now() );

        WriteBatchRequest request =
            new WriteBatchRequest( new RepoRef( repoId ),
                                    List.of( new PayloadRef.Inline( dataBytes ), new PayloadRef.Inline( indexBytes ),
                                             new PayloadRef.Inline( aclBytes ) ), List.of( version1, version2 ),
                                    List.of( entry1, entry2 ), commit );

        assertThrows( SQLException.class, () -> Tx.inTenantTx( dataSource, acme, connection -> WriteService.write( connection, request ) ) );

        // Whole transaction rolled back: not even the payloads (inserted before the
        // FK-violating version) or version1 (inserted successfully before version2 failed)
        // survive.
        assertEquals( 0, countWhere( "node_version", "version_id IN ('" + version1Id + "', '" + version2Id + "')" ) );
        assertEquals( 0, countWhere( "branch_entry", "node_id = '" + entry1.nodeId() + "' OR node_id = '" + entry2.nodeId() + "'" ) );
        assertEquals( 0, countWhere( "node_commit", "commit_id = '" + commit.commitId() + "'" ) );
        assertEquals( 0, countWhere( "payload", "hash IN ('" + dataHash + "', '" + indexHash + "', '" + aclHash + "')" ) );
        assertEquals( 0, countWhere( "outbox", "node_id = '" + entry1.nodeId() + "' OR node_id = '" + entry2.nodeId() + "'" ) );
    }

    @Test
    void needPayloadListsMissingHashesAndPersistsNothingThenRetrySucceeds()
        throws SQLException
    {
        long repoKey = createRepo( "needpayload-repo-" + UUID.randomUUID() );
        createBranch( repoKey, "draft" );
        String repoId = repoIdFor( repoKey );

        byte[] dataBytes = randomBytes();
        byte[] indexBytes = randomBytes();
        byte[] aclBytes = randomBytes();
        String dataHash = predictedHash( dataBytes );
        String indexHash = predictedHash( indexBytes );
        String aclHash = predictedHash( aclBytes );

        VersionRecord version = newVersion( "/x", dataHash, indexHash, aclHash );
        BranchEntryRecord entry = new BranchEntryRecord( "draft", version.nodeId(), version.versionId(), "/x", Instant.now() );

        WriteBatchRequest missingRequest =
            new WriteBatchRequest( new RepoRef( repoId ), List.of( new PayloadRef.HashOnly( dataHash ) ), List.of( version ),
                                    List.of( entry ), null );

        WriteBatchResponse missingResponse =
            Tx.inTenantTx( dataSource, acme, connection -> WriteService.write( connection, missingRequest ) );

        assertTrue( missingResponse.needsPayload() );
        assertEquals( List.of( dataHash ), missingResponse.needPayload() );
        assertNull( missingResponse.outboxSeq() );
        assertEquals( 0, countWhere( "node_version", "version_id = '" + version.versionId() + "'" ) );
        assertEquals( 0, countWhere( "branch_entry", "node_id = '" + entry.nodeId() + "'" ) );

        WriteBatchRequest retryRequest =
            new WriteBatchRequest( new RepoRef( repoId ),
                                    List.of( new PayloadRef.Inline( dataBytes ), new PayloadRef.Inline( indexBytes ),
                                             new PayloadRef.Inline( aclBytes ) ), List.of( version ), List.of( entry ), null );

        WriteBatchResponse retryResponse = Tx.inTenantTx( dataSource, acme, connection -> WriteService.write( connection, retryRequest ) );

        assertFalse( retryResponse.needsPayload() );
        assertNotNull( retryResponse.outboxSeq() );
        assertEquals( 1, countWhere( "node_version", "version_id = '" + version.versionId() + "'" ) );
        assertEquals( 1, countWhere( "branch_entry", "node_id = '" + entry.nodeId() + "'" ) );
    }

    @Test
    void hashOnlyRefsToAlreadyStoredPayloadsSucceedGateBShape()
        throws SQLException
    {
        long repoKey = createRepo( "gateb-repo-" + UUID.randomUUID() );
        createBranch( repoKey, "master" );
        String repoId = repoIdFor( repoKey );

        // Prime index-config/ACL once, mirroring real steady-state usage (BUILD-PHASE-3.md
        // Gate 0's bench, BenchRunner's sharedIndexHashes/sharedAclHashes pattern): a
        // handful of distinct index-config/ACL blobs shared across many nodes.
        byte[] indexBytes = randomBytes();
        byte[] aclBytes = randomBytes();
        String indexHash = Tx.inTenantTx( dataSource, acme, connection -> PayloadStore.putPayload( connection, indexBytes ) );
        String aclHash = Tx.inTenantTx( dataSource, acme, connection -> PayloadStore.putPayload( connection, aclBytes ) );

        // The Gate-B shape under test (BUILD-PHASE-3.md Phase 3 Gate A scope item 2): ONE
        // version + ONE branch entry, node-data inline, index-config/acl referenced by
        // hash only.
        byte[] dataBytes = randomBytes();
        String dataHash = predictedHash( dataBytes );
        VersionRecord version = newVersion( "/gate-b", dataHash, indexHash, aclHash );
        BranchEntryRecord entry = new BranchEntryRecord( "master", version.nodeId(), version.versionId(), "/gate-b", Instant.now() );

        WriteBatchRequest request =
            new WriteBatchRequest( new RepoRef( repoId ),
                                    List.of( new PayloadRef.Inline( dataBytes ), new PayloadRef.HashOnly( indexHash ),
                                             new PayloadRef.HashOnly( aclHash ) ), List.of( version ), List.of( entry ), null );

        WriteBatchResponse response = Tx.inTenantTx( dataSource, acme, connection -> WriteService.write( connection, request ) );

        assertFalse( response.needsPayload() );
        assertNotNull( response.outboxSeq() );
        assertEquals( 1, countWhere( "node_version", "version_id = '" + version.versionId() + "'" ) );
        assertEquals( 1, countWhere( "branch_entry", "node_id = '" + entry.nodeId() + "'" ) );
        // dedup: the priming inserts above are the only rows for these hashes -- the
        // hash-only refs in this batch must not have inserted duplicates.
        assertEquals( 1, countWhere( "payload", "hash = '" + indexHash + "'" ) );
        assertEquals( 1, countWhere( "payload", "hash = '" + aclHash + "'" ) );
    }

    @Test
    void branchForkCopiesEntriesSharesVersionsAndEmitsOutbox()
        throws SQLException
    {
        long repoKey = createRepo( "fork-repo-" + UUID.randomUUID() );
        createBranch( repoKey, "draft" );
        String repoId = repoIdFor( repoKey );

        List<VersionRecord> versions = new ArrayList<>();
        List<BranchEntryRecord> entries = new ArrayList<>();
        List<PayloadRef> payloads = new ArrayList<>();
        for ( int i = 0; i < 3; i++ )
        {
            byte[] dataBytes = randomBytes();
            byte[] indexBytes = randomBytes();
            byte[] aclBytes = randomBytes();
            payloads.add( new PayloadRef.Inline( dataBytes ) );
            payloads.add( new PayloadRef.Inline( indexBytes ) );
            payloads.add( new PayloadRef.Inline( aclBytes ) );
            VersionRecord v = newVersion( "/n" + i, predictedHash( dataBytes ), predictedHash( indexBytes ), predictedHash( aclBytes ) );
            versions.add( v );
            entries.add( new BranchEntryRecord( "draft", v.nodeId(), v.versionId(), "/n" + i, Instant.now() ) );
        }
        WriteBatchRequest request = new WriteBatchRequest( new RepoRef( repoId ), payloads, versions, entries, null );
        WriteBatchResponse response = Tx.inTenantTx( dataSource, acme, connection -> WriteService.write( connection, request ) );
        assertFalse( response.needsPayload() );

        long payloadCountBeforeFork = countAll( "payload" );

        Long forkSeq = Tx.inTenantTx( dataSource, acme, connection -> WriteService.forkBranch( connection, repoKey, "draft", "review" ) );
        assertNotNull( forkSeq );
        assertTrue( forkSeq > response.outboxSeq() );

        long payloadCountAfterFork = countAll( "payload" );
        assertEquals( payloadCountBeforeFork, payloadCountAfterFork, "fork must not create new payload rows" );

        List<BranchEntryRecord> reviewChildren =
            Tx.inTenantTx( dataSource, acme, connection -> BranchStore.getChildren( connection, repoKey, "review", "/", new Page( 0, 10 ) ) );
        assertEquals( 3, reviewChildren.size() );

        Set<String> draftVersionIds = entries.stream().map( BranchEntryRecord::versionId ).collect( Collectors.toSet() );
        Set<String> reviewVersionIds = reviewChildren.stream().map( BranchEntryRecord::versionId ).collect( Collectors.toSet() );
        assertEquals( draftVersionIds, reviewVersionIds, "review must share the exact same version_ids as draft" );

        long reviewOutboxCount = countWhere( "outbox", "repo_key = " + repoKey + " AND branch = 'review'" );
        assertEquals( 3, reviewOutboxCount, "fork must emit one outbox row per copied entry" );
    }

    @Test
    void repoDropRemovesPartitionsAndAllRows()
        throws SQLException
    {
        String repoId = "drop-repo-" + UUID.randomUUID();
        long repoKey = createRepo( repoId );
        createBranch( repoKey, "draft" );

        byte[] dataBytes = randomBytes();
        byte[] indexBytes = randomBytes();
        byte[] aclBytes = randomBytes();
        VersionRecord version =
            newVersion( "/x", predictedHash( dataBytes ), predictedHash( indexBytes ), predictedHash( aclBytes ) );
        BranchEntryRecord entry = new BranchEntryRecord( "draft", version.nodeId(), version.versionId(), "/x", Instant.now() );
        WriteBatchRequest request =
            new WriteBatchRequest( new RepoRef( repoId ),
                                    List.of( new PayloadRef.Inline( dataBytes ), new PayloadRef.Inline( indexBytes ),
                                             new PayloadRef.Inline( aclBytes ) ), List.of( version ), List.of( entry ), null );
        Tx.inTenantTx( dataSource, acme, connection -> WriteService.write( connection, request ) );

        Tx.inTenantSchema( dataSource, acme, connection -> {
            RepositoryLifecycle.deleteRepository( connection, new RepoRef( repoId ) );
            return null;
        } );

        assertFalse( partitionExists( "node_version_" + repoKey ) );
        assertFalse( partitionExists( "branch_entry_" + repoKey ) );
        assertEquals( 0, countWhere( "node_version", "repo_key = " + repoKey ) );
        assertEquals( 0, countWhere( "branch_entry", "repo_key = " + repoKey ) );
        assertEquals( 0, countWhere( "repository", "repo_key = " + repoKey ) );
    }

    @Test
    void outboxSeqIsMonotonicallyIncreasingAcrossBatches()
        throws SQLException
    {
        long repoKey = createRepo( "outbox-repo-" + UUID.randomUUID() );
        createBranch( repoKey, "draft" );
        String repoId = repoIdFor( repoKey );

        byte[] dataBytes1 = randomBytes();
        byte[] indexBytes1 = randomBytes();
        byte[] aclBytes1 = randomBytes();
        VersionRecord v1 =
            newVersion( "/a", predictedHash( dataBytes1 ), predictedHash( indexBytes1 ), predictedHash( aclBytes1 ) );
        BranchEntryRecord e1 = new BranchEntryRecord( "draft", v1.nodeId(), v1.versionId(), "/a", Instant.now() );
        WriteBatchRequest request1 =
            new WriteBatchRequest( new RepoRef( repoId ),
                                    List.of( new PayloadRef.Inline( dataBytes1 ), new PayloadRef.Inline( indexBytes1 ),
                                             new PayloadRef.Inline( aclBytes1 ) ), List.of( v1 ), List.of( e1 ), null );
        WriteBatchResponse response1 = Tx.inTenantTx( dataSource, acme, connection -> WriteService.write( connection, request1 ) );

        byte[] dataBytes2 = randomBytes();
        byte[] indexBytes2 = randomBytes();
        byte[] aclBytes2 = randomBytes();
        VersionRecord v2 =
            newVersion( "/b", predictedHash( dataBytes2 ), predictedHash( indexBytes2 ), predictedHash( aclBytes2 ) );
        BranchEntryRecord e2 = new BranchEntryRecord( "draft", v2.nodeId(), v2.versionId(), "/b", Instant.now() );
        WriteBatchRequest request2 =
            new WriteBatchRequest( new RepoRef( repoId ),
                                    List.of( new PayloadRef.Inline( dataBytes2 ), new PayloadRef.Inline( indexBytes2 ),
                                             new PayloadRef.Inline( aclBytes2 ) ), List.of( v2 ), List.of( e2 ), null );
        WriteBatchResponse response2 = Tx.inTenantTx( dataSource, acme, connection -> WriteService.write( connection, request2 ) );

        assertNotNull( response1.outboxSeq() );
        assertNotNull( response2.outboxSeq() );
        assertTrue( response2.outboxSeq() > response1.outboxSeq() );
    }

    private static String repoIdFor( long repoKey )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, acme, connection -> {
            try (PreparedStatement statement = connection.prepareStatement( "SELECT repo_id FROM repository WHERE repo_key = ?" ))
            {
                statement.setLong( 1, repoKey );
                try (ResultSet resultSet = statement.executeQuery())
                {
                    resultSet.next();
                    return resultSet.getString( 1 );
                }
            }
        } );
    }

    private static long countWhere( String table, String whereClause )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, acme, connection -> {
            try (var statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery( "SELECT count(*) FROM " + table + " WHERE " + whereClause ))
            {
                resultSet.next();
                return resultSet.getLong( 1 );
            }
        } );
    }

    private static long countAll( String table )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, acme, connection -> {
            try (var statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(
                "SELECT count(*) FROM " + table ))
            {
                resultSet.next();
                return resultSet.getLong( 1 );
            }
        } );
    }

    private static boolean partitionExists( String tableName )
        throws SQLException
    {
        DataSource ds = dataSource;
        try (Connection connection = ds.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement( "SELECT 1 FROM information_schema.tables WHERE table_schema = 'acme' AND table_name = ?" ))
        {
            statement.setString( 1, tableName );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        }
    }
}
