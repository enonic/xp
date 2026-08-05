package com.enonic.nodb.engine.store;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import com.enonic.nodb.engine.model.VersionQuery;
import com.enonic.nodb.engine.model.VersionQueryResult;
import com.enonic.nodb.engine.model.VersionRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 3.5 Gate A: the storage-index query family on SQL — branch diff (correctness
 * matrix + a naive-Java-oracle comparison, the work order's decision-5 requirement),
 * version history (ordering, keyset, count-only, blob-key terms), active versions and
 * commit queries — with dual-repo and dual-tenant isolation asserted per read path.
 * Follows {@link EngineStoreTest}'s patterns: one Postgres container for the whole class.
 */
@Testcontainers
class StorageQueryStoreTest
{
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    private static final TenantContext ACME = new TenantContext( "acme" );

    private static final TenantContext FISK = new TenantContext( "fisk" );

    private static final String DRAFT = "draft";

    private static final String MASTER = "master";

    private static HikariDataSource dataSource;

    private static long diffRepoKey;

    /** The diff corpus, kept fully in Java as seeded — the naive oracle's ground truth. */
    private static final List<BranchEntryRecord> DRAFT_ROWS = new ArrayList<>();

    private static final List<BranchEntryRecord> MASTER_ROWS = new ArrayList<>();

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

        TenantProvisioner provisioner = new TenantProvisioner( dataSource, POSTGRES.getUsername() );
        provisioner.provision( ACME );
        provisioner.provision( FISK );

        diffRepoKey = createRepo( ACME, "diff-corpus-repo" );
        seedDiffCorpus();
    }

    @AfterAll
    static void tearDown()
    {
        dataSource.close();
    }

    /**
     * The corpus enumerates every pinned diff case (nodb/BUILD-PHASE-3.5.md Gate 0):
     * only-in-source, only-in-target, both-with-different-versions, both-same, renames
     * crossing the scope boundary in each direction, a case-only path, and a node at the
     * scope root itself.
     */
    private static void seedDiffCorpus()
        throws SQLException
    {
        seedEntry( DRAFT, "n-content", "v-content-d", "/content" );
        seedEntry( MASTER, "n-content", "v-content-m", "/content" );
        seedEntry( DRAFT, "n-src-only", "v-src-only", "/content/src-only" );
        seedEntry( MASTER, "n-tgt-only", "v-tgt-only", "/content/tgt-only" );
        seedEntry( DRAFT, "n-both-diff", "v-bd-d", "/content/both-diff" );
        seedEntry( MASTER, "n-both-diff", "v-bd-m", "/content/both-diff" );
        seedEntry( DRAFT, "n-both-same", "v-bs", "/content/both-same" );
        seedEntry( MASTER, "n-both-same", "v-bs", "/content/both-same" );
        seedEntry( DRAFT, "n-rename-in", "v-ri-d", "/content/inside/renamed" );
        seedEntry( MASTER, "n-rename-in", "v-ri-m", "/outside/renamed" );
        seedEntry( DRAFT, "n-rename-out", "v-ro-d", "/outside/renamed2" );
        seedEntry( MASTER, "n-rename-out", "v-ro-m", "/content/inside/renamed2" );
        seedEntry( DRAFT, "n-case", "v-case", "/Content/CasePath" );
    }

    private static void seedEntry( String branch, String nodeId, String versionId, String nodePath )
        throws SQLException
    {
        BranchEntryRecord entry = storeEntry( ACME, diffRepoKey, branch, nodeId, versionId, nodePath );
        ( DRAFT.equals( branch ) ? DRAFT_ROWS : MASTER_ROWS ).add( entry );
    }

    // ---- diff: oracle + matrix ------------------------------------------------------------

    @Test
    void diffMatchesNaiveOracleAcrossScopesExcludesAndBothDirections()
        throws SQLException
    {
        List<String> scopes = Arrays.asList( null, "/content", "/CONTENT", "/content/inside", "/outside", "/content/both-diff",
                                              "/Content/CasePath", "/nowhere" );
        List<List<String>> excludeSets =
            List.of( List.of(), List.of( "/content" ), List.of( "/CONTENT" ), List.of( "/content/src-only", "/outside/renamed" ) );

        for ( String scope : scopes )
        {
            for ( List<String> excludes : excludeSets )
            {
                for ( boolean draftToMaster : new boolean[]{true, false} )
                {
                    String source = draftToMaster ? DRAFT : MASTER;
                    String target = draftToMaster ? MASTER : DRAFT;
                    List<BranchEntryRecord> sourceRows = draftToMaster ? DRAFT_ROWS : MASTER_ROWS;
                    List<BranchEntryRecord> targetRows = draftToMaster ? MASTER_ROWS : DRAFT_ROWS;

                    Set<String> expected = naiveDiff( sourceRows, targetRows, scope, excludes );
                    List<String> actual = Tx.inTenantTx( dataSource, ACME,
                                                          connection -> BranchStore.diffBranches( connection, diffRepoKey, source,
                                                                                                   target, scope, excludes, 0 ) );
                    assertEquals( expected, Set.copyOf( actual ),
                                  "scope=" + scope + " excludes=" + excludes + " " + source + "->" + target );
                    assertEquals( actual.size(), Set.copyOf( actual ).size(), "result must contain DISTINCT node ids" );
                }
            }
        }
    }

    /**
     * Trivially-correct reference implementation of the pinned diff semantics, computed
     * over the fully-loaded branch contents: per-side scope/excludes (each row judged by
     * its OWN branch's path), case-insensitive comparison, scope root included, exact-path
     * excludes, distinct ids.
     */
    private static Set<String> naiveDiff( List<BranchEntryRecord> sourceRows, List<BranchEntryRecord> targetRows, String scope,
                                           List<String> excludes )
    {
        Map<String, BranchEntryRecord> targetByNodeId = new HashMap<>();
        targetRows.forEach( row -> targetByNodeId.put( row.nodeId(), row ) );
        Map<String, BranchEntryRecord> sourceByNodeId = new HashMap<>();
        sourceRows.forEach( row -> sourceByNodeId.put( row.nodeId(), row ) );

        Set<String> result = new HashSet<>();
        for ( BranchEntryRecord row : sourceRows )
        {
            BranchEntryRecord other = targetByNodeId.get( row.nodeId() );
            if ( ( other == null || !other.versionId().equals( row.versionId() ) ) && sideMatches( row.nodePath(), scope, excludes ) )
            {
                result.add( row.nodeId() );
            }
        }
        for ( BranchEntryRecord row : targetRows )
        {
            BranchEntryRecord other = sourceByNodeId.get( row.nodeId() );
            if ( ( other == null || !other.versionId().equals( row.versionId() ) ) && sideMatches( row.nodePath(), scope, excludes ) )
            {
                result.add( row.nodeId() );
            }
        }
        return result;
    }

    private static boolean sideMatches( String nodePath, String scope, List<String> excludes )
    {
        String path = nodePath.toLowerCase();
        if ( scope != null )
        {
            String loweredScope = scope.toLowerCase();
            if ( !path.equals( loweredScope ) && !path.startsWith( loweredScope + "/" ) )
            {
                return false;
            }
        }
        return excludes.stream().noneMatch( exclude -> exclude.toLowerCase().equals( path ) );
    }

    @Test
    void diffPinnedSemanticsSpotChecks()
        throws SQLException
    {
        // Whole branch (no scope): every differing node exactly once, both-same absent.
        List<String> wholeBranch = diff( null, List.of() );
        assertEquals( Set.of( "n-content", "n-src-only", "n-tgt-only", "n-both-diff", "n-rename-in", "n-rename-out", "n-case" ),
                      Set.copyOf( wholeBranch ) );
        assertEquals( 1, wholeBranch.stream().filter( "n-both-diff"::equals ).count(),
                      "present in both branches with different versions must dedup to ONE id" );

        // Scope root itself is included.
        assertEquals( Set.of( "n-both-diff" ), Set.copyOf( diff( "/content/both-diff", List.of() ) ) );

        // Renames evaluate per side: whichever branch's own path is inside the scope wins.
        assertEquals( Set.of( "n-rename-in", "n-rename-out" ), Set.copyOf( diff( "/content/inside", List.of() ) ),
                      "n-rename-in via its source path, n-rename-out via its target path" );
        assertEquals( Set.of( "n-rename-in", "n-rename-out" ), Set.copyOf( diff( "/outside", List.of() ) ),
                      "the same pair through their opposite-side paths" );

        // Case-insensitive scope and paths.
        assertTrue( Set.copyOf( diff( "/CONTENT", List.of() ) ).contains( "n-case" ) );

        // The HasUnpublishedChildren pattern: scope = parent, excludes = [parent] — the
        // exclude removes the parent itself only, never its subtree.
        Set<String> childrenOnly = Set.copyOf( diff( "/content", List.of( "/content" ) ) );
        assertEquals( Set.of( "n-src-only", "n-tgt-only", "n-both-diff", "n-rename-in", "n-rename-out", "n-case" ), childrenOnly );
    }

    @Test
    void diffExistenceOnlyLimitProbe()
        throws SQLException
    {
        assertEquals( 1, diffLimited( "/content", List.of(), 1 ).size(), "limit 1 must return exactly one id when a diff exists" );

        long emptyDiffRepoKey = createRepo( ACME, "diff-empty-repo-" + UUID.randomUUID() );
        storeEntry( ACME, emptyDiffRepoKey, DRAFT, "n-identical", "v-identical", "/same" );
        storeEntry( ACME, emptyDiffRepoKey, MASTER, "n-identical", "v-identical", "/same" );
        List<String> empty = Tx.inTenantTx( dataSource, ACME,
                                             connection -> BranchStore.diffBranches( connection, emptyDiffRepoKey, DRAFT, MASTER, null,
                                                                                      List.of(), 1 ) );
        assertTrue( empty.isEmpty(), "identical branches must yield an empty existence probe" );
    }

    @Test
    void diffIsRepoScopedAndTenantIsolated()
        throws SQLException
    {
        // Same node id as the corpus, but in a SECOND acme repo where both branches agree:
        // its diff must be empty — the corpus repo's differing rows must not bleed in.
        long repoB = createRepo( ACME, "diff-iso-repo-" + UUID.randomUUID() );
        storeEntry( ACME, repoB, DRAFT, "n-src-only", "v-iso", "/content/src-only" );
        storeEntry( ACME, repoB, MASTER, "n-src-only", "v-iso", "/content/src-only" );
        List<String> repoBDiff = Tx.inTenantTx( dataSource, ACME,
                                                 connection -> BranchStore.diffBranches( connection, repoB, DRAFT, MASTER, null,
                                                                                          List.of(), 0 ) );
        assertTrue( repoBDiff.isEmpty(), "repo B agrees on its only node — repo A's rows with the same node id must not leak in" );
        assertTrue( diff( null, List.of() ).contains( "n-src-only" ), "repo A's own diff is unaffected by repo B" );

        // Same node id again under tenant fisk: fisk sees exactly its own single-branch row.
        long fiskRepoKey = createRepo( FISK, "diff-corpus-repo" );
        storeEntry( FISK, fiskRepoKey, DRAFT, "n-src-only", "v-fisk", "/content/src-only" );
        List<String> fiskDiff = Tx.inTenantTx( dataSource, FISK,
                                                connection -> BranchStore.diffBranches( connection, fiskRepoKey, DRAFT, MASTER, null,
                                                                                         List.of(), 0 ) );
        assertEquals( List.of( "n-src-only" ), fiskDiff, "fisk must see only its own tenant's rows" );
    }

    private static List<String> diff( String scope, List<String> excludes )
        throws SQLException
    {
        return diffLimited( scope, excludes, 0 );
    }

    private static List<String> diffLimited( String scope, List<String> excludes, int limit )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, ACME,
                               connection -> BranchStore.diffBranches( connection, diffRepoKey, DRAFT, MASTER, scope, excludes,
                                                                        limit ) );
    }

    // ---- findVersions: history, keysets, counts, blob keys ---------------------------------

    @Test
    void historyOrderingIsTsDescWithVersionIdAscTiebreaker()
        throws SQLException
    {
        long repoKey = createRepo( ACME, "hist-order-repo-" + UUID.randomUUID() );
        seedHistory( repoKey );

        VersionQueryResult all = findVersions( repoKey, historyQuery( "nh", null, -1 ) );
        assertEquals( 4, all.totalHits() );
        assertEquals( List.of( "h3", "h2a", "h2b", "h1" ), versionIds( all ),
                      "ts DESC with version_id ASC as the equal-ts tiebreaker; the other node's versions excluded" );
    }

    @Test
    void historyKeysetCursorContinuesWithoutOverlapOrSkip()
        throws SQLException
    {
        long repoKey = createRepo( ACME, "hist-cursor-repo-" + UUID.randomUUID() );
        seedHistory( repoKey );

        VersionQueryResult page1 = findVersions( repoKey, historyQuery( "nh", null, 2 ) );
        assertEquals( 4, page1.totalHits(), "totalHits must be accurate regardless of page size" );
        assertEquals( List.of( "h3", "h2a" ), versionIds( page1 ) );

        VersionRecord last = page1.versions().get( 1 );
        VersionQueryResult page2 =
            findVersions( repoKey, historyQuery( "nh", new VersionQuery.Cursor( last.timestamp(), last.versionId() ), -1 ) );
        assertEquals( List.of( "h2b", "h1" ), versionIds( page2 ), "continuation strictly after (ts, version_id), incl. the equal-ts row" );
        assertEquals( 2, page2.totalHits(), "totalHits counts the query as given — cursor predicate included" );
    }

    @Test
    void countOnlyAllRowsAndWindowPastTheEnd()
        throws SQLException
    {
        long repoKey = createRepo( ACME, "hist-window-repo-" + UUID.randomUUID() );
        seedHistory( repoKey );

        VersionQueryResult countOnly = findVersions( repoKey, historyQuery( "nh", null, 0 ) );
        assertEquals( 4, countOnly.totalHits() );
        assertTrue( countOnly.versions().isEmpty(), "size 0 is count-only" );

        VersionQueryResult allRows =
            findVersions( repoKey, new VersionQuery( null, null, null, null, null, null, VersionQuery.Order.UNORDERED, 0, -1 ) );
        assertEquals( 5, allRows.totalHits(), "size -1 returns every version in the repo (4 of nh + 1 of n-other)" );
        assertEquals( 5, allRows.versions().size() );

        VersionQueryResult pastEnd = findVersions( repoKey, new VersionQuery( "nh", null, null, null, null, null,
                                                                                VersionQuery.Order.TS_DESC_ID_ASC, 10, 5 ) );
        assertTrue( pastEnd.versions().isEmpty() );
        assertEquals( 4, pastEnd.totalHits(), "a window past the end must still report the accurate total" );
    }

    @Test
    void tsFloorAndCeilingAreInclusive()
        throws SQLException
    {
        long repoKey = createRepo( ACME, "hist-bounds-repo-" + UUID.randomUUID() );
        seedHistory( repoKey );

        VersionQueryResult floor = findVersions( repoKey, new VersionQuery( "nh", Instant.ofEpochMilli( 2_000 ), null, null, null, null,
                                                                              VersionQuery.Order.TS_DESC_ID_ASC, 0, -1 ) );
        assertEquals( List.of( "h3", "h2a", "h2b" ), versionIds( floor ), "ts >= floor (RangeFilter.from parity)" );

        VersionQueryResult ceiling = findVersions( repoKey, new VersionQuery( "nh", null, Instant.ofEpochMilli( 2_000 ), null, null,
                                                                                null, VersionQuery.Order.TS_DESC_ID_ASC, 0, -1 ) );
        assertEquals( List.of( "h2a", "h2b", "h1" ), versionIds( ceiling ), "ts <= ceiling (RangeFilter.to parity)" );

        VersionQueryResult band = findVersions( repoKey, new VersionQuery( "nh", Instant.ofEpochMilli( 2_000 ),
                                                                             Instant.ofEpochMilli( 2_000 ), null, null, null,
                                                                             VersionQuery.Order.TS_DESC_ID_ASC, 0, -1 ) );
        assertEquals( List.of( "h2a", "h2b" ), versionIds( band ) );
    }

    @Test
    void versionIdAfterKeysetWithIdAscOrder()
        throws SQLException
    {
        long repoKey = createRepo( ACME, "hist-idasc-repo-" + UUID.randomUUID() );
        seedHistory( repoKey );

        VersionQueryResult afterH2a =
            findVersions( repoKey, new VersionQuery( "nh", null, null, "h2a", null, null, VersionQuery.Order.ID_ASC, 0, -1 ) );
        assertEquals( List.of( "h2b", "h3" ), versionIds( afterH2a ), "version_id > after (exclusive, RangeFilter.gt parity)" );
    }

    @Test
    void blobKeyTermsMatchBinaryKeysContainmentAndNodeDataHashEquality()
        throws SQLException
    {
        long repoKey = createRepo( ACME, "blobkey-repo-" + UUID.randomUUID() );
        writeVersion( ACME, repoKey, "bk-with", "n-bk", "/bk", Instant.ofEpochMilli( 1_000 ), List.of( "s3-bin-1", "s3-bin-2" ), null );
        writeVersion( ACME, repoKey, "bk-without", "n-bk", "/bk", Instant.ofEpochMilli( 2_000 ), List.of(), null );
        // Two versions sharing the same node-data payload: hash equality must find both.
        writeVersion( ACME, repoKey, "bk-shared-1", "n-bk2", "/bk2", Instant.ofEpochMilli( 3_000 ), List.of(), "shared-data" );
        VersionRecord shared = writeVersion( ACME, repoKey, "bk-shared-2", "n-bk2", "/bk2", Instant.ofEpochMilli( 4_000 ), List.of(),
                                              "shared-data" );

        VersionQueryResult byBinaryKey = findVersions( repoKey, new VersionQuery( null, null, null, null,
                                                                                    new VersionQuery.BlobKeyTerm( "s3-bin-1",
                                                                                                                   VersionQuery.BlobKeyField.BINARY_KEYS ),
                                                                                    null, VersionQuery.Order.UNORDERED, 0, -1 ) );
        assertEquals( List.of( "bk-with" ), versionIds( byBinaryKey ), "binary_keys array containment" );

        VersionQueryResult byDataHashCount = findVersions( repoKey, new VersionQuery( null, null, null, null,
                                                                                        new VersionQuery.BlobKeyTerm(
                                                                                            shared.nodeDataHash(),
                                                                                            VersionQuery.BlobKeyField.NODE_DATA_HASH ),
                                                                                        null, VersionQuery.Order.UNORDERED, 0, 0 ) );
        assertEquals( 2, byDataHashCount.totalHits(), "node_data_hash equality, count-only (the IsBlobUsedByVersion shape)" );
    }

    @Test
    void findVersionsIsRepoScopedAndTenantIsolated()
        throws SQLException
    {
        // The SAME version_id and node_id in TWO repos of one tenant (gate P2): each repo's
        // history contains only its own row.
        long repoA = createRepo( ACME, "fv-iso-a-" + UUID.randomUUID() );
        long repoB = createRepo( ACME, "fv-iso-b-" + UUID.randomUUID() );
        writeVersion( ACME, repoA, "v-shared", "n-shared", "/fv-a", Instant.ofEpochMilli( 1_000 ), List.of(), null );
        writeVersion( ACME, repoB, "v-shared", "n-shared", "/fv-b", Instant.ofEpochMilli( 1_000 ), List.of(), null );

        VersionQueryResult fromA = findVersions( repoA, historyQuery( "n-shared", null, -1 ) );
        assertEquals( 1, fromA.totalHits() );
        assertEquals( "/fv-a", fromA.versions().get( 0 ).nodePath(), "repo A's history must contain repo A's row only" );

        VersionQueryResult fromB = findVersions( repoB, historyQuery( "n-shared", null, -1 ) );
        assertEquals( "/fv-b", fromB.versions().get( 0 ).nodePath(), "repo B's history must contain repo B's row only" );

        long fiskRepoKey = createRepo( FISK, "fv-iso-fisk-" + UUID.randomUUID() );
        VersionQueryResult fromFisk = Tx.inTenantTx( dataSource, FISK, connection -> VersionStore.findVersions( connection, fiskRepoKey,
                                                                                                                  historyQuery(
                                                                                                                      "n-shared", null,
                                                                                                                      -1 ) ) );
        assertEquals( 0, fromFisk.totalHits(), "tenant fisk must see no versions for acme's node id" );
    }

    // ---- active versions --------------------------------------------------------------------

    @Test
    void activeVersionsReturnsBranchToVersionInOneRoundTripAndIsIsolated()
        throws SQLException
    {
        long repoKey = createRepo( ACME, "av-repo-" + UUID.randomUUID() );
        writeVersion( ACME, repoKey, "av-d", "n-av", "/av", Instant.ofEpochMilli( 2_000 ), List.of(), null );
        writeVersion( ACME, repoKey, "av-m", "n-av", "/av", Instant.ofEpochMilli( 1_000 ), List.of(), null );
        Tx.inTenantTx( dataSource, ACME, connection -> {
            BranchStore.store( connection, repoKey, new BranchEntryRecord( DRAFT, "n-av", "av-d", "/av", Instant.ofEpochMilli( 2_000 ) ) );
            BranchStore.store( connection, repoKey,
                                new BranchEntryRecord( MASTER, "n-av", "av-m", "/av", Instant.ofEpochMilli( 1_000 ) ) );
            return null;
        } );

        Map<String, VersionRecord> active = Tx.inTenantTx( dataSource, ACME,
                                                            connection -> VersionStore.getActiveVersions( connection, repoKey, "n-av",
                                                                                                            List.of( DRAFT, MASTER,
                                                                                                                     "no-such-branch" ) ) );
        assertEquals( 2, active.size(), "a branch without the node is simply absent" );
        assertEquals( "av-d", active.get( DRAFT ).versionId() );
        assertEquals( "av-m", active.get( MASTER ).versionId() );
        assertEquals( "/av", active.get( DRAFT ).nodePath(), "the full version record rides along, not just the id" );

        // Dual-repo: the same node id in another repo resolves to that repo's own version.
        long repoB = createRepo( ACME, "av-iso-repo-" + UUID.randomUUID() );
        writeVersion( ACME, repoB, "av-other", "n-av", "/av-other", Instant.ofEpochMilli( 3_000 ), List.of(), null );
        Tx.inTenantTx( dataSource, ACME, connection -> {
            BranchStore.store( connection, repoB, new BranchEntryRecord( DRAFT, "n-av", "av-other", "/av-other", Instant.ofEpochMilli( 3_000 ) ) );
            return null;
        } );
        Map<String, VersionRecord> fromRepoB = Tx.inTenantTx( dataSource, ACME,
                                                               connection -> VersionStore.getActiveVersions( connection, repoB, "n-av",
                                                                                                               List.of( DRAFT,
                                                                                                                        MASTER ) ) );
        assertEquals( Set.of( DRAFT ), fromRepoB.keySet() );
        assertEquals( "av-other", fromRepoB.get( DRAFT ).versionId() );

        // Dual-tenant: fisk sees nothing for acme's node id.
        long fiskRepoKey = createRepo( FISK, "av-repo-" + UUID.randomUUID() );
        Map<String, VersionRecord> fromFisk = Tx.inTenantTx( dataSource, FISK,
                                                              connection -> VersionStore.getActiveVersions( connection, fiskRepoKey,
                                                                                                              "n-av",
                                                                                                              List.of( DRAFT,
                                                                                                                       MASTER ) ) );
        assertTrue( fromFisk.isEmpty() );
    }

    // ---- commits ------------------------------------------------------------------------------

    @Test
    void findCommitsReturnsOnlyTheReposCommitsInDeterministicOrder()
        throws SQLException
    {
        long repoA = createRepo( ACME, "commits-a-" + UUID.randomUUID() );
        long repoB = createRepo( ACME, "commits-b-" + UUID.randomUUID() );
        storeCommit( ACME, repoA, "c-2", "second", Instant.ofEpochMilli( 2_000 ) );
        storeCommit( ACME, repoA, "c-1", "first", Instant.ofEpochMilli( 1_000 ) );
        storeCommit( ACME, repoB, "c-other", "other repo", Instant.ofEpochMilli( 1_500 ) );

        List<CommitRecord> commitsOfA = Tx.inTenantTx( dataSource, ACME, connection -> CommitStore.findByRepo( connection, repoA ) );
        assertEquals( List.of( "c-1", "c-2" ), commitsOfA.stream().map( CommitRecord::commitId ).toList(),
                      "only repo A's commits, ordered by (ts, commit_id)" );

        long fiskRepoKey = createRepo( FISK, "commits-fisk-" + UUID.randomUUID() );
        List<CommitRecord> fiskCommits =
            Tx.inTenantTx( dataSource, FISK, connection -> CommitStore.findByRepo( connection, fiskRepoKey ) );
        assertTrue( fiskCommits.isEmpty(), "a fresh fisk repo has no commits — and never acme's" );
    }

    @Test
    void commitGetIsRepoScopedWithinTenantAndIsolatedAcrossTenants()
        throws SQLException
    {
        long repoA = createRepo( ACME, "commit-get-a-" + UUID.randomUUID() );
        long repoB = createRepo( ACME, "commit-get-b-" + UUID.randomUUID() );
        String commitId = UUID.randomUUID().toString();
        storeCommit( ACME, repoA, commitId, "acme message", Instant.ofEpochMilli( 1_000 ) );

        CommitRecord fromA = Tx.inTenantTx( dataSource, ACME, connection -> CommitStore.get( connection, repoA, commitId ) );
        assertNotNull( fromA );
        assertEquals( "acme message", fromA.message() );

        assertNull( Tx.inTenantTx( dataSource, ACME, connection -> CommitStore.get( connection, repoB, commitId ) ),
                    "the Gate 0 holdout: a commit must not be addressable through another repo of the same tenant" );

        // The SAME commit id under tenant fisk is a separate row entirely.
        long fiskRepoKey = createRepo( FISK, "commit-get-fisk-" + UUID.randomUUID() );
        storeCommit( FISK, fiskRepoKey, commitId, "fisk message", Instant.ofEpochMilli( 2_000 ) );
        CommitRecord fromFisk = Tx.inTenantTx( dataSource, FISK, connection -> CommitStore.get( connection, fiskRepoKey, commitId ) );
        assertEquals( "fisk message", fromFisk.message() );
        assertEquals( "acme message",
                      Tx.inTenantTx( dataSource, ACME, connection -> CommitStore.get( connection, repoA, commitId ) ).message() );
    }

    // ---- helpers -------------------------------------------------------------------------------

    private static long createRepo( TenantContext tenant, String repoId )
        throws SQLException
    {
        return Tx.inTenantSchema( dataSource, tenant, connection -> RepositoryLifecycle.createRepository( connection, repoId, null ) );
    }

    /** Four versions of node "nh" (two sharing ts 2000 for the tiebreaker) plus one version of another node. */
    private static void seedHistory( long repoKey )
        throws SQLException
    {
        writeVersion( ACME, repoKey, "h1", "nh", "/nh", Instant.ofEpochMilli( 1_000 ), List.of(), null );
        writeVersion( ACME, repoKey, "h2a", "nh", "/nh", Instant.ofEpochMilli( 2_000 ), List.of(), null );
        writeVersion( ACME, repoKey, "h2b", "nh", "/nh", Instant.ofEpochMilli( 2_000 ), List.of(), null );
        writeVersion( ACME, repoKey, "h3", "nh", "/nh", Instant.ofEpochMilli( 3_000 ), List.of(), null );
        writeVersion( ACME, repoKey, "h-other", "n-other", "/n-other", Instant.ofEpochMilli( 2_500 ), List.of(), null );
    }

    private static VersionQuery historyQuery( String nodeId, VersionQuery.Cursor cursor, int size )
    {
        return new VersionQuery( nodeId, null, null, null, null, cursor, VersionQuery.Order.TS_DESC_ID_ASC, 0, size );
    }

    private static VersionQueryResult findVersions( long repoKey, VersionQuery query )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, ACME, connection -> VersionStore.findVersions( connection, repoKey, query ) );
    }

    private static List<String> versionIds( VersionQueryResult result )
    {
        return result.versions().stream().map( VersionRecord::versionId ).toList();
    }

    /**
     * Stores a version with explicit identity/timestamp (the corpus needs deterministic
     * values, unlike {@link EngineStoreTest}'s random helper). {@code dataSeed} non-null
     * makes the node-data payload deterministic so two versions can share one hash.
     */
    private static VersionRecord writeVersion( TenantContext tenant, long repoKey, String versionId, String nodeId, String nodePath,
                                                Instant ts, List<String> binaryKeys, String dataSeed )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, tenant, connection -> {
            String dataHash = PayloadStore.putPayload( connection, ( "data-" + ( dataSeed != null ? dataSeed : versionId ) ).getBytes(
                StandardCharsets.UTF_8 ) );
            String indexHash = PayloadStore.putPayload( connection, ( "index-" + versionId ).getBytes( StandardCharsets.UTF_8 ) );
            String aclHash = PayloadStore.putPayload( connection, ( "acl-" + versionId ).getBytes( StandardCharsets.UTF_8 ) );

            VersionRecord version =
                new VersionRecord( versionId, nodeId, nodePath, ts, dataHash, indexHash, aclHash, binaryKeys, null, Map.of() );
            VersionStore.store( connection, repoKey, version );
            return version;
        } );
    }

    /** Writes the version (unless the id was already stored for a shared-version case) and upserts the branch entry. */
    private static BranchEntryRecord storeEntry( TenantContext tenant, long repoKey, String branch, String nodeId, String versionId,
                                                  String nodePath )
        throws SQLException
    {
        if ( Tx.inTenantTx( dataSource, tenant, connection -> VersionStore.get( connection, repoKey, versionId ) ) == null )
        {
            writeVersion( tenant, repoKey, versionId, nodeId, nodePath, Instant.ofEpochMilli( 1_000 ), List.of(), null );
        }
        BranchEntryRecord entry = new BranchEntryRecord( branch, nodeId, versionId, nodePath, Instant.ofEpochMilli( 1_000 ) );
        Tx.inTenantTx( dataSource, tenant, connection -> {
            BranchStore.store( connection, repoKey, entry );
            return null;
        } );
        return entry;
    }

    private static void storeCommit( TenantContext tenant, long repoKey, String commitId, String message, Instant ts )
        throws SQLException
    {
        Tx.inTenantTx( dataSource, tenant, connection -> {
            CommitStore.store( connection, repoKey, new CommitRecord( commitId, message, "user:system:admin", ts ) );
            return null;
        } );
    }
}
