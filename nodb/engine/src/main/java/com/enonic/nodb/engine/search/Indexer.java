package com.enonic.nodb.engine.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.Tx;
import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.store.RepoKeys;

/**
 * The outbox → OpenSearch indexer, and with it the {@code refresh(SEARCH)} half of DESIGN §3.3.
 *
 * <p><b>The contract, restated.</b> Every write commits an {@code outbox} row with a monotonic
 * seq in the same transaction as the branch/version rows. This class applies rows in seq order
 * and advances {@code index_checkpoint}. {@link #awaitRefresh} blocks until
 * {@code checkpoint >= seq} and THEN issues an OpenSearch refresh on the affected indices. Both
 * halves are needed and neither is sufficient: the checkpoint proves the documents reached
 * OpenSearch, the refresh proves they are searchable.
 *
 * <p><b>Ordering of the two side effects is the correctness argument.</b> Bulk first, checkpoint
 * second. A crash between them replays the batch, which is harmless because every op is
 * idempotent (bulk {@code index} fully replaces; bulk {@code delete} of an absent id is
 * {@code not_found}, not an error). The other order would advance past writes that never landed —
 * i.e. report read-your-writes success for documents that are not in the index. That is the one
 * failure the contract cannot tolerate, so at-least-once is chosen deliberately over at-most-once.
 *
 * <p><b>One indexer per tenant.</b> The outbox is per-tenant-schema (it must stay globally ordered
 * per tenant), so this class is instantiated per {@link TenantContext}. Multi-instance indexing on
 * one cell — and the leader election it would need — is Phase 6's problem, not this gate's; the
 * checkpoint's {@code GREATEST} semantics already make a second instance safe rather than
 * corrupting, just wasteful.
 */
