package com.enonic.nodb.server.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import com.enonic.nodb.engine.Tx;
import com.enonic.nodb.engine.model.Page;
import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.store.BranchStore;
import com.enonic.nodb.engine.store.CommitStore;
import com.enonic.nodb.engine.store.PayloadStore;
import com.enonic.nodb.engine.store.UnknownRepoException;
import com.enonic.nodb.engine.store.VersionStore;
import com.enonic.nodb.engine.store.WriteService;
import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.BranchRef;
import com.enonic.nodb.proto.v1.Commit;
import com.enonic.nodb.proto.v1.DeleteBranchEntriesRequest;
import com.enonic.nodb.proto.v1.DeleteVersionRequest;
import com.enonic.nodb.proto.v1.ExistsBranchEntryRequest;
import com.enonic.nodb.proto.v1.ExistsResponse;
import com.enonic.nodb.proto.v1.GetBranchEntriesRequest;
import com.enonic.nodb.proto.v1.GetBranchEntryRequest;
import com.enonic.nodb.proto.v1.GetBranchesWithNodeRequest;
import com.enonic.nodb.proto.v1.GetChildrenRequest;
import com.enonic.nodb.proto.v1.GetCommitRequest;
import com.enonic.nodb.proto.v1.GetPayloadRequest;
import com.enonic.nodb.proto.v1.GetPayloadsRequest;
import com.enonic.nodb.proto.v1.GetVersionRequest;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.Payload;
import com.enonic.nodb.proto.v1.PutPayloadRequest;
import com.enonic.nodb.proto.v1.PutPayloadResponse;
import com.enonic.nodb.proto.v1.StoreBranchEntryRequest;
import com.enonic.nodb.proto.v1.StoreCommitRequest;
import com.enonic.nodb.proto.v1.StoreVersionRequest;
import com.enonic.nodb.proto.v1.Version;
import com.enonic.nodb.proto.v1.WriteBatchRequest;
import com.enonic.nodb.proto.v1.WriteBatchResponse;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.auth.TenantPrincipal;

/**
 * Data-plane {@code NodeStore} RPCs — wired directly to the engine's stores/{@link
 * WriteService}. Slice 1 implemented WriteBatch, GetBranchEntry, GetChildren, GetVersion,
 * PutPayload, GetPayload. Phase 1 Gate A (BUILD-PHASE-1.md's SPI&lt;-&gt;proto
 * reconciliation table) adds the standalone per-op mirrors of every other {@code
 * spi.NodeStore} method that has a real NoDB translation: StoreBranchEntry,
 * DeleteBranchEntries, ExistsBranchEntry, GetBranchEntries, GetBranchesWithNode,
 * StoreVersion, DeleteVersion, StoreCommit, GetCommit. Accepts runtime OR operator scope
 * (enforced by {@link TenantAuthInterceptor}, which only requires operator for the
 * RepositoryAdmin management RPCs).
 *
 * <p>Every method declared on {@code NodeStoreGrpc.NodeStoreImplBase} that is still out of
 * scope entirely (FindVersions — no XP SPI equivalent) is left un-overridden: the
 * generated base class replies {@code UNIMPLEMENTED} for any method a subclass doesn't
 * override, which is exactly the "stub" behavior the work order allows for out-of-scope
 * RPCs — no separate marker code needed.
 *
 * <p>Tenant is taken ONLY from {@link TenantAuthInterceptor#PRINCIPAL_KEY} (populated by
 * the auth interceptor from the verified JWT) — request messages never carry a tenant
 * field (DESIGN.md §7.2). Every store call runs inside {@link Tx#inTenantTx}, which issues
 * {@code SET LOCAL ROLE <tenant>} so a bug here can never cross a schema boundary.
 */
