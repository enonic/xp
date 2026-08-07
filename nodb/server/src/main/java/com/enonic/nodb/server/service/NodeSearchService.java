package com.enonic.nodb.server.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.sql.DataSource;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.Tx;
import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.search.IndexRefreshTimeoutException;
import com.enonic.nodb.engine.search.Indexer;
import com.enonic.nodb.engine.search.OpenSearchException;
import com.enonic.nodb.engine.search.OutboxStore;
import com.enonic.nodb.engine.search.QueryDslTranslator;
import com.enonic.nodb.engine.search.SearchQuery;
import com.enonic.nodb.engine.search.SearchQueryExecutor;
import com.enonic.nodb.engine.search.SearchDocument;
import com.enonic.nodb.engine.search.SearchDocumentStore;
import com.enonic.nodb.engine.search.SearchIndexAdmin;
import com.enonic.nodb.engine.store.RepoKeys;
import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.AwaitRefreshRequest;
import com.enonic.nodb.proto.v1.CreateSearchIndexRequest;
import com.enonic.nodb.proto.v1.DeleteSearchIndexRequest;
import com.enonic.nodb.proto.v1.DeleteDocumentsRequest;
import com.enonic.nodb.proto.v1.IndexAck;
import com.enonic.nodb.proto.v1.IndexDoc;
import com.enonic.nodb.proto.v1.IndexDocumentsRequest;
import com.enonic.nodb.proto.v1.IndexField;
import com.enonic.nodb.proto.v1.IndexValue;
import com.enonic.nodb.proto.v1.NodeSearchGrpc;
import com.enonic.nodb.proto.v1.ReturnValue;
import com.enonic.nodb.proto.v1.SearchHit;
import com.enonic.nodb.proto.v1.SearchResult;
import com.enonic.nodb.proto.v1.SearchSourceRef;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.auth.TenantPrincipal;

/**
 * The {@code NodeSearch} write path (Phase 4 Gate A): {@code IndexDocuments},
 * {@code DeleteDocuments}, {@code AwaitRefresh} — plus, from Gate B, the {@code Search} query
 * path. {@code Reindex} stays un-overridden and therefore answers UNIMPLEMENTED, the generated
 * base class's own convention used throughout this server.
 *
 * <p>{@code Search} receives XP's canonical JSON query DSL and an envelope, and translates it
 * server-side (decision 2). Gates B–E translate every envelope slot; anything the translators do
 * not know is a loud {@code INVALID_ARGUMENT} rather than a partial translation returning
 * plausible-looking wrong hits. Note the envelope is validated by consequence rather than by a
 * schema pass: the translators reject every construct they do not know, and the wire's
 * {@code format_version} is checked before anything else is read.
 *
 * <p>Decision 3 in one sentence: XP builds the index documents and ships them here, NoDB stores
 * them transactionally with an outbox row, and the {@link Indexer} applies them to OpenSearch.
 *
 * <p><b>Why the store and the outbox row are one transaction.</b> The alternative — index
 * synchronously inside the RPC — would make every write wait on OpenSearch, put a second
 * distributed commit in the write path, and leave nothing to replay after a restart. Committing the
 * document plus its outbox row to Postgres is a single local transaction; whether OpenSearch has
 * caught up is then a question with an exact answer (the checkpoint), which is what makes
 * {@code refresh(SEARCH)} implementable at all.
 *
 * <p>One {@link Indexer} per tenant, created on first use: the outbox lives in the tenant schema
 * and must stay globally ordered per tenant.
 */
