package com.enonic.nodb.server.service;

import java.sql.SQLException;
import javax.sql.DataSource;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import com.enonic.nodb.engine.Tx;
import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.store.RepositoryLifecycle;
import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.CreateRepositoryRequest;
import com.enonic.nodb.proto.v1.DeleteRepositoryRequest;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.auth.TenantPrincipal;

/**
 * Management-plane {@code RepositoryAdmin} RPCs implemented this slice: CreateRepository,
 * DeleteRepository. Operator scope is required — enforced by {@link
 * TenantAuthInterceptor}, not re-checked here. {@code ListRepositories`/`Stats} are left
 * un-overridden (UNIMPLEMENTED via the generated base class), same convention as {@link
 * NodeStoreService}.
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

    private final DataSource dataSource;

    public RepositoryAdminService( DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

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
            responseObserver.onNext( Ack.newBuilder().build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( NodeStoreService.mapSqlException( e ) );
        }
    }

    @Override
    public void deleteRepository( DeleteRepositoryRequest request, StreamObserver<Ack> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try
        {
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
