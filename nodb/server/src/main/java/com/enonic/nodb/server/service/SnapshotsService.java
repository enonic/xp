package com.enonic.nodb.server.service;

import java.sql.SQLException;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import com.enonic.nodb.engine.snapshot.SnapshotRecord;
import com.enonic.nodb.engine.snapshot.SnapshotService;
import com.enonic.nodb.proto.v1.CreateSnapshotRequest;
import com.enonic.nodb.proto.v1.DeleteSnapshotRequest;
import com.enonic.nodb.proto.v1.DeleteSnapshotResponse;
import com.enonic.nodb.proto.v1.ListSnapshotsRequest;
import com.enonic.nodb.proto.v1.SnapshotInfo;
import com.enonic.nodb.proto.v1.SnapshotsGrpc;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.auth.TenantPrincipal;

/**
 * The {@code Snapshots} RPCs (Phase 5 Gate A): CreateSnapshot (repo- and tenant-scoped),
 * ListSnapshots, DeleteSnapshot — thin delegation to {@link SnapshotService}, following
 * {@link RepositoryAdminService}'s shape (principal from the interceptor, engine exception
 * mapping via {@link NodeStoreService#mapSqlException}). {@code Restore} is Gate B and
 * stays un-overridden (UNIMPLEMENTED via the generated base class).
 *
 * <p>Authz posture is UNCHANGED from the existing services (interim, stated in the proto's
 * service comment): runtime or operator scope accepted; Gate E owns the management-plane
 * move that puts snapshot ops behind operator scope.
 */
public final class SnapshotsService
    extends SnapshotsGrpc.SnapshotsImplBase
{
    private final SnapshotService snapshotService;

    public SnapshotsService( SnapshotService snapshotService )
    {
        this.snapshotService = snapshotService;
    }

    @Override
    public void createSnapshot( CreateSnapshotRequest request, StreamObserver<SnapshotInfo> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try
        {
            SnapshotRecord record = request.getRepoId().isEmpty()
                ? snapshotService.createTenantSnapshot( principal.tenantContext() )
                : snapshotService.createRepoSnapshot( principal.tenantContext(), request.getRepoId() );
            responseObserver.onNext( toInfo( record ) );
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

    @Override
    public void listSnapshots( ListSnapshotsRequest request, StreamObserver<SnapshotInfo> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try
        {
            for ( SnapshotRecord record : snapshotService.list( principal.tenantContext() ) )
            {
                responseObserver.onNext( toInfo( record ) );
            }
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( NodeStoreService.mapSqlException( e ) );
        }
    }

    @Override
    public void deleteSnapshot( DeleteSnapshotRequest request, StreamObserver<DeleteSnapshotResponse> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try
        {
            boolean deleted = snapshotService.delete( principal.tenantContext(), request.getSnapshotId() );
            responseObserver.onNext( DeleteSnapshotResponse.newBuilder().setDeleted( deleted ).build() );
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
        catch ( RuntimeException e )
        {
            responseObserver.onError( Status.INTERNAL.withDescription( e.getMessage() ).withCause( e ).asRuntimeException() );
        }
    }

    private static SnapshotInfo toInfo( SnapshotRecord record )
    {
        SnapshotInfo.Builder builder = SnapshotInfo.newBuilder()
            .setSnapshotId( record.snapshotId() )
            .setScope( record.scope() )
            .setCreatedAtMillis( record.createdAt().toEpochMilli() )
            .setExpiresAtMillis( record.expiresAt().toEpochMilli() )
            .setOutboxSeq( record.outboxSeq() )
            .setState( record.state() );
        if ( record.repoId() != null )
        {
            builder.setRepoId( record.repoId() );
        }
        if ( record.versionCount() != null )
        {
            builder.setVersionCount( record.versionCount() );
        }
        if ( record.headCount() != null )
        {
            builder.setHeadCount( record.headCount() );
        }
        if ( record.commitCount() != null )
        {
            builder.setCommitCount( record.commitCount() );
        }
        if ( record.documentCount() != null )
        {
            builder.setDocumentCount( record.documentCount() );
        }
        if ( record.hashCount() != null )
        {
            builder.setHashCount( record.hashCount() );
        }
        if ( record.totalBytes() != null )
        {
            builder.setTotalBytes( record.totalBytes() );
        }
        if ( record.manifestSha256() != null )
        {
            builder.setManifestSha256( record.manifestSha256() );
        }
        return builder.build();
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
