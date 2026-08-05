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