public final class Indexer
    implements AutoCloseable
{
    private static final Logger LOG = LoggerFactory.getLogger( Indexer.class );

    /** Outbox rows read per pass. Bounded so one huge import cannot make a pass unbounded in memory. */
    public static final int DEFAULT_BATCH_SIZE = 500;

    private static final long POLL_INTERVAL_MILLIS = 100;

    private final DataSource dataSource;

    private final TenantContext tenant;

    private final OpenSearchClient client;

    private final SearchIndexAdmin admin;

    private final int batchSize;

    private final Object checkpointMonitor = new Object();

    private final AtomicBoolean running = new AtomicBoolean();

    private ScheduledExecutorService poller;

    public Indexer( DataSource dataSource, TenantContext tenant, OpenSearchClient client, SearchIndexAdmin admin )
    {
        this( dataSource, tenant, client, admin, DEFAULT_BATCH_SIZE );
    }

    public Indexer( DataSource dataSource, TenantContext tenant, OpenSearchClient client, SearchIndexAdmin admin, int batchSize )
    {
        this.dataSource = dataSource;
        this.tenant = tenant;
        this.client = client;
        this.admin = admin;
        this.batchSize = batchSize;
    }

    // ------------------------------------------------------------------------- background loop

    /**
     * Starts background polling. Deliberately a poll rather than a Postgres {@code LISTEN}: the
     * outbox is read inside the tenant's own transaction scope ({@code SET LOCAL ROLE}), a
     * notification channel would be a second, differently-scoped connection, and 100 ms of extra
     * latency for a reader that did NOT call {@code awaitRefresh} is invisible — the readers that
     * care all await, which drains synchronously.
     */
    public void start()
    {
        if ( !running.compareAndSet( false, true ) )
        {
            return;
        }
        poller = Executors.newSingleThreadScheduledExecutor( runnable -> {
            Thread thread = new Thread( runnable, "nodb-indexer-" + tenant.tenantId() );
            thread.setDaemon( true );
            return thread;
        } );
        poller.scheduleWithFixedDelay( this::pollQuietly, 0, POLL_INTERVAL_MILLIS, TimeUnit.MILLISECONDS );
        LOG.info( "Indexer started for tenant {}", tenant.tenantId() );
    }

    public boolean isRunning()
    {
        return running.get();
    }

    private void pollQuietly()
    {
        try
        {
            drain();
        }
        catch ( SQLException | RuntimeException e )
        {
            // Never let a pass kill the poller: the outbox is durable, so the next pass retries
            // from the same checkpoint. A permanently failing row shows up as growing outbox lag
            // (DESIGN §7.1's headline SLO), which is the intended signal.
            LOG.warn( "Indexer pass failed for tenant {}; will retry", tenant.tenantId(), e );
        }
    }

    // ------------------------------------------------------------------------------ the work

    /**
     * Applies every pending outbox row, in batches, until nothing is left. Returns the checkpoint
     * reached.
     */
    public long drain()
        throws SQLException
    {
        long checkpoint = checkpoint();
        while ( true )
        {
            long advanced = applyNextBatch();
            if ( advanced == checkpoint )
            {
                return checkpoint;
            }
            checkpoint = advanced;
        }
    }

    /** Applies at most one batch. Returns the checkpoint after the pass. */
    public long applyNextBatch()
        throws SQLException
    {
        long checkpoint = checkpoint();
        List<OutboxStore.OutboxEntry> entries =
            Tx.inTenantTx( dataSource, tenant, connection -> OutboxStore.next( connection, checkpoint, batchSize ) );

        if ( entries.isEmpty() )
        {
            // Nothing to do, so the checkpoint does not move. It MUST NOT move here.
            //
            // This branch used to advance the checkpoint to `OutboxStore.maxSeq()` in order to step
            // over rows the old inner-joined `next` could not see (rows whose repository had been
            // dropped). That was a silent LOST WRITE: `next` and `maxSeq` ran in two separate
            // transactions, so any write committing in the window between them was invisible to the
            // first and visible to the second, and the checkpoint jumped straight past it. `next`
            // only ever reads rows ABOVE the checkpoint, so the row was then unreachable forever —
            // a document permanently absent from the search index, with no error anywhere.
            //
            // Observed as exactly that: one arbitrary node (a different one each run) missing from
            // the Gate C corpus, because the row lost was the node's IndexDocuments row — the one
            // carrying its document — while its earlier branch-entry row had already been applied
            // and skipped for a not-yet-shipped document.
            //
            // The fix is upstream: `next` LEFT-joins now and returns orphan rows itself, so the
            // indexer can see and skip them, and nothing has to guess at what it could not see.
            return checkpoint;
        }

        long highestSeq = entries.get( entries.size() - 1 ).seq();
        Set<String> touchedIndices = apply( entries );
        long published = publishCheckpoint( highestSeq );

        LOG.debug( "Indexer applied {} outbox rows up to seq {} for tenant {} ({} indices touched)", entries.size(), highestSeq,
                   tenant.tenantId(), touchedIndices.size() );
        return published;
    }

    /**
     * Turns a batch of outbox rows into one bulk request.
     *
     * <p>Rows are grouped by (repo, branch) so the documents for a group are fetched in one query
     * rather than one per node — the same N+1 avoidance the branch-entry read path already makes.
     * Within a group the LAST row for a node wins, which is what makes replay cheap: a node
     * touched five times in one batch is indexed once, from its current stored document.
     */
    private Set<String> apply( List<OutboxStore.OutboxEntry> entries )
        throws SQLException
    {
        BulkRequest bulk = new BulkRequest();
        Set<String> touchedIndices = new LinkedHashSet<>();

        Map<GroupKey, Group> groups = new LinkedHashMap<>();
        List<OutboxStore.OutboxEntry> controls = new ArrayList<>();
        // repoId -> live physical index name, resolved once per pass. One lookup per repo, not
        // per row: a batch is usually one repo and many nodes.
        Map<String, String> indexNames = new LinkedHashMap<>();

        for ( OutboxStore.OutboxEntry entry : entries )
        {
            if ( entry.repoId() == null )
            {
                // An ORPHAN: the row outlived its `repository` row (no FK, by design — DELETE_REPO
                // has to survive the drop). There is nothing to apply, and the index is already gone
                // (repo delete is index-then-Postgres). Skipping it HERE, having seen it, is what
                // lets the checkpoint move past it without the blind maxSeq jump that used to lose
                // writes — see applyNextBatch.
                LOG.debug( "Skipping outbox row {} (op {}): its repository is gone", entry.seq(), entry.op() );
                continue;
            }
            switch ( entry.op() )
            {
                case OutboxStore.OP_INDEX, OutboxStore.OP_DELETE ->
                {
                    if ( entry.nodeId() == null || entry.branch() == null )
                    {
                        LOG.warn( "Skipping malformed outbox row {} (op {} without node/branch)", entry.seq(), entry.op() );
                        continue;
                    }
                    groups.computeIfAbsent( new GroupKey( entry.repoKey(), entry.repoId(), entry.branch() ), key -> new Group() )
                        .ops.put( entry.nodeId(), entry.op() );
                }
                case OutboxStore.OP_DELETE_BRANCH, OutboxStore.OP_DELETE_REPO, OutboxStore.OP_REINDEX -> controls.add( entry );
                default -> LOG.warn( "Skipping outbox row {} with unknown op '{}'", entry.seq(), entry.op() );
            }
        }

        for ( Map.Entry<GroupKey, Group> group : groups.entrySet() )
        {
            GroupKey key = group.getKey();
            String indexName = indexNameFor( indexNames, key.repoId() );
            if ( indexName == null )
            {
                // The repo has no index yet (a pre-Phase-4 repo, or one whose index was dropped
                // for a rebuild). Skipping is correct: rebuild replays from search_document, so
                // nothing is lost, and creating an index implicitly here would produce one
                // WITHOUT the ported mappings — the exact failure mode blocker 2 describes
                // (indexing succeeds, queries return nothing).
                LOG.debug( "No search index for repo {}; skipping {} outbox ops", key.repoId(), group.getValue().ops.size() );
                continue;
            }
            touchedIndices.add( indexName );
            appendGroup( bulk, key, group.getValue(), indexName );
        }

        for ( OutboxStore.OutboxEntry control : controls )
        {
            touchedIndices.addAll( applyControl( indexNames, control ) );
        }

        if ( !bulk.isEmpty() )
        {
            client.bulk( bulk.toNdjson() );
        }
        return touchedIndices;
    }

    private void appendGroup( BulkRequest bulk, GroupKey key, Group group, String indexName )
        throws SQLException
    {
        List<String> toIndex = new ArrayList<>();
        List<String> toDelete = new ArrayList<>();
        group.ops.forEach( ( nodeId, op ) -> ( OutboxStore.OP_DELETE.equals( op ) ? toDelete : toIndex ).add( nodeId ) );

        for ( String nodeId : toDelete )
        {
            bulk.delete( indexName, IndexFields.documentId( nodeId, key.branch() ) );
        }

        if ( toIndex.isEmpty() )
        {
            return;
        }
        Map<String, SearchDocument> documents = Tx.inTenantTx( dataSource, tenant, connection -> SearchDocumentStore.get( connection,
                                                                                                                         key.repoKey(),
                                                                                                                         key.branch(),
                                                                                                                         toIndex ) );
        for ( String nodeId : toIndex )
        {
            SearchDocument document = documents.get( nodeId );
            if ( document == null )
            {
                // See SearchDocumentStore.get's contract: WriteBatch commits its outbox row
                // before XP ships the document (two SPI calls, always have been), so this is an
                // expected race, not a lost write. The document's own outbox row applies it.
                LOG.debug( "No shipped document yet for node {} in {}/{}; will index when it arrives", nodeId, key.repoId(),
                           key.branch() );
                continue;
            }
            bulk.index( indexName, IndexDocumentProjection.documentId( document, key.branch() ),
                        IndexDocumentProjection.project( document, key.repoId(), key.branch() ) );
        }
    }

    /**
     * Fan-out ops. DELETE_BRANCH and DELETE_REPO are expressed as OpenSearch delete-by-query on
     * the {@link IndexFields#BRANCH} field rather than as N per-node deletes: the whole reason
     * branch stopped being a mapping type is that one index now holds every branch of a repo, so
     * "drop this branch" is a query, not an index drop. REINDEX replays {@code search_document},
     * which is Gate G's drill exercised from the ordinary outbox path.
     */
    private Set<String> applyControl( Map<String, String> indexNames, OutboxStore.OutboxEntry control )
        throws SQLException
    {
        String indexName = indexNameFor( indexNames, control.repoId() );
        if ( indexName == null )
        {
            return Set.of();
        }

        switch ( control.op() )
        {
            // search_document rows for a deleted branch are already gone via the branch FK's
            // ON DELETE CASCADE; this op is the index side of the same delete.
            case OutboxStore.OP_DELETE_BRANCH -> deleteByBranch( indexName, control.branch() );
            case OutboxStore.OP_DELETE_REPO -> client.deleteIndex( indexName );
            case OutboxStore.OP_REINDEX -> reindexFromDocuments( control.repoKey(), control.repoId(), indexName );
            default ->
            {
            }
        }
        return Set.of( indexName );
    }

    private void deleteByBranch( String indexName, String branch )
    {
        var query = OpenSearchClient.mapper().createObjectNode();
        query.putObject( "term" ).put( IndexFields.BRANCH, branch );
        client.deleteByQuery( indexName, query );
    }

    /**
     * Replays every stored document of a repo into its (current) index. This is the
     * rebuild-from-docs primitive: drop the index, create it, replay, and the result is identical
     * because the projection is a pure function of the stored canonical document plus its version.
     *
     * <p>Also the honest boundary of decision 3's deferral: this is index-disposable, NOT yet
     * XP-independent. Until server-side derivation from {@code payload} lands, a rebuild needs the
     * documents XP shipped — which is exactly why they are stored rather than forwarded.
     */
    public int reindexFromDocuments( long repoKey, String repoId, String indexName )
        throws SQLException
    {
        List<SearchDocumentStore.BranchDocument> documents =
            Tx.inTenantTx( dataSource, tenant, connection -> SearchDocumentStore.listAll( connection, repoKey ) );

        BulkRequest bulk = new BulkRequest();
        for ( SearchDocumentStore.BranchDocument stored : documents )
        {
            bulk.index( indexName, IndexDocumentProjection.documentId( stored.document(), stored.branch() ),
                        IndexDocumentProjection.project( stored.document(), repoId, stored.branch() ) );
        }
        if ( !bulk.isEmpty() )
        {
            client.bulk( bulk.toNdjson() );
        }
        client.refresh( List.of( indexName ) );
        LOG.info( "Reindexed {} documents into {}", documents.size(), indexName );
        return documents.size();
    }

    /** Convenience for the rebuild drill: resolve the repo and replay into its live index. */
    public int reindexFromDocuments( String repoId )
        throws SQLException
    {
        long repoKey = Tx.inTenantTx( dataSource, tenant, connection -> RepoKeys.resolve( connection, new RepoRef( repoId ) ) );
        String indexName = admin.liveIndexName( tenant, repoId );
        if ( indexName == null )
        {
            throw new IllegalStateException( "Repository " + repoId + " has no live search index" );
        }
        return reindexFromDocuments( repoKey, repoId, indexName );
    }

    // ------------------------------------------------------------------------- refresh(SEARCH)

    public long checkpoint()
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, tenant, connection -> IndexCheckpointStore.read( connection, IndexCheckpointStore.SEARCH_INDEXER ) );
    }

    /**
     * {@code RefreshMode.SEARCH}: block until every write up to {@code seq} is applied, then make
     * it searchable.
     *
     * <p>The wait is not passive. If the background poller is not running (tests, a single-shot
     * server, a poller that just crashed) this drains the outbox itself, so the contract holds
     * without depending on a thread being alive — a refresh that silently waited forever because
     * the poller died would be the worst possible expression of this contract.
     *
     * @param seq       the caller's last committed outbox seq; 0 or less means "nothing to wait for"
     * @param repoIds   repos to refresh; empty means every index of this tenant
     * @param timeoutMs how long to wait for the checkpoint before giving up
     * @return the checkpoint reached
     */
    public long awaitRefresh( long seq, List<String> repoIds, long timeoutMs )
        throws SQLException, InterruptedException
    {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos( timeoutMs );
        long checkpoint = drain();

        while ( checkpoint < seq )
        {
            if ( System.nanoTime() >= deadline )
            {
                throw new IndexRefreshTimeoutException(
                    "Timed out after " + timeoutMs + "ms waiting for the search checkpoint to reach seq " + seq + " (at " + checkpoint +
                        ") for tenant " + tenant.tenantId() );
            }
            synchronized ( checkpointMonitor )
            {
                checkpointMonitor.wait( POLL_INTERVAL_MILLIS );
            }
            checkpoint = drain();
        }

        client.refresh( refreshTargets( repoIds ) );
        return checkpoint;
    }

    private List<String> refreshTargets( List<String> repoIds )
    {
        if ( repoIds.isEmpty() )
        {
            return admin.listTenantIndices( tenant );
        }
        List<String> aliases = new ArrayList<>( repoIds.size() );
        for ( String repoId : repoIds )
        {
            aliases.add( SearchIndexNames.alias( tenant, repoId ) );
        }
        return aliases;
    }

    private long publishCheckpoint( long seq )
        throws SQLException
    {
        long published =
            Tx.inTenantTx( dataSource, tenant, connection -> IndexCheckpointStore.advance( connection,
                                                                                           IndexCheckpointStore.SEARCH_INDEXER, seq ) );
        synchronized ( checkpointMonitor )
        {
            checkpointMonitor.notifyAll();
        }
        return published;
    }

    /**
     * The PHYSICAL index a write goes to, from {@code search_index} rather than from the alias.
     *
     * <p>Writing through the alias would work today (one generation, one index) and break the
     * moment Gate G's rebuild puts two indices behind it: OpenSearch refuses a write to a
     * multi-index alias unless one is marked {@code is_write_index}. Resolving the live generation
     * from NoDB's own metadata is both correct now and correct then — and it is the rule DESIGN §5
     * states outright ("the authoritative alias→generation mapping is NoDB metadata"). Reads still
     * target the alias; only writes name the generation.
     *
     * <p>{@code null} means "this repo has no index", which is a normal state, not an error — see
     * the call site's comment.
     */
    private String indexNameFor( Map<String, String> cache, String repoId )
        throws SQLException
    {
        if ( cache.containsKey( repoId ) )
        {
            return cache.get( repoId );
        }
        String indexName = admin.liveIndexName( tenant, repoId );
        cache.put( repoId, indexName );
        return indexName;
    }

    @Override
    public void close()
    {
        if ( running.compareAndSet( true, false ) && poller != null )
        {
            poller.shutdownNow();
        }
    }

    private record GroupKey(long repoKey, String repoId, String branch)
    {
    }

    private static final class Group
    {
        /** node id → last op seen in this batch. LinkedHashMap keeps the bulk body deterministic. */
        private final Map<String, String> ops = new LinkedHashMap<>();
    }
}