public final class NodeStoreService
    extends NodeStoreGrpc.NodeStoreImplBase
{
    /** GetChildren page size when the caller doesn't specify one (proto3 default 0). */
    private static final int DEFAULT_PAGE_SIZE = 10_000;

    private final DataSource dataSource;

    public NodeStoreService( DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    @Override
    public void writeBatch( WriteBatchRequest request, StreamObserver<WriteBatchResponse> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();

        List<com.enonic.nodb.engine.store.PayloadRef> payloads = new ArrayList<>();
        for ( com.enonic.nodb.proto.v1.PayloadRef ref : request.getPayloadsList() )
        {
            payloads.add( ProtoMapper.toEnginePayloadRef( ref ) );
        }
        List<com.enonic.nodb.engine.model.VersionRecord> versions = new ArrayList<>();
        for ( Version v : request.getVersionsList() )
        {
            versions.add( ProtoMapper.toEngineVersion( v ) );
        }
        List<com.enonic.nodb.engine.model.BranchEntryRecord> branchEntries = new ArrayList<>();
        for ( BranchEntry e : request.getBranchEntriesList() )
        {
            branchEntries.add( ProtoMapper.toEngineBranchEntry( e ) );
        }
        com.enonic.nodb.engine.model.CommitRecord commit = request.hasCommit() ? ProtoMapper.toEngineCommit( request.getCommit() ) : null;

        com.enonic.nodb.engine.store.WriteBatchRequest engineRequest =
            new com.enonic.nodb.engine.store.WriteBatchRequest( new RepoRef( request.getRepoId() ), payloads, versions, branchEntries,
                                                                  commit );

        try
        {
            com.enonic.nodb.engine.store.WriteBatchResponse engineResponse =
                Tx.inTenantTx( dataSource, principal.tenantContext(), connection -> WriteService.write( connection, engineRequest ) );

            WriteBatchResponse.Builder builder = WriteBatchResponse.newBuilder().addAllNeedPayload( engineResponse.needPayload() );
            if ( engineResponse.outboxSeq() != null )
            {
                builder.setOutboxSeq( engineResponse.outboxSeq() );
            }
            responseObserver.onNext( builder.build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void getBranchEntry( GetBranchEntryRequest request, StreamObserver<BranchEntry> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        RepoRef repo = new RepoRef( request.getRepoId() );

        try
        {
            com.enonic.nodb.engine.model.BranchEntryRecord result =
                Tx.inTenantTx( dataSource, principal.tenantContext(), connection -> switch ( request.getByCase() )
                {
                    case NODE_ID -> BranchStore.getByNodeId( connection, repo, request.getBranch(), request.getNodeId() );
                    case NODE_PATH -> BranchStore.getByPath( connection, repo, request.getBranch(), request.getNodePath() );
                    case BY_NOT_SET -> throw new IllegalArgumentException( "Either node_id or node_path must be set" );
                } );

            if ( result == null )
            {
                responseObserver.onError( Status.NOT_FOUND.withDescription( "No such branch entry" ).asRuntimeException() );
                return;
            }
            responseObserver.onNext( ProtoMapper.fromEngineBranchEntry( result ) );
            responseObserver.onCompleted();
        }
        catch ( IllegalArgumentException e )
        {
            responseObserver.onError( Status.INVALID_ARGUMENT.withDescription( e.getMessage() ).asRuntimeException() );
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void storeBranchEntry( StoreBranchEntryRequest request, StreamObserver<Ack> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        RepoRef repo = new RepoRef( request.getRepoId() );
        com.enonic.nodb.engine.model.BranchEntryRecord entry = ProtoMapper.toEngineBranchEntry( request.getEntry() );

        try
        {
            Tx.inTenantTx( dataSource, principal.tenantContext(), connection -> {
                BranchStore.store( connection, repo, entry );
                return null;
            } );
            responseObserver.onNext( Ack.newBuilder().build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void deleteBranchEntries( DeleteBranchEntriesRequest request, StreamObserver<Ack> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        RepoRef repo = new RepoRef( request.getRepoId() );

        try
        {
            Tx.inTenantTx( dataSource, principal.tenantContext(), connection -> {
                BranchStore.delete( connection, repo, request.getBranch(), request.getNodeIdsList() );
                return null;
            } );
            responseObserver.onNext( Ack.newBuilder().build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void existsBranchEntry( ExistsBranchEntryRequest request, StreamObserver<ExistsResponse> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        RepoRef repo = new RepoRef( request.getRepoId() );

        try
        {
            boolean exists = Tx.inTenantTx( dataSource, principal.tenantContext(),
                                             connection -> BranchStore.existsByNodeId( connection, repo, request.getBranch(),
                                                                                       request.getNodeId() ) );
            responseObserver.onNext( ExistsResponse.newBuilder().setExists( exists ).build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void getBranchEntries( GetBranchEntriesRequest request, StreamObserver<BranchEntry> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        RepoRef repo = new RepoRef( request.getRepoId() );

        try
        {
            List<com.enonic.nodb.engine.model.BranchEntryRecord> entries =
                Tx.inTenantTx( dataSource, principal.tenantContext(),
                                connection -> BranchStore.getByNodeIds( connection, repo, request.getBranch(),
                                                                         request.getNodeIdsList() ) );
            for ( com.enonic.nodb.engine.model.BranchEntryRecord entry : entries )
            {
                responseObserver.onNext( ProtoMapper.fromEngineBranchEntry( entry ) );
            }
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void getBranchesWithNode( GetBranchesWithNodeRequest request, StreamObserver<BranchRef> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        RepoRef repo = new RepoRef( request.getRepoId() );

        try
        {
            List<String> branches = Tx.inTenantTx( dataSource, principal.tenantContext(),
                                                     connection -> BranchStore.getBranchesWithNode( connection, repo,
                                                                                                     request.getNodeId() ) );
            for ( String branch : branches )
            {
                responseObserver.onNext( BranchRef.newBuilder().setBranch( branch ).build() );
            }
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void getChildren( GetChildrenRequest request, StreamObserver<BranchEntry> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        RepoRef repo = new RepoRef( request.getRepoId() );
        int size = request.getSize() > 0 ? request.getSize() : DEFAULT_PAGE_SIZE;
        Page page = new Page( request.getFrom(), size );

        try
        {
            List<com.enonic.nodb.engine.model.BranchEntryRecord> children =
                Tx.inTenantTx( dataSource, principal.tenantContext(),
                                connection -> BranchStore.getChildren( connection, repo, request.getBranch(), request.getParentPath(),
                                                                        page ) );
            for ( com.enonic.nodb.engine.model.BranchEntryRecord entry : children )
            {
                responseObserver.onNext( ProtoMapper.fromEngineBranchEntry( entry ) );
            }
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void getVersion( GetVersionRequest request, StreamObserver<Version> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try
        {
            com.enonic.nodb.engine.model.VersionRecord result =
                Tx.inTenantTx( dataSource, principal.tenantContext(),
                                connection -> VersionStore.get( connection, request.getVersionId() ) );
            if ( result == null )
            {
                responseObserver.onError( Status.NOT_FOUND.withDescription( "No such version" ).asRuntimeException() );
                return;
            }
            responseObserver.onNext( ProtoMapper.fromEngineVersion( result ) );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void storeVersion( StoreVersionRequest request, StreamObserver<Ack> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        RepoRef repo = new RepoRef( request.getRepoId() );
        com.enonic.nodb.engine.model.VersionRecord version = ProtoMapper.toEngineVersion( request.getVersion() );

        try
        {
            Tx.inTenantTx( dataSource, principal.tenantContext(), connection -> {
                VersionStore.store( connection, repo, version );
                return null;
            } );
            responseObserver.onNext( Ack.newBuilder().build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void deleteVersion( DeleteVersionRequest request, StreamObserver<Ack> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try
        {
            Tx.inTenantTx( dataSource, principal.tenantContext(), connection -> {
                VersionStore.delete( connection, request.getVersionId() );
                return null;
            } );
            responseObserver.onNext( Ack.newBuilder().build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void storeCommit( StoreCommitRequest request, StreamObserver<Ack> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        RepoRef repo = new RepoRef( request.getRepoId() );
        com.enonic.nodb.engine.model.CommitRecord commit = ProtoMapper.toEngineCommit( request.getCommit() );

        try
        {
            Tx.inTenantTx( dataSource, principal.tenantContext(), connection -> {
                CommitStore.store( connection, repo, commit );
                return null;
            } );
            responseObserver.onNext( Ack.newBuilder().build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void getCommit( GetCommitRequest request, StreamObserver<Commit> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try
        {
            com.enonic.nodb.engine.model.CommitRecord result =
                Tx.inTenantTx( dataSource, principal.tenantContext(),
                                connection -> CommitStore.get( connection, request.getCommitId() ) );
            if ( result == null )
            {
                responseObserver.onError( Status.NOT_FOUND.withDescription( "No such commit" ).asRuntimeException() );
                return;
            }
            responseObserver.onNext( ProtoMapper.fromEngineCommit( result ) );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void putPayload( PutPayloadRequest request, StreamObserver<PutPayloadResponse> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        byte[] bytes = request.getBytes().toByteArray();
        try
        {
            String hash =
                Tx.inTenantTx( dataSource, principal.tenantContext(), connection -> PayloadStore.putPayload( connection, bytes ) );
            responseObserver.onNext( PutPayloadResponse.newBuilder().setHash( hash ).build() );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void getPayload( GetPayloadRequest request, StreamObserver<Payload> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try
        {
            byte[] bytes = Tx.inTenantTx( dataSource, principal.tenantContext(),
                                           connection -> PayloadStore.getPayload( connection, request.getHash() ) );
            if ( bytes == null )
            {
                responseObserver.onError( Status.NOT_FOUND.withDescription( "No such payload" ).asRuntimeException() );
                return;
            }
            responseObserver.onNext( ProtoMapper.fromEnginePayload( request.getHash(), bytes ) );
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    @Override
    public void getPayloads( GetPayloadsRequest request, StreamObserver<Payload> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try
        {
            List<com.enonic.nodb.engine.model.PayloadRecord> results =
                Tx.inTenantTx( dataSource, principal.tenantContext(),
                                connection -> PayloadStore.getPayloads( connection, request.getHashesList() ) );
            for ( com.enonic.nodb.engine.model.PayloadRecord result : results )
            {
                responseObserver.onNext( ProtoMapper.fromEnginePayload( result.hash(), result.bytes() ) );
            }
            responseObserver.onCompleted();
        }
        catch ( SQLException e )
        {
            responseObserver.onError( mapSqlException( e ) );
        }
    }

    private static TenantPrincipal currentPrincipal()
    {
        TenantPrincipal principal = TenantAuthInterceptor.PRINCIPAL_KEY.get();
        if ( principal == null )
        {
            // Defense in depth: every registered service is wrapped with the auth
            // interceptor in NodbServer, so this is unreachable in practice.
            throw Status.UNAUTHENTICATED.withDescription( "No authenticated tenant in context" ).asRuntimeException();
        }
        return principal;
    }

    /**
     * Shared with {@link RepositoryAdminService}: same store/DDL failure modes, same
     * mapping — see the status-mapping comment block in nodb.proto for the full contract.
     *
     * <p>Both checks below are structural (exception type / SQLSTATE), not message-text
     * matching: the original slice-1 implementation matched {@code "Unknown repo id"} as a
     * substring of the exception message, which is fragile (Gate A bug fix, flagged in
     * BUILD-PHASE-1.md's reconciliation table) and never had an {@code ALREADY_EXISTS}
     * mapping at all — a duplicate {@code CreateRepository} fell all the way through to
     * {@code INTERNAL}.
     */
    static StatusRuntimeException mapSqlException( SQLException e )
    {
        if ( e instanceof UnknownRepoException )
        {
            return Status.NOT_FOUND.withDescription( e.getMessage() ).asRuntimeException();
        }
        // PostgreSQL SQLSTATE 23505 = unique_violation (e.g. a duplicate repository.repo_id
        // on CreateRepository) -> ALREADY_EXISTS, which the XP-side nodb-client translates
        // to StorageIndexExistsException.
        if ( "23505".equals( e.getSQLState() ) )
        {
            return Status.ALREADY_EXISTS.withDescription( e.getMessage() ).asRuntimeException();
        }
        // PostgreSQL SQLSTATE 23503 = foreign_key_violation -- most commonly a version
        // whose node_data_hash/index_config_hash/acl_hash has no matching `payload` row
        // (Phase 3 Gate A's re-added FK, BUILD-PHASE-3.md #10b) -> FAILED_PRECONDITION, not
        // NOT_FOUND: NOT_FOUND above is reserved for point-lookup READS in this codebase's
        // convention, whereas this is a write rejected because a precondition (the
        // referenced payload must already be stored) was not met -- FAILED_PRECONDITION's
        // canonical gRPC meaning fits exactly. See nodb.proto's status-mapping table.
        if ( "23503".equals( e.getSQLState() ) )
        {
            return Status.FAILED_PRECONDITION.withDescription( e.getMessage() ).asRuntimeException();
        }
        return Status.INTERNAL.withDescription( e.getMessage() ).withCause( e ).asRuntimeException();
    }
}
