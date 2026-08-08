package com.enonic.nodb.engine.search;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.Tx;
import com.enonic.nodb.engine.model.BranchEntryRecord;
import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.model.VersionRecord;
import com.enonic.nodb.engine.store.PayloadRef;
import com.enonic.nodb.engine.store.WriteBatchRequest;
import com.enonic.nodb.engine.store.WriteBatchResponse;
import com.enonic.nodb.engine.store.WriteService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The DESIGN §3.3 contract, end to end: write → outbox → indexed → {@code awaitRefresh} → visible,
 * plus checkpoint monotonicity, risk 10a, rebuild-from-docs, and composite-{@code _id} uniqueness
 * across branches.
 *
 * <p><b>These tests run with {@code refresh_interval: -1}</b> (see {@link SearchTestFixture}). That
 * is what makes them meaningful: with the production 1s interval a missing {@code awaitRefresh}
 * would still pass on a timer, and the suite would silently stop testing the contract.
 */
class IndexerTest
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

    private static Indexer indexer( TenantContext tenant )
    {
        return new Indexer( dataSource, tenant, client, admin );
    }

    // ------------------------------------------------------------------ the read-your-writes path

    /**
     * The headline: a write commits an outbox seq, {@code awaitRefresh(seq)} blocks until the
     * indexer has applied it and the index has been refreshed, and only then is the document
     * searchable. With refresh disabled, the "visible" assertion is a statement about
     * {@code awaitRefresh} and nothing else.
     */
    @Test
    void writeThenOutboxThenIndexedThenAwaitRefreshThenVisible()
        throws Exception
    {
        String repoId = SearchTestFixture.createRepo( acme, "rvw" );
        admin.createIndex( acme, repoId );
        String alias = SearchIndexNames.alias( acme, repoId );

        long seq = shipDocument( acme, repoId, "master", "node-1", "Hello Brave World" );
        assertTrue( seq > 0, "the index RPC must return the seq it committed" );

        // Nothing has drained yet, so nothing is searchable.
        assertEquals( 0, hits( alias, term( "data.title._text", "hello brave world" ) ) );

        try (Indexer indexer = indexer( acme ))
        {
            long checkpoint = indexer.awaitRefresh( seq, List.of( repoId ), 30_000 );
            assertTrue( checkpoint >= seq, "checkpoint " + checkpoint + " must have reached " + seq );
        }

        assertEquals( 1, hits( alias, term( "data.title._text", "hello brave world" ) ) );
        assertEquals( 1, hits( alias, term( IndexFields.BRANCH, "master" ) ) );
        // The injected admin read key is on the document, not applied as a query-time exemption.
        assertEquals( 1, hits( alias, term( "_permissions.read._text", IndexFields.ADMIN_PRINCIPAL ) ) );
    }

    /** An update is a full replace under the same composite id — one document, new content. */
    @Test
    void reIndexingTheSameNodeReplacesItRatherThanDuplicatingIt()
        throws Exception
    {
        String repoId = SearchTestFixture.createRepo( acme, "replace" );
        admin.createIndex( acme, repoId );
        String alias = SearchIndexNames.alias( acme, repoId );

        try (Indexer indexer = indexer( acme ))
        {
            indexer.awaitRefresh( shipDocument( acme, repoId, "master", "node-1", "first" ), List.of( repoId ), 30_000 );
            assertEquals( 1, hits( alias, term( "data.title._text", "first" ) ) );

            indexer.awaitRefresh( shipDocument( acme, repoId, "master", "node-1", "second" ), List.of( repoId ), 30_000 );
            assertEquals( 0, hits( alias, term( "data.title._text", "first" ) ) );
            assertEquals( 1, hits( alias, term( "data.title._text", "second" ) ) );
            assertEquals( 1, client.count( alias ) );
        }
    }

    @Test
    void deletingDocumentsRemovesThemFromTheIndexAndFromTheStore()
        throws Exception
    {
        String repoId = SearchTestFixture.createRepo( acme, "delete" );
        admin.createIndex( acme, repoId );
        String alias = SearchIndexNames.alias( acme, repoId );
        long repoKey = SearchTestFixture.repoKey( acme, repoId );

        try (Indexer indexer = indexer( acme ))
        {
            indexer.awaitRefresh( shipDocument( acme, repoId, "master", "node-1", "doomed" ), List.of( repoId ), 30_000 );
            assertEquals( 1, hits( alias, term( "data.title._text", "doomed" ) ) );

            long seq = Tx.inTenantTx( dataSource, acme, connection -> {
                SearchDocumentStore.delete( connection, repoKey, "master", List.of( "node-1" ) );
                return OutboxStore.appendDelete( connection, repoKey, "master", List.of( "node-1" ) );
            } );
            indexer.awaitRefresh( seq, List.of( repoId ), 30_000 );

            assertEquals( 0, hits( alias, term( "data.title._text", "doomed" ) ) );
            assertEquals( 0, client.count( alias ) );
        }
    }

    /** {@code awaitRefresh} must fail loudly rather than return early for a seq that will never arrive. */
    @Test
    void awaitRefreshTimesOutRatherThanReturningEarly()
        throws Exception
    {
        String repoId = SearchTestFixture.createRepo( acme, "timeout" );
        admin.createIndex( acme, repoId );

        try (Indexer indexer = indexer( acme ))
        {
            long unreachable = Tx.inTenantTx( dataSource, acme, OutboxStore::maxSeq ) + 1_000_000;
            assertThrows( IndexRefreshTimeoutException.class,
                          () -> indexer.awaitRefresh( unreachable, List.of( repoId ), 250 ) );
        }
    }

    // ----------------------------------------------------------------------- checkpoint behaviour

    /**
     * Monotonic under interleaved writers. Eight threads write concurrently while a ninth drains;
     * the checkpoint is sampled throughout and must never move backwards, and must end at or above
     * the highest seq any writer saw.
     *
     * <p>Monotonicity is enforced in SQL ({@code GREATEST}), not by discipline, because two passes
     * can finish out of order — and a checkpoint that went backwards would make
     * {@code awaitRefresh} return for a seq and then block again for one already applied.
     */
    @Test
    void checkpointIsMonotonicUnderInterleavedWriters()
        throws Exception
    {
        String repoId = SearchTestFixture.createRepo( acme, "monotonic" );
        admin.createIndex( acme, repoId );

        int writers = 8;
        int perWriter = 6;
        List<Long> observed = new ArrayList<>();

        try (Indexer indexer = indexer( acme ); ExecutorService pool = Executors.newFixedThreadPool( writers + 2 ))
        {
            List<Callable<Long>> tasks = new ArrayList<>();
            for ( int w = 0; w < writers; w++ )
            {
                int writer = w;
                tasks.add( () -> {
                    long highest = 0;
                    for ( int i = 0; i < perWriter; i++ )
                    {
                        highest = Math.max( highest, shipDocument( acme, repoId, "master", "w" + writer + "-n" + i, "v" + i ) );
                    }
                    return highest;
                } );
            }
            // Two concurrent drainers: the second is the out-of-order finisher a plain assignment
            // would let publish a stale checkpoint.
            for ( int d = 0; d < 2; d++ )
            {
                tasks.add( () -> {
                    long last = 0;
                    for ( int i = 0; i < 40; i++ )
                    {
                        long checkpoint = indexer.drain();
                        assertTrue( checkpoint >= last, "checkpoint went backwards: " + checkpoint + " after " + last );
                        last = checkpoint;
                        synchronized ( observed )
                        {
                            observed.add( checkpoint );
                        }
                    }
                    return last;
                } );
            }

            long highestWritten = 0;
            List<Future<Long>> futures = pool.invokeAll( tasks );
            for ( int i = 0; i < writers; i++ )
            {
                highestWritten = Math.max( highestWritten, futures.get( i ).get() );
            }

            long finalCheckpoint = indexer.awaitRefresh( highestWritten, List.of( repoId ), 30_000 );
            assertTrue( finalCheckpoint >= highestWritten );
            assertEquals( writers * perWriter, client.count( SearchIndexNames.alias( acme, repoId ) ) );
        }

        long previous = 0;
        for ( Long checkpoint : observed )
        {
            assertTrue( checkpoint >= 0 );
            previous = Math.max( previous, checkpoint );
        }
        assertTrue( previous > 0 );
    }

    // ---------------------------------------------------------------------------------- risk 10a

    /**
     * Risk 10a closed: the per-op write paths emit outbox rows. Before this gate only
     * {@code WriteBatch} did, so a node written through {@code StoreBranchEntry} committed to
     * Postgres and was never seen by the indexer — no error, just permanently absent from search.
     */
    @Test
    void perOpWritesEmitOutboxRows()
        throws Exception
    {
        String repoId = SearchTestFixture.createRepo( acme, "risk10a" );
        long repoKey = SearchTestFixture.repoKey( acme, repoId );
        long before = Tx.inTenantTx( dataSource, acme, OutboxStore::maxSeq );

        // A version must exist first: branch_entry FKs (repo_key, version_id).
        String versionId = "v-10a";
        Tx.inTenantTx( dataSource, acme, connection -> {
            storePayloadsAndVersion( connection, repoId, versionId, "node-10a" );
            return null;
        } );

        BranchEntryRecord entry =
            new BranchEntryRecord( "master", "node-10a", versionId, "/node-10a", Instant.now(), null, null, null );

        Long storeSeq = Tx.inTenantTx( dataSource, acme,
                                        connection -> WriteService.storeBranchEntry( connection, new RepoRef( repoId ), entry ) );
        assertNotNull( storeSeq, "StoreBranchEntry must emit an outbox row" );
        assertTrue( storeSeq > before );
        assertEquals( List.of( OutboxStore.OP_INDEX ), opsSince( acme, before ) );

        long afterStore = storeSeq;
        Long deleteSeq = Tx.inTenantTx( dataSource, acme, connection -> WriteService.deleteBranchEntries( connection, new RepoRef( repoId ),
                                                                                                          "master",
                                                                                                          List.of( "node-10a" ) ) );
        assertNotNull( deleteSeq, "DeleteBranchEntries must emit an outbox row" );
        assertTrue( deleteSeq > afterStore );
        assertEquals( List.of( OutboxStore.OP_DELETE ), opsSince( acme, afterStore ) );
    }

    private static List<String> opsSince( TenantContext tenant, long afterSeq )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, tenant, connection -> OutboxStore.next( connection, afterSeq, 100 ) )
            .stream()
            .map( OutboxStore.OutboxEntry::op )
            .toList();
    }

    /** WriteBatch's own emission, unchanged, asserted here so 10a's fix cannot regress it. */
    @Test
    void writeBatchStillEmitsOneRowPerBranchEntry()
        throws Exception
    {
        String repoId = SearchTestFixture.createRepo( acme, "writebatch" );
        long before = Tx.inTenantTx( dataSource, acme, OutboxStore::maxSeq );

        WriteBatchResponse response = Tx.inTenantTx( dataSource, acme, connection -> {
            // The inlined bytes must be exactly what version()'s hashes are computed from -- the
            // node_version -> payload FK (Phase 3 #10b) makes a mismatch a hard failure.
            List<PayloadRef> payloads = List.of( new PayloadRef.Inline( "data-v-batch".getBytes() ),
                                                 new PayloadRef.Inline( "icfg-v-batch".getBytes() ),
                                                 new PayloadRef.Inline( "acl-v-batch".getBytes() ) );
            VersionRecord version = version( "v-batch", "node-batch" );
            BranchEntryRecord entry =
                new BranchEntryRecord( "master", "node-batch", "v-batch", "/node-batch", Instant.now(), null, null, null );
            return WriteService.write( connection, new WriteBatchRequest( new RepoRef( repoId ), payloads, List.of( version ),
                                                                          List.of( entry ), null ) );
        } );

        assertNotNull( response.outboxSeq() );
        assertTrue( response.outboxSeq() > before );
        assertEquals( List.of( OutboxStore.OP_INDEX ), opsSince( acme, before ) );
    }

    // -------------------------------------------------------------------------------- D10 / rebuild

    /**
     * D10: the composite {@code _id} keeps a node's branches apart. With the bare nodeId ES 2.4
     * used — unique only per mapping type, and the type WAS the branch — draft would silently
     * overwrite master in a single-type index. Silent cross-branch data loss, not an error.
     */
    @Test
    void compositeIdKeepsTheSameNodeDistinctAcrossBranches()
        throws Exception
    {
        String repoId = SearchTestFixture.createRepo( acme, "branches" );
        admin.createIndex( acme, repoId );
        String alias = SearchIndexNames.alias( acme, repoId );

        try (Indexer indexer = indexer( acme ) )
        {
            shipDocument( acme, repoId, "master", "node-1", "published" );
            long seq = shipDocument( acme, repoId, "draft", "node-1", "unpublished" );
            indexer.awaitRefresh( seq, List.of( repoId ), 30_000 );
        }

        assertEquals( 2, client.count( alias ), "one document per (node, branch)" );
        assertEquals( 1, hits( alias, term( "data.title._text", "published" ) ) );
        assertEquals( 1, hits( alias, term( "data.title._text", "unpublished" ) ) );
        assertEquals( 1, hits( alias, term( IndexFields.BRANCH, "master" ) ) );
        assertEquals( 1, hits( alias, term( IndexFields.BRANCH, "draft" ) ) );

        JsonNode ids = idsIn( alias );
        assertTrue( ids.toString().contains( "node-1@master" ) );
        assertTrue( ids.toString().contains( "node-1@draft" ) );
    }

    /**
     * Gate G's rebuild drill, exercised here: drop the index, recreate it, replay
     * {@code search_document}, and the result is identical — same document count, same ids, same
     * source. This is what "search is a cache, never truth" has to mean operationally.
     *
     * <p>Honest boundary: index-disposable, not yet XP-independent. Until decision 3's later swap
     * derives documents from {@code payload}, a rebuild replays the documents XP shipped — which is
     * exactly why they are stored rather than forwarded.
     */
    @Test
    void rebuildFromDocumentsProducesAnIdenticalIndex()
        throws Exception
    {
        String repoId = SearchTestFixture.createRepo( acme, "rebuild" );
        admin.createIndex( acme, repoId );
        String alias = SearchIndexNames.alias( acme, repoId );

        try (Indexer indexer = indexer( acme ))
        {
            long seq = 0;
            for ( int i = 0; i < 5; i++ )
            {
                seq = shipDocument( acme, repoId, "master", "node-" + i, "title " + i );
                seq = Math.max( seq, shipDocument( acme, repoId, "draft", "node-" + i, "draft title " + i ) );
            }
            indexer.awaitRefresh( seq, List.of( repoId ), 30_000 );

            String before = sourcesIn( alias );
            assertEquals( 10, client.count( alias ) );

            admin.deleteIndex( acme, repoId );
            assertEquals( 0, admin.listTenantIndices( acme ).stream().filter( name -> name.startsWith( alias ) ).count() );

            admin.createIndex( acme, repoId );
            int replayed = indexer.reindexFromDocuments( repoId );

            assertEquals( 10, replayed );
            assertEquals( 10, client.count( alias ) );
            assertEquals( before, sourcesIn( alias ), "the replayed index must be byte-identical in content" );
        }
    }

    /** Dual-tenant isolation through the whole indexer path, not just at index-name level. */
    @Test
    void twoTenantsIndexIndependentlyUnderTheSameRepoAndNodeIds()
        throws Exception
    {
        String repoId = "shared.repo.id";
        for ( TenantContext tenant : List.of( acme, fisk ) )
        {
            Tx.inTenantSchema( dataSource, tenant, connection -> {
                long repoKey = com.enonic.nodb.engine.store.RepositoryLifecycle.createRepository( connection, repoId, null );
                com.enonic.nodb.engine.store.RepositoryLifecycle.createBranch( connection, repoKey, "master" );
                return null;
            } );
            admin.createIndex( tenant, repoId );
        }

        try (Indexer acmeIndexer = indexer( acme ); Indexer fiskIndexer = indexer( fisk ))
        {
            acmeIndexer.awaitRefresh( shipDocument( acme, repoId, "master", "node-1", "acme value" ), List.of( repoId ), 30_000 );
            fiskIndexer.awaitRefresh( shipDocument( fisk, repoId, "master", "node-1", "fisk value" ), List.of( repoId ), 30_000 );
        }

        assertEquals( 1, hits( SearchIndexNames.alias( acme, repoId ), term( "data.title._text", "acme value" ) ) );
        assertEquals( 0, hits( SearchIndexNames.alias( acme, repoId ), term( "data.title._text", "fisk value" ) ) );
        assertEquals( 1, hits( SearchIndexNames.alias( fisk, repoId ), term( "data.title._text", "fisk value" ) ) );
        assertEquals( 0, hits( SearchIndexNames.alias( fisk, repoId ), term( "data.title._text", "acme value" ) ) );

        admin.deleteIndex( acme, repoId );
        admin.deleteIndex( fisk, repoId );
    }

    /**
     * A repo with no index yet must be SKIPPED, not implicitly created. An implicitly created index
     * would come up with OpenSearch's own dynamic mapping instead of the ported templates — i.e.
     * indexing would succeed and every query would return nothing, which is blocker 2's symptom
     * arriving through a different door.
     */
    @Test
    void outboxRowsForARepoWithoutAnIndexAreSkippedNotImplicitlyIndexed()
        throws Exception
    {
        String repoId = SearchTestFixture.createRepo( acme, "noindex" );
        long seq = shipDocument( acme, repoId, "master", "node-1", "orphan" );

        try (Indexer indexer = indexer( acme ))
        {
            long checkpoint = indexer.awaitRefresh( seq, List.of(), 30_000 );
            assertTrue( checkpoint >= seq, "the checkpoint must still advance, or it stalls forever" );
        }
        assertTrue( admin.listTenantIndices( acme ).stream().noneMatch( name -> name.startsWith( "acme-" + repoId ) ) );
    }

    // --------------------------------------------------------------------------------- helpers

    /** Ships one XP-shaped document the way {@code NodeSearchService.indexDocuments} does. */
    // ------------------------------------------------------- the lost-write window (Gate C regression)

    /**
     * A write that commits while a pass is observing an EMPTY queue must never end up below the
     * checkpoint. Regression test for a silent lost write found by the Gate C corpus.
     *
     * <p><b>The defect.</b> {@code applyNextBatch} used to react to "no rows to read" by advancing
     * the checkpoint to {@code OutboxStore.maxSeq()}, so that it could step over rows the then
     * inner-joined {@code next} could not see (rows whose repository had been dropped). But
     * {@code next} and {@code maxSeq} run in two SEPARATE transactions, hence two separate snapshots.
     * A write committing in the window between them is invisible to the first and visible to the
     * second — so the checkpoint jumped straight past a row that had never been applied, and since
     * {@code next} only ever reads ABOVE the checkpoint, that row became unreachable forever. No
     * error, no retry: a document permanently missing from the search index.
     *
     * <p><b>Why it needed provoking rather than waiting for.</b> Without concurrency the branch is
     * harmless — if a readable row above the checkpoint exists, {@code next} returns it and the
     * branch is not taken at all. The bug is reachable ONLY through that inter-transaction window,
     * which is why it surfaced as an occasional single missing node (a different one each run)
     * instead of a reproducible failure. So the window is opened deliberately here: the connection
     * pool is wrapped, and the late write is committed at the exact moment {@code next}'s
     * transaction closes — the instant the old code was about to ask for {@code maxSeq}.
     *
     * <p>Both halves are asserted, because they fail for different reasons: the checkpoint invariant
     * ("never advance past a row this pass did not return") is the defect itself, and the document's
     * eventual searchability is the consequence a user would experience.
     */
    @Test
    void aWriteCommittingWhileAPassSeesAnEmptyQueueIsNeverBuriedBelowTheCheckpoint()
        throws Exception
    {
        final String repoId = SearchTestFixture.createRepo( acme, "lostwrite" );
        admin.createIndex( acme, repoId );
        final String alias = SearchIndexNames.alias( acme, repoId );

        // Start from a settled checkpoint, so the pass under test genuinely sees an empty queue.
        final Indexer settle = indexer( acme );
        settle.drain();
        final long before = settle.checkpoint();

        final long[] lateSeq = {0};
        // Committed at the exact instant the outbox READ's transaction closes -- the moment the old
        // code was about to ask a SECOND transaction for maxSeq. Keyed on the query rather than on a
        // connection ordinal so that adding or removing an unrelated round trip cannot silently move
        // the hook out of the window and turn this into a test that proves nothing.
        final javax.sql.DataSource hooked = hookedAfterOutboxReadCommits( dataSource, () -> {
            lateSeq[0] = shipDocument( acme, repoId, "master", "late-node", "Committed In The Window" );
            return null;
        } );

        final Indexer racing = new Indexer( hooked, acme, client, admin );
        racing.drain();

        assertTrue( lateSeq[0] > 0, "the hook must have committed the late write; otherwise this test proves nothing" );
        assertTrue( racing.checkpoint() < lateSeq[0],
                    "the checkpoint advanced past outbox seq " + lateSeq[0] + " without ever reading it -- that row is now unreachable" );

        // And the consequence: the write is still there to be applied, and awaitRefresh delivers it.
        racing.awaitRefresh( lateSeq[0], List.of( repoId ), 30_000 );
        assertEquals( 1, hits( alias, term( "_name._text", "late-node" ) ), "the late write must reach the index, not vanish" );
    }

    /**
     * A {@link javax.sql.DataSource} that runs {@code hook} exactly once, immediately after the
     * transaction that READ THE OUTBOX has committed and closed.
     *
     * <p>A reflection proxy rather than a hand-written delegate: the point is to observe two methods
     * ({@code prepareStatement}, {@code close}) on connections the code under test opens, and
     * {@code Connection} has ~60 others this test has no opinion about.
     *
     * <p>Keyed on the outbox query, not on "the Nth connection". An ordinal was the obvious first
     * attempt and it was wrong -- {@code drain()} reads the checkpoint before delegating to
     * {@code applyNextBatch}, which reads it again, so the ordinal that looked like the outbox read
     * was one round trip too early and the late write landed BEFORE the read instead of after it.
     * The test then passed while proving nothing at all. Matching the statement makes the hook land
     * where it is meant to regardless of how many other queries the pass happens to make.
     */
    private static javax.sql.DataSource hookedAfterOutboxReadCommits( final javax.sql.DataSource delegate, final Callable<Void> hook )
    {
        return hookedAfterQueryCommits( delegate, IndexerTest::isOutboxRead, hook );
    }

    /**
     * The generalised machinery behind {@link #hookedAfterOutboxReadCommits}: {@code hook} runs
     * exactly once, immediately after the first transaction whose statements matched {@code query}
     * has committed and closed. Every P1 lost-write regression in this class provokes its window
     * with this — keyed on the query, never on a connection ordinal, for the reason documented
     * above.
     */
    private static javax.sql.DataSource hookedAfterQueryCommits( final javax.sql.DataSource delegate,
                                                                 final java.util.function.Predicate<String> query,
                                                                 final Callable<Void> hook )
    {
        final java.util.concurrent.atomic.AtomicBoolean fired = new java.util.concurrent.atomic.AtomicBoolean();
        return (javax.sql.DataSource) java.lang.reflect.Proxy.newProxyInstance( IndexerTest.class.getClassLoader(),
                                                                               new Class<?>[]{javax.sql.DataSource.class},
                                                                               ( dsProxy, dsMethod, dsArgs ) -> {
                                                                                   if ( !"getConnection".equals( dsMethod.getName() ) )
                                                                                   {
                                                                                       return dsMethod.invoke( delegate, dsArgs );
                                                                                   }
                                                                                   final java.sql.Connection real =
                                                                                       (java.sql.Connection) dsMethod.invoke( delegate,
                                                                                                                              dsArgs );
                                                                                   final boolean[] readOutbox = {false};
                                                                                   return java.lang.reflect.Proxy.newProxyInstance(
                                                                                       IndexerTest.class.getClassLoader(),
                                                                                       new Class<?>[]{java.sql.Connection.class},
                                                                                       ( cProxy, cMethod, cArgs ) -> {
                                                                                           if ( "prepareStatement".equals(
                                                                                               cMethod.getName() ) && cArgs != null &&
                                                                                               cArgs.length > 0 &&
                                                                                               query.test( String.valueOf( cArgs[0] ) ) )
                                                                                           {
                                                                                               readOutbox[0] = true;
                                                                                           }
                                                                                           final Object result =
                                                                                               cMethod.invoke( real, cArgs );
                                                                                           if ( "close".equals( cMethod.getName() ) &&
                                                                                               readOutbox[0] &&
                                                                                               fired.compareAndSet( false, true ) )
                                                                                           {
                                                                                               hook.call();
                                                                                           }
                                                                                           return result;
                                                                                       } );
                                                                               } );
    }

    /** {@code OutboxStore.next}'s query specifically -- not {@code maxSeq}, which also reads the table. */
    private static boolean isOutboxRead( final String sql )
    {
        return sql.contains( "FROM outbox" ) && sql.contains( "ORDER BY o.seq" );
    }

    // ------------------------------------------- the torn rebuild window (Phase 5 P1 regression)

    /**
     * P1 regression (FINDINGS #1): a repo drop+recreate committing between "resolve the repo key"
     * and "list the stored documents" must never produce a silently empty replay.
     *
     * <p><b>The hazard.</b> {@code reindexFromDocuments(repoId)} used to resolve the repo key in
     * one transaction and {@code listAll} the documents in another. A drop+recreate of the same
     * repoId committing in the window leaves the first read holding the DEAD incarnation's key, so
     * the second read lists that incarnation's (cascade-emptied) document set — and the rebuild
     * replays nothing, reports success, and the live incarnation's documents never reach the
     * index. Post-P1 the key, the live index record and the document set come from ONE
     * repeatable-read snapshot ({@code Tx.inTenantSnapshot}), so the replay is always internally
     * consistent with exactly one incarnation.
     *
     * <p><b>Negative control, run first and in the same test.</b> The pre-conversion shape is
     * reintroduced verbatim (resolve in its own {@code Tx.inTenantTx}, then the replay primitive
     * in another) against the same provocation, and the torn result — zero documents replayed
     * while the store demonstrably holds three — is asserted. That is the proof this test detects
     * the defect it guards against; if the old shape ever stops tearing here, the provocation has
     * rotted and the positive half proves nothing.
     */
    @Test
    void aRepoSwapDuringRebuildNeverProducesASilentlyEmptyReplay()
        throws Exception
    {
        // ---- negative control: the pre-P1 two-transaction shape exhibits the defect ----
        final String tornRepo = SearchTestFixture.createRepo( acme, "swaptorn" );
        admin.createIndex( acme, tornRepo );
        shipDocument( acme, tornRepo, "master", "old-1", "old one" );
        shipDocument( acme, tornRepo, "master", "old-2", "old two" );
        final String tornIndexName = liveIndexName( tornRepo );

        final javax.sql.DataSource tornHooked = hookedAfterQueryCommits( dataSource, IndexerTest::isRepoResolve, () -> {
            swapRepoIncarnation( tornRepo, tornIndexName );
            return null;
        } );

        final long staleKey = Tx.inTenantTx( tornHooked, acme, connection -> com.enonic.nodb.engine.store.RepoKeys.resolve( connection,
                                                                                                                            new RepoRef(
                                                                                                                                tornRepo ) ) );
        final int torn;
        try (Indexer tornIndexer = new Indexer( tornHooked, acme, client, admin ))
        {
            torn = tornIndexer.reindexFromDocuments( staleKey, tornRepo, liveIndexName( tornRepo ) );
        }

        assertEquals( 3, storedDocumentCount( tornRepo ), "the live incarnation holds three documents throughout" );
        assertEquals( 0, torn, "negative control: the two-transaction shape must reproduce the silent empty replay -- "
            + "if it no longer does, the provocation has rotted and the positive half below proves nothing" );

        // ---- the converted path: same provocation, one snapshot, no torn replay ----
        final String repoId = SearchTestFixture.createRepo( acme, "swap" );
        admin.createIndex( acme, repoId );
        shipDocument( acme, repoId, "master", "old-1", "old one" );
        shipDocument( acme, repoId, "master", "old-2", "old two" );
        final String indexName = liveIndexName( repoId );

        final boolean[] swapped = {false};
        final javax.sql.DataSource hooked = hookedAfterQueryCommits( dataSource, IndexerTest::isRepoResolve, () -> {
            swapRepoIncarnation( repoId, indexName );
            swapped[0] = true;
            return null;
        } );

        final int replayed;
        try (Indexer racing = new Indexer( hooked, acme, client, admin ))
        {
            replayed = racing.reindexFromDocuments( repoId );
        }

        assertTrue( swapped[0], "the swap must have run; otherwise this test proves nothing" );
        assertEquals( 2, replayed, "one snapshot: the replay is exactly the incarnation it resolved (its two documents), "
            + "never the torn empty set" );
    }

    /** {@code RepoKeys.resolve/tryResolve}'s lookup -- the first read of the pair the P1 test tears. */
    private static boolean isRepoResolve( final String sql )
    {
        return sql.contains( "SELECT repo_key FROM repository" );
    }

    /**
     * The provocation: drop {@code repoId} and recreate it under a NEW {@code repo_key} carrying
     * THREE stored documents and a live {@code search_index} row (pointing at the same physical
     * index -- generation names are reused across incarnations until Gate B's never-reuse rule).
     * Runs on the UN-hooked pool, exactly like the Gate C hook's late write.
     */
    private static void swapRepoIncarnation( String repoId, String indexName )
        throws SQLException
    {
        Tx.inTenantSchema( dataSource, acme, connection -> {
            com.enonic.nodb.engine.store.RepositoryLifecycle.deleteRepository( connection, new RepoRef( repoId ) );
            long newKey = com.enonic.nodb.engine.store.RepositoryLifecycle.createRepository( connection, repoId, null );
            com.enonic.nodb.engine.store.RepositoryLifecycle.createBranch( connection, newKey, "master" );
            return null;
        } );
        long newKey = SearchTestFixture.repoKey( acme, repoId );
        Tx.inTenantTx( dataSource, acme, connection -> {
            SearchIndexStore.record( connection, newKey,
                                     new SearchIndexStore.SearchIndexRecord( 1, SearchIndexNames.alias( acme, repoId ), indexName, 1,
                                                                             IndexDocumentProjection.VERSION,
                                                                             SearchIndexStore.STATE_LIVE ) );
            return null;
        } );
        shipDocument( acme, repoId, "master", "new-1", "new one" );
        shipDocument( acme, repoId, "master", "new-2", "new two" );
        shipDocument( acme, repoId, "master", "new-3", "new three" );
    }

    private static String liveIndexName( String repoId )
        throws SQLException
    {
        String indexName = admin.liveIndexName( acme, repoId );
        assertNotNull( indexName, "fixture: repo " + repoId + " must have a live index" );
        return indexName;
    }

    private static long storedDocumentCount( String repoId )
        throws SQLException
    {
        long repoKey = SearchTestFixture.repoKey( acme, repoId );
        return Tx.inTenantTx( dataSource, acme, connection -> (long) SearchDocumentStore.listAll( connection, repoKey ).size() );
    }

    private static long shipDocument( TenantContext tenant, String repoId, String branch, String nodeId, String title )
        throws SQLException
    {
        long repoKey = SearchTestFixture.repoKey( tenant, repoId );

        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        fields.put( "data.title", List.of( new SearchDocument.Value.Text( title ) ) );
        fields.put( "data.title._analyzed", List.of( new SearchDocument.Value.Text( title ) ) );
        fields.put( "data.title._orderby", List.of( new SearchDocument.Value.Text( title.toLowerCase() ) ) );
        fields.put( "_name", List.of( new SearchDocument.Value.Text( nodeId ) ) );
        fields.put( IndexFields.PERMISSIONS_READ, List.of( new SearchDocument.Value.Text( "role:system.everyone" ) ) );
        SearchDocument document = new SearchDocument( nodeId, null, fields );

        Long seq = Tx.inTenantTx( dataSource, tenant, connection -> {
            SearchDocumentStore.store( connection, repoKey, branch, document );
            return OutboxStore.appendIndex( connection, repoKey, branch, List.of( nodeId ), null );
        } );
        return seq == null ? 0 : seq;
    }

    private static void storePayloadsAndVersion( java.sql.Connection connection, String repoId, String versionId, String nodeId )
        throws SQLException
    {
        com.enonic.nodb.engine.store.PayloadStore.putPayload( connection, ( "data-" + versionId ).getBytes() );
        com.enonic.nodb.engine.store.PayloadStore.putPayload( connection, ( "icfg-" + versionId ).getBytes() );
        com.enonic.nodb.engine.store.PayloadStore.putPayload( connection, ( "acl-" + versionId ).getBytes() );
        com.enonic.nodb.engine.store.VersionStore.store( connection, new RepoRef( repoId ), version( versionId, nodeId ) );
    }

    private static VersionRecord version( String versionId, String nodeId )
    {
        return new VersionRecord( versionId, nodeId, "/" + nodeId, Instant.now(), sha( "data-" + versionId ), sha( "icfg-" + versionId ),
                                  sha( "acl-" + versionId ), List.of(), null, Map.of() );
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

    private static JsonNode idsIn( String target )
    {
        ObjectNode body = OpenSearchClient.mapper().createObjectNode();
        body.put( "size", 100 );
        body.put( "_source", false );
        body.putArray( "sort" ).add( "_id" );
        return client.search( target, body ).path( "hits" ).path( "hits" );
    }

    /** Sorted (id, _source) pairs — a content fingerprint the rebuild must reproduce exactly. */
    private static String sourcesIn( String target )
    {
        ObjectNode body = OpenSearchClient.mapper().createObjectNode();
        body.put( "size", 1000 );
        body.putArray( "sort" ).add( "_id" );
        JsonNode hits = client.search( target, body ).path( "hits" ).path( "hits" );

        StringBuilder fingerprint = new StringBuilder();
        for ( JsonNode hit : hits )
        {
            fingerprint.append( hit.path( "_id" ).asText() ).append( '=' ).append( hit.path( "_source" ) ).append( '\n' );
        }
        return fingerprint.toString();
    }
}
