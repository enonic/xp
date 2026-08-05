package com.enonic.nodb.server.service;

import java.sql.SQLException;
import javax.sql.DataSource;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.nodb.engine.Tx;
import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.search.SearchIndexAdmin;
import com.enonic.nodb.engine.store.RepositoryLifecycle;
import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.CreateRepositoryRequest;
import com.enonic.nodb.proto.v1.DeleteRepositoryRequest;
import com.enonic.nodb.proto.v1.ExistsResponse;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;
import com.enonic.nodb.proto.v1.RepositoryExistsRequest;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.auth.TenantPrincipal;

/**
 * Management-plane {@code RepositoryAdmin} RPCs implemented this slice: CreateRepository,
 * DeleteRepository. Operator scope is required for those two — enforced by {@link
 * TenantAuthInterceptor}, not re-checked here. Phase 1 Gate A adds {@code
 * RepositoryExists} (spi.RepositoryStorageAdmin#indexExists): deliberately NOT added to
 * {@link TenantAuthInterceptor}'s scope rules — it is a read-only existence
 * probe scoped to the caller's own tenant schema regardless of scope (same "read/exists =
 * data-plane" reasoning {@link NodeStoreService} already
 * draws between its point-lookup RPCs and none of which are mutating admin ops). {@code
 * ListRepositories`/`Stats} are left un-overridden (UNIMPLEMENTED via the generated base
 * class), same convention as {@link NodeStoreService}.
 *
 * <p>Repo lifecycle is DDL (partition create/drop): tenant roles deliberately hold DML-only
 * grants (see {@link com.enonic.nodb.engine.TenantProvisioner}), so these run under {@link
 * Tx#inTenantSchema} — same posture {@code TenantProvisioner}/{@code MigrationRunner} and
 * the engine's own {@code WriteBatchTest} already use for repo/branch lifecycle, never
 * {@link Tx#inTenantTx}.
 */
public final class RepositoryAdminService
    extends RepositoryAdminGrpc.RepositoryAdminImplBase
{
    /**
     * The wire protocol has no separate branch-lifecycle RPC in this slice, so a repo
     * created purely over gRPC would otherwise have no branch to write into (branch_entry
     * has an FK to an existing {@code branch} row). CreateRepository therefore also
     * provisions this one default branch — a deliberate slice-1 simplification, not an
     * engine requirement ({@link RepositoryLifecycle#createBranch} takes any branch name).
     */
    public static final String DEFAULT_BRANCH = "master";

    private static final Logger LOG = LoggerFactory.getLogger( RepositoryAdminService.class );

    private final DataSource dataSource;

    /** {@code null} when this NoDB has no search backend — the whole pre-Gate-F hybrid window. */
    private final SearchIndexAdmin searchIndexAdmin;

    public RepositoryAdminService( DataSource dataSource )
    {
        this( dataSource, null );
    }

    public RepositoryAdminService( DataSource dataSource, SearchIndexAdmin searchIndexAdmin )
    {
        this.dataSource = dataSource;
        this.searchIndexAdmin = searchIndexAdmin;
    }

    /**
     * Repo create also creates the repo's OpenSearch index when a search backend is configured
     * (Phase 4 Gate A): alias {@code <tenant>-<repo>} over {@code <tenant>-<repo>+g1}, with the
     * ported mappings and analyzers.
     *
     * <p>Postgres first, index second, and NOT in one transaction — because they cannot be. The
     * order is chosen so the failure modes are the recoverable ones: a repo with no index yet is a
     * repo whose search is empty until a rebuild (the indexer skips it explicitly, see
     * {@code Indexer#indexNameFor}), whereas an index with no repo row would be an orphan nothing
     * ever cleans up. If index creation fails the RPC fails, so the caller sees it rather than
     * discovering later that queries return nothing.
     */
    @Override
    public void createRepository( CreateRepositoryRequest request, StreamObserver<Ack> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        String settingsJson = request.getSettingsJson().isEmpty() ? null : request.getSettingsJson();

        try
        {
            Tx.inTenantSchema( dataSource, principal.tenantContext(), connection -> {
                long repoKey = RepositoryLifecycle.createRepository( connection, request.getRepoId(), settingsJson );
                RepositoryLifecycle.createBranch( connection, repoKey, DEFAULT_BRANCH );
                return null;
            } );
            if ( searchIndexAdmin != null )
            {
                searchIndexAdmin.createIndex( principal.tenantContext(), request.getRepoId() );
            }
            responseObserver.onNext( Ack.newBuilder().build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( NodeStoreService.mapSqlException( e ) );
        }
        catch ( RuntimeException e )
        {
            responseObserver.onError( Status.INTERNAL.withDescription( e.getMessage() ).withCause( e ).asRuntimeException() );
        }
    }

    /**
     * Index first, Postgres second — the mirror of {@link #createRepository}'s ordering, for the
     * same reason. Dropping the {@code repository} row cascades {@code search_index} away, so doing
     * it first would leave the physical index unreachable through NoDB's own metadata: an orphan
     * that only a name-parsing sweep could find, which DESIGN §5 forbids. An index deleted while
     * the repo row survives is merely a repo whose search is empty.
     */
    @Override
    public void deleteRepository( DeleteRepositoryRequest request, StreamObserver<Ack> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try
        {
            if ( searchIndexAdmin != null )
            {
                try
                {
                    searchIndexAdmin.deleteIndex( principal.tenantContext(), request.getRepoId() );
                }
                catch ( RuntimeException e )
                {
                    // A search backend that is down must not block a repo delete: the storage side
                    // is the system of record, and search is rebuildable by definition. Logged
                    // loudly so the leftover index is findable.
                    LOG.warn( "Failed to delete the search index for repo {}; continuing with the storage delete", request.getRepoId(), e );
                }
            }
            Tx.inTenantSchema( dataSource, principal.tenantContext(), connection -> {
                RepositoryLifecycle.deleteRepository( connection, new RepoRef( request.getRepoId() ) );
                return null;
            } );
            responseObserver.onNext( Ack.newBuilder().build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( NodeStoreService.mapSqlException( e ) );
        }
    }

    @Override
    public void repositoryExists( RepositoryExistsRequest request, StreamObserver<ExistsResponse> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try
        {
            boolean exists = Tx.inTenantTx( dataSource, principal.tenantContext(),
                                             connection -> RepositoryLifecycle.repositoryExists( connection, request.getRepoId() ) );
            responseObserver.onNext( ExistsResponse.newBuilder().setExists( exists ).build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( NodeStoreService.mapSqlException( e ) );
        }
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
