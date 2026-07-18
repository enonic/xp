package com.enonic.xp.storage.nodb;

import java.util.Comparator;
import java.util.Map;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

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
import com.enonic.nodb.proto.v1.GetVersionRequest;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.StoreBranchEntryRequest;
import com.enonic.nodb.proto.v1.StoreCommitRequest;
import com.enonic.nodb.proto.v1.StoreVersionRequest;
import com.enonic.nodb.proto.v1.Version;

/**
 * Test-only {@code NodeStore} service backed by {@link FakeNodbState}. Reproduces just
 * enough of nodb.proto's documented status contract (repo-scoped NOT_FOUND, point-get
 * NOT_FOUND) to exercise {@link NodbNodeStore}'s status mapping -- see
 * {@link FakeNodbState}'s javadoc for why this is a stand-in rather than the real engine.
 * <p>
 * Read methods ({@link #getBranchEntry}/{@link #getBranchEntries}) reproduce
 * {@code BranchStore}'s server-side JOIN against {@code node_version} (Phase 1 Gate C N+1
 * fix, BUILD-PHASE-1.md) by looking up the entry's version in {@link FakeNodbState#versions}
 * at read time and stamping the hash fields onto the returned {@code BranchEntry} -- the
 * real server never trusts whatever hash fields happen to be on the stored row, and neither
 * does this stub, so a test that only ever calls {@code storeBranchEntry} still gets the
 * same joined-hash behavior on read.
 */
