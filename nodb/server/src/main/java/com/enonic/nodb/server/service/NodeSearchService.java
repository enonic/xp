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
import com.enonic.nodb.engine.search.SearchDocument;
import com.enonic.nodb.engine.search.SearchDocumentStore;
import com.enonic.nodb.engine.store.RepoKeys;
import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.AwaitRefreshRequest;
import com.enonic.nodb.proto.v1.DeleteDocumentsRequest;
import com.enonic.nodb.proto.v1.IndexAck;
import com.enonic.nodb.proto.v1.IndexDoc;
import com.enonic.nodb.proto.v1.IndexDocumentsRequest;
import com.enonic.nodb.proto.v1.IndexField;
import com.enonic.nodb.proto.v1.IndexValue;
import com.enonic.nodb.proto.v1.NodeSearchGrpc;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.auth.TenantPrincipal;

/**
 * The {@code NodeSearch} write path (Phase 4 Gate A): {@code IndexDocuments},
 * {@code DeleteDocuments}, {@code AwaitRefresh}. {@code Search} and {@code Reindex} stay
 * un-overridden and therefore answer UNIMPLEMENTED — the generated base class's own convention,
 * used throughout this server; the query path is Gates B–E.
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

    private final DataSource dataSource;

    private final Function<TenantContext, Indexer> indexers;

    private final Map<String, Indexer> cache = new LinkedHashMap<>();

    public NodeSearchService( DataSource dataSource, Function<TenantContext, Indexer> indexerFactory )
    {
        this.dataSource = dataSource;
        this.indexers = indexerFactory;
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

    private synchronized Indexer indexer( TenantContext tenant )
    {
        return cache.computeIfAbsent( tenant.tenantId(), id -> indexers.apply( tenant ) );
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
