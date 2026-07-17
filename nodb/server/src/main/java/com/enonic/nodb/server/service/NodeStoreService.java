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
import com.enonic.nodb.engine.store.PayloadStore;
import com.enonic.nodb.engine.store.VersionStore;
import com.enonic.nodb.engine.store.WriteService;
import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.GetBranchEntryRequest;
import com.enonic.nodb.proto.v1.GetChildrenRequest;
import com.enonic.nodb.proto.v1.GetPayloadRequest;
import com.enonic.nodb.proto.v1.GetVersionRequest;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.Payload;
import com.enonic.nodb.proto.v1.PutPayloadRequest;
import com.enonic.nodb.proto.v1.PutPayloadResponse;
import com.enonic.nodb.proto.v1.Version;
import com.enonic.nodb.proto.v1.WriteBatchRequest;
import com.enonic.nodb.proto.v1.WriteBatchResponse;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.auth.TenantPrincipal;

/**
 * Data-plane {@code NodeStore} RPCs implemented this slice: WriteBatch, GetBranchEntry,
 * GetChildren, GetVersion, PutPayload, GetPayload — wired directly to the engine's
 * stores/{@link WriteService}. Accepts runtime OR operator scope (enforced by {@link
 * TenantAuthInterceptor}, which only requires operator for the RepositoryAdmin
 * management RPCs).
 *
 * <p>Every other method declared on {@code NodeStoreGrpc.NodeStoreImplBase}
 * (StoreBranchEntry, DeleteBranchEntries, GetBranchEntries, StoreVersion, FindVersions,
 * StoreCommit, GetCommit) is intentionally left un-overridden: the generated base class
 * replies {@code UNIMPLEMENTED} for any method a subclass doesn't override, which is
 * exactly the "stub" behavior the work order allows for out-of-scope RPCs — no separate
 * marker code needed.
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

    /** Shared with {@link RepositoryAdminService}: same store/DDL failure modes, same mapping. */
    static StatusRuntimeException mapSqlException( SQLException e )
    {
        if ( e.getMessage() != null && e.getMessage().contains( "Unknown repo id" ) )
        {
            return Status.NOT_FOUND.withDescription( e.getMessage() ).asRuntimeException();
        }
        return Status.INTERNAL.withDescription( e.getMessage() ).withCause( e ).asRuntimeException();
    }
}