final class StubNodeStoreService
    extends NodeStoreGrpc.NodeStoreImplBase
{
    private final FakeNodbState state;

    StubNodeStoreService( final FakeNodbState state )
    {
        this.state = state;
    }

    @Override
    public void storeBranchEntry( final StoreBranchEntryRequest request, final StreamObserver<Ack> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        final BranchEntry entry = request.getEntry();
        state.branchEntriesById.put( FakeNodbState.entryKey( request.getRepoId(), entry.getBranch(), entry.getNodeId() ), entry );
        state.nodeIdByPath.put( FakeNodbState.pathKey( request.getRepoId(), entry.getBranch(), entry.getNodePath() ), entry.getNodeId() );
        respondAck( responseObserver );
    }

    @Override
    public void deleteBranchEntries( final DeleteBranchEntriesRequest request, final StreamObserver<Ack> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        for ( final String nodeId : request.getNodeIdsList() )
        {
            final BranchEntry removed = state.branchEntriesById.remove( FakeNodbState.entryKey( request.getRepoId(), request.getBranch(), nodeId ) );
            if ( removed != null )
            {
                state.nodeIdByPath.remove( FakeNodbState.pathKey( request.getRepoId(), request.getBranch(), removed.getNodePath() ) );
            }
        }
        respondAck( responseObserver );
    }

    @Override
    public void existsBranchEntry( final ExistsBranchEntryRequest request, final StreamObserver<ExistsResponse> responseObserver )
    {
        final boolean exists = state.branchEntriesById.containsKey(
            FakeNodbState.entryKey( request.getRepoId(), request.getBranch(), request.getNodeId() ) );
        responseObserver.onNext( ExistsResponse.newBuilder().setExists( exists ).build() );
        responseObserver.onCompleted();
    }

    @Override
    public void getBranchEntry( final GetBranchEntryRequest request, final StreamObserver<BranchEntry> responseObserver )
    {
        final BranchEntry entry;
        if ( !request.getNodeId().isEmpty() )
        {
            entry = state.branchEntriesById.get( FakeNodbState.entryKey( request.getRepoId(), request.getBranch(), request.getNodeId() ) );
        }
        else
        {
            final String nodeId = state.nodeIdByPath.get( FakeNodbState.pathKey( request.getRepoId(), request.getBranch(), request.getNodePath() ) );
            entry = nodeId == null ? null : state.branchEntriesById.get( FakeNodbState.entryKey( request.getRepoId(), request.getBranch(), nodeId ) );
        }
        if ( entry == null )
        {
            responseObserver.onError( Status.NOT_FOUND.withDescription( "No such branch entry" ).asRuntimeException() );
            return;
        }
        responseObserver.onNext( joinHashes( entry ) );
        responseObserver.onCompleted();
    }

    @Override
    public void getBranchEntries( final GetBranchEntriesRequest request, final StreamObserver<BranchEntry> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        for ( final String nodeId : request.getNodeIdsList() )
        {
            final BranchEntry entry = state.branchEntriesById.get( FakeNodbState.entryKey( request.getRepoId(), request.getBranch(), nodeId ) );
            if ( entry != null )
            {
                responseObserver.onNext( joinHashes( entry ) );
            }
        }
        responseObserver.onCompleted();
    }

    /** See class javadoc: reproduces BranchStore's read-side JOIN against node_version. */
    private BranchEntry joinHashes( final BranchEntry entry )
    {
        final Version version = state.versions.get( entry.getVersionId() );
        if ( version == null )
        {
            // Mirrors the real engine's FK guarantee not holding here only if a test builds
            // an inconsistent fixture directly; fail loudly rather than silently return a
            // BranchEntry with empty hash fields.
            throw new IllegalStateException( "branch_entry references version [" + entry.getVersionId() + "] not present in fake state" );
        }
        final BranchEntry.Builder builder = entry.toBuilder().setNodeDataHash( version.getNodeDataHash() );
        if ( !version.getIndexConfigHash().isEmpty() )
        {
            builder.setIndexConfigHash( version.getIndexConfigHash() );
        }
        if ( !version.getAclHash().isEmpty() )
        {
            builder.setAclHash( version.getAclHash() );
        }
        return builder.build();
    }

    @Override
    public void getChildren( final GetChildrenRequest request, final StreamObserver<BranchEntry> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        // Mirrors the real engine's parent_path generated column: "/" itself has no
        // parent-path key (never matches, root is never its own child); direct children of
        // root key on "" (regexp-stripping "/child" from "/child" leaves "").
        final String parentPathKey = "/".equals( request.getParentPath() ) ? "" : request.getParentPath();
        final int size = request.getSize() > 0 ? request.getSize() : Integer.MAX_VALUE;
        state.branchEntriesById.entrySet()
            .stream()
            .filter( e -> e.getKey().startsWith( request.getRepoId() + "|" + request.getBranch() + "|" ) )
            .map( Map.Entry::getValue )
            .filter( entry -> parentPathKey.equals( parentPath( entry.getNodePath() ) ) )
            .sorted( Comparator.comparing( BranchEntry::getNodePath ) )
            .skip( request.getFrom() )
            .limit( size )
            .forEach( entry -> responseObserver.onNext( joinHashes( entry ) ) );
        responseObserver.onCompleted();
    }

    /** Same convention as schema.sql's generated column: null for root itself, "" for root's direct children. */
    private static String parentPath( final String nodePath )
    {
        if ( "/".equals( nodePath ) )
        {
            return null;
        }
        final int lastSlash = nodePath.lastIndexOf( '/' );
        return nodePath.substring( 0, lastSlash );
    }

    @Override
    public void getBranchesWithNode( final GetBranchesWithNodeRequest request, final StreamObserver<BranchRef> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        state.branchEntriesById.forEach( ( key, entry ) -> {
            if ( key.startsWith( request.getRepoId() + "|" ) && entry.getNodeId().equals( request.getNodeId() ) )
            {
                responseObserver.onNext( BranchRef.newBuilder().setBranch( entry.getBranch() ).build() );
            }
        } );
        responseObserver.onCompleted();
    }

    @Override
    public void storeVersion( final StoreVersionRequest request, final StreamObserver<Ack> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        state.versions.put( request.getVersion().getVersionId(), request.getVersion() );
        respondAck( responseObserver );
    }

    @Override
    public void deleteVersion( final DeleteVersionRequest request, final StreamObserver<Ack> responseObserver )
    {
        state.versions.remove( request.getVersionId() );
        respondAck( responseObserver );
    }

    @Override
    public void getVersion( final GetVersionRequest request, final StreamObserver<Version> responseObserver )
    {
        final Version version = state.versions.get( request.getVersionId() );
        if ( version == null )
        {
            responseObserver.onError( Status.NOT_FOUND.withDescription( "No such version" ).asRuntimeException() );
            return;
        }
        responseObserver.onNext( version );
        responseObserver.onCompleted();
    }

    @Override
    public void storeCommit( final StoreCommitRequest request, final StreamObserver<Ack> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        state.commits.put( request.getCommit().getCommitId(), request.getCommit() );
        respondAck( responseObserver );
    }

    @Override
    public void getCommit( final GetCommitRequest request, final StreamObserver<Commit> responseObserver )
    {
        final Commit commit = state.commits.get( request.getCommitId() );
        if ( commit == null )
        {
            responseObserver.onError( Status.NOT_FOUND.withDescription( "No such commit" ).asRuntimeException() );
            return;
        }
        responseObserver.onNext( commit );
        responseObserver.onCompleted();
    }

    private boolean requireRepo( final String repoId, final StreamObserver<?> responseObserver )
    {
        if ( !state.repos.contains( repoId ) )
        {
            responseObserver.onError( Status.NOT_FOUND.withDescription( "Unknown repo id: " + repoId ).asRuntimeException() );
            return false;
        }
        return true;
    }

    private static void respondAck( final StreamObserver<Ack> responseObserver )
    {
        responseObserver.onNext( Ack.newBuilder().build() );
        responseObserver.onCompleted();
    }
}