public final class NodeSearchService
    extends NodeSearchGrpc.NodeSearchImplBase
{
    /** Matches the ES-era refresh timeout order of magnitude; overridable per request. */
    private static final long DEFAULT_AWAIT_TIMEOUT_MILLIS = 30_000;

    /** The only envelope version this server understands. */
    private static final int SUPPORTED_FORMAT_VERSION = 1;

    private final DataSource dataSource;

    private final Function<TenantContext, Indexer> indexers;

    private final SearchQueryExecutor executor;

    /** Phase 4 Gate F: the search index's own lifecycle (create/delete/purge). */
    private final SearchIndexAdmin searchIndexAdmin;

    private final Map<String, Indexer> cache = new LinkedHashMap<>();

    public NodeSearchService( DataSource dataSource, Function<TenantContext, Indexer> indexerFactory, SearchQueryExecutor executor,
                              SearchIndexAdmin searchIndexAdmin )
    {
        this.dataSource = dataSource;
        this.indexers = indexerFactory;
        this.executor = executor;
        this.searchIndexAdmin = searchIndexAdmin;
    }

    @Override
    public void search( com.enonic.nodb.proto.v1.SearchRequest request, StreamObserver<SearchResult> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();

        if ( request.getFormatVersion() != SUPPORTED_FORMAT_VERSION )
        {
            responseObserver.onError( Status.INVALID_ARGUMENT.withDescription(
                "Unsupported search envelope format_version " + request.getFormatVersion() + " (this server speaks " +
                    SUPPORTED_FORMAT_VERSION + ")" ).asRuntimeException() );
            return;
        }

        try
        {
            SearchQueryExecutor.Result result = executor.execute( principal.tenantContext(), toEngineQuery( request ) );

            SearchResult.Builder response =
                SearchResult.newBuilder().setTotalHits( result.totalHits() ).setMaxScore( result.maxScore() );
            response.setSuggestions( result.suggestions() );
            response.setAggregations( result.aggregations() );
            for ( SearchQueryExecutor.Hit hit : result.hits() )
            {
                response.addHits( toProto( hit ) );
            }

            responseObserver.onNext( response.build() );
            responseObserver.onCompleted();
        }
        catch ( QueryDslTranslator.UnsupportedQueryException e )
        {
            responseObserver.onError( Status.INVALID_ARGUMENT.withDescription( e.getMessage() ).asRuntimeException() );
        }
        catch ( IllegalArgumentException e )
        {
            responseObserver.onError( Status.INVALID_ARGUMENT.withDescription( e.getMessage() ).asRuntimeException() );
        }
        catch ( OpenSearchException e )
        {
            // The engine's own response body is the actionable part, so it is carried through
            // rather than collapsed into a generic message.
            responseObserver.onError( Status.UNAVAILABLE.withDescription( e.getMessage() ).asRuntimeException() );
        }
    }

    private static SearchQuery toEngineQuery( com.enonic.nodb.proto.v1.SearchRequest request )
    {
        List<SearchQuery.Source> sources = new ArrayList<>( request.getSourcesCount() );
        for ( SearchSourceRef source : request.getSourcesList() )
        {
            sources.add( new SearchQuery.Source( source.getRepoId(), source.getBranch(), List.copyOf( source.getPrincipalsList() ) ) );
        }

        // Every envelope slot is now translated (Gate E closed the last one). The fence moved DOWN a
        // level rather than away: an aggregation naming a type the translator does not implement, or
        // a sub-aggregation under a metric aggregation, still fails loudly inside
        // AggregationTranslator instead of being dropped from the response.
        return new SearchQuery( sources, request.getQuery(), List.copyOf( request.getQueryFiltersList() ),
                                List.copyOf( request.getPostFiltersList() ), List.copyOf( request.getSortList() ),
                                request.getAggregations(), request.getSuggest(), request.getHighlight(), request.getFrom(),
                                request.getSize(), request.getBatchSize(), request.getExplain(), request.getSearchOptimizer(),
                                List.copyOf( request.getReturnFieldsList() ) );
    }

    private static SearchHit toProto( SearchQueryExecutor.Hit hit )
    {
        SearchHit.Builder builder = SearchHit.newBuilder().setId( hit.id() ).setScore( hit.score() );
        if ( hit.repoId() != null )
        {
            builder.setRepoId( hit.repoId() );
        }
        if ( hit.branch() != null )
        {
            builder.setBranch( hit.branch() );
        }
        builder.addAllSortValues( hit.sortValues() );

        // Names are already canonical: the postfix strip happens in HighlightTranslator, because
        // the physical postfixes are the server's vocabulary and the client is a decoder.
        hit.highlights().forEach( ( name, fragments ) -> builder.addHighlights(
            com.enonic.nodb.proto.v1.HighlightedProperty.newBuilder().setName( name ).addAllFragments( fragments ).build() ) );

        hit.returnValues().forEach( ( name, values ) -> {
            ReturnValue.Builder value = ReturnValue.newBuilder().setName( name );
            for ( Object raw : values )
            {
                value.addValues( toProto( raw ) );
            }
            builder.addReturnValues( value.build() );
        } );

        return builder.build();
    }

    private static IndexValue toProto( Object value )
    {
        IndexValue.Builder builder = IndexValue.newBuilder();
        if ( value instanceof Double || value instanceof Float )
        {
            builder.setDoubleValue( ( (Number) value ).doubleValue() );
        }
        else if ( value instanceof Number )
        {
            builder.setLongValue( ( (Number) value ).longValue() );
        }
        else if ( value instanceof Boolean )
        {
            builder.setBoolValue( (Boolean) value );
        }
        else
        {
            builder.setStringValue( String.valueOf( value ) );
        }
        return builder.build();
    }

    @Override
    public void indexDocuments( IndexDocumentsRequest request, StreamObserver<IndexAck> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        RepoRef repo = new RepoRef( request.getRepoId() );
        String branch = request.getBranch();

        try
        {
            List<SearchDocument> documents = new ArrayList<>( request.getDocumentsCount() );
            List<String> nodeIds = new ArrayList<>( request.getDocumentsCount() );
            for ( IndexDoc doc : request.getDocumentsList() )
            {
                SearchDocument document = toEngineDocument( doc );
                documents.add( document );
                nodeIds.add( document.nodeId() );
            }

            Long seq = Tx.inTenantTx( dataSource, principal.tenantContext(), connection -> {
                long repoKey = RepoKeys.resolve( connection, repo );
                for ( SearchDocument document : documents )
                {
                    SearchDocumentStore.store( connection, repoKey, branch, document );
                }
                // The version id is not carried: the outbox row says "reindex this node in this
                // branch", and the indexer reads the CURRENT stored document. Pinning a version
                // would make a replayed row re-index a stale document.
                return OutboxStore.appendIndex( connection, repoKey, branch, nodeIds, null );
            } );

            responseObserver.onNext( IndexAck.newBuilder().setOutboxSeq( seq == null ? 0 : seq ).build() );
            responseObserver.onCompleted();
        }
        catch ( IllegalArgumentException e )
        {
            responseObserver.onError( Status.INVALID_ARGUMENT.withDescription( e.getMessage() ).asRuntimeException() );
        }
        catch ( SQLException e )
        {
            responseObserver.onError( NodeStoreService.mapSqlException( e ) );
        }
    }

    @Override
    public void deleteDocuments( DeleteDocumentsRequest request, StreamObserver<IndexAck> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        RepoRef repo = new RepoRef( request.getRepoId() );
        String branch = request.getBranch();
        List<String> nodeIds = request.getNodeIdsList();

        try
        {
            Long seq = Tx.inTenantTx( dataSource, principal.tenantContext(), connection -> {
                long repoKey = RepoKeys.resolve( connection, repo );
                SearchDocumentStore.delete( connection, repoKey, branch, nodeIds );
                return OutboxStore.appendDelete( connection, repoKey, branch, nodeIds );
            } );
            responseObserver.onNext( IndexAck.newBuilder().setOutboxSeq( seq == null ? 0 : seq ).build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( NodeStoreService.mapSqlException( e ) );
        }
    }

    @Override
    public void awaitRefresh( AwaitRefreshRequest request, StreamObserver<Ack> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        long timeout = request.getTimeoutMillis() > 0 ? request.getTimeoutMillis() : DEFAULT_AWAIT_TIMEOUT_MILLIS;

        try
        {
            long checkpoint =
                indexer( principal.tenantContext() ).awaitRefresh( request.getSeq(), request.getRepoIdsList(), timeout );
            responseObserver.onNext( Ack.newBuilder().setOutboxSeq( checkpoint ).build() );
            responseObserver.onCompleted();
        }
        catch ( IndexRefreshTimeoutException e )
        {
            // DEADLINE_EXCEEDED, not INTERNAL and not a rollback: the write IS committed and
            // durable, it is only not yet searchable, so retrying is meaningful.
            responseObserver.onError( Status.DEADLINE_EXCEEDED.withDescription( e.getMessage() ).asRuntimeException() );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            responseObserver.onError( Status.CANCELLED.withDescription( "Interrupted while awaiting refresh" ).asRuntimeException() );
        }
        catch ( OpenSearchException e )
        {
            responseObserver.onError( Status.UNAVAILABLE.withDescription( e.getMessage() ).asRuntimeException() );
        }
        catch ( SQLException e )
        {
            responseObserver.onError( NodeStoreService.mapSqlException( e ) );
        }
    }

    /**
     * Phase 4 Gate F: idempotent create of a repository's search index -- see the RPC's comment in
     * nodb.proto for why one idempotent pair serves both the repository-lifecycle caller (for which
     * this is a no-op, the index already exists) and the PURGE caller (for which it is the real
     * thing).
     */
    @Override
    public void createSearchIndex( CreateSearchIndexRequest request, StreamObserver<Ack> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try
        {
            searchIndexAdmin.createIndex( principal.tenantContext(), request.getRepoId() );
            responseObserver.onNext( Ack.newBuilder().build() );
            responseObserver.onCompleted();
        }
        catch ( OpenSearchException e )
        {
            responseObserver.onError( Status.UNAVAILABLE.withDescription( e.getMessage() ).asRuntimeException() );
        }
        catch ( SQLException e )
        {
            responseObserver.onError( NodeStoreService.mapSqlException( e ) );
        }
    }

    /**
     * Phase 4 Gate F: drops every generation of the repository's search index AND its stored
     * documents, in that order. The documents matter: leaving them behind would let a later
     * rebuild-from-documents resurrect exactly what the purge removed. Idempotent -- an absent index
     * or an already-dropped repository row is a successful delete.
     */
    @Override
    public void deleteSearchIndex( DeleteSearchIndexRequest request, StreamObserver<Ack> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        RepoRef repo = new RepoRef( request.getRepoId() );
        try
        {
            searchIndexAdmin.deleteIndex( principal.tenantContext(), request.getRepoId() );

            Tx.inTenantTx( dataSource, principal.tenantContext(), connection -> {
                Long repoKey = RepoKeys.tryResolve( connection, repo );
                if ( repoKey != null )
                {
                    SearchDocumentStore.deleteAll( connection, repoKey );
                }
                return null;
            } );

            responseObserver.onNext( Ack.newBuilder().build() );
            responseObserver.onCompleted();
        }
        catch ( OpenSearchException e )
        {
            responseObserver.onError( Status.UNAVAILABLE.withDescription( e.getMessage() ).asRuntimeException() );
        }
        catch ( SQLException e )
        {
            responseObserver.onError( NodeStoreService.mapSqlException( e ) );
        }
    }

    private synchronized Indexer indexer( TenantContext tenant )
    {
        return cache.computeIfAbsent( tenant.tenantId(), id -> indexers.apply( tenant ) );
    }

    /**
     * Stops and forgets {@code tenantId}'s indexer, returning it so the caller can drop it from its
     * own registry; {@code null} if this tenant never had one.
     * <p>
     * Phase 4 Gate F (nodb/BUILD-PHASE-4.md). An indexer is a polling thread plus a share of the
     * connection pool, created on a tenant's first search write and — until now — kept for the life
     * of the process. That is right for the one-tenant-per-install case and wrong for a cell that
     * sees many tenants: the itest suite reached ~80 live indexers on a 16-connection pool and
     * {@code awaitRefresh} stopped returning, which is the same shape a shared cell would hit with
     * churning tenants. Releasing an idle tenant is the missing half of the lifecycle, not a test
     * hook (Phase 6 owns the policy for WHEN to release — idle timeout, eviction; this is the
     * mechanism it needs).
     */
    public synchronized Indexer releaseTenant( String tenantId )
    {
        Indexer indexer = cache.remove( tenantId );
        if ( indexer != null )
        {
            indexer.close();
        }
        return indexer;
    }

    static SearchDocument toEngineDocument( IndexDoc doc )
    {
        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        for ( IndexField field : doc.getFieldsList() )
        {
            List<SearchDocument.Value> values = new ArrayList<>( field.getValuesCount() );
            for ( IndexValue value : field.getValuesList() )
            {
                values.add( switch ( value.getValueCase() )
                            {
                                case STRING_VALUE -> new SearchDocument.Value.Text( value.getStringValue() );
                                case DOUBLE_VALUE -> new SearchDocument.Value.Number( value.getDoubleValue() );
                                case LONG_VALUE -> new SearchDocument.Value.Integer( value.getLongValue() );
                                case BOOL_VALUE -> new SearchDocument.Value.Bool( value.getBoolValue() );
                                case INSTANT_MILLIS -> new SearchDocument.Value.Timestamp( value.getInstantMillis() );
                                case VALUE_NOT_SET -> throw new IllegalArgumentException(
                                    "Index document field '" + field.getName() + "' has a value with no type set" );
                            } );
            }
            // merge, not put: a repeated field list may legitimately carry the same name twice.
            fields.merge( field.getName(), List.copyOf( values ), ( first, second ) -> {
                List<SearchDocument.Value> merged = new ArrayList<>( first );
                merged.addAll( second );
                return List.copyOf( merged );
            } );
        }
        String analyzer = doc.getAnalyzer().isEmpty() ? null : doc.getAnalyzer();
        return new SearchDocument( doc.getId(), analyzer, fields );
    }

    private static TenantPrincipal currentPrincipal()
    {
        TenantPrincipal principal = TenantAuthInterceptor.PRINCIPAL_KEY.get();
        if ( principal == null )
        {
            throw Status.UNAUTHENTICATED.withDescription( "No authenticated tenant in context" ).asRuntimeException();
        }
        return principal;
    }
}
