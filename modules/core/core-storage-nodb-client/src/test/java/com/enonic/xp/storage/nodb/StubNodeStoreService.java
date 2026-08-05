package com.enonic.xp.storage.nodb;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import com.google.common.io.ByteSource;
import com.google.protobuf.ByteString;

import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.ActiveVersion;
import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.BranchRef;
import com.enonic.nodb.proto.v1.Commit;
import com.enonic.nodb.proto.v1.DeleteBranchEntriesRequest;
import com.enonic.nodb.proto.v1.DeleteVersionRequest;
import com.enonic.nodb.proto.v1.DiffBranchesRequest;
import com.enonic.nodb.proto.v1.DiffBranchesResponse;
import com.enonic.nodb.proto.v1.ExistsBranchEntryRequest;
import com.enonic.nodb.proto.v1.ExistsResponse;
import com.enonic.nodb.proto.v1.FindCommitsRequest;
import com.enonic.nodb.proto.v1.FindVersionsRequest;
import com.enonic.nodb.proto.v1.FindVersionsResponse;
import com.enonic.nodb.proto.v1.GetActiveVersionsRequest;
import com.enonic.nodb.proto.v1.GetActiveVersionsResponse;
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
import com.enonic.nodb.proto.v1.PayloadRef;
import com.enonic.nodb.proto.v1.PutPayloadRequest;
import com.enonic.nodb.proto.v1.PutPayloadResponse;
import com.enonic.nodb.proto.v1.StoreBranchEntryRequest;
import com.enonic.nodb.proto.v1.StoreCommitRequest;
import com.enonic.nodb.proto.v1.StoreVersionRequest;
import com.enonic.nodb.proto.v1.Version;
import com.enonic.nodb.proto.v1.WriteBatchRequest;
import com.enonic.nodb.proto.v1.WriteBatchResponse;

import com.enonic.xp.blob.BlobKey;

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
        responseObserver.onNext( joinHashes( request.getRepoId(), entry ) );
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
                responseObserver.onNext( joinHashes( request.getRepoId(), entry ) );
            }
        }
        responseObserver.onCompleted();
    }

    /** See class javadoc: reproduces BranchStore's read-side JOIN against node_version. */
    private BranchEntry joinHashes( final String repoId, final BranchEntry entry )
    {
        final Version version = state.versions.get( FakeNodbState.versionKey( repoId, entry.getVersionId() ) );
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
            .forEach( entry -> responseObserver.onNext( joinHashes( request.getRepoId(), entry ) ) );
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
        state.versions.put( FakeNodbState.versionKey( request.getRepoId(), request.getVersion().getVersionId() ), request.getVersion() );
        respondAck( responseObserver );
    }

    @Override
    public void deleteVersion( final DeleteVersionRequest request, final StreamObserver<Ack> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        state.versions.remove( FakeNodbState.versionKey( request.getRepoId(), request.getVersionId() ) );
        respondAck( responseObserver );
    }

    @Override
    public void getVersion( final GetVersionRequest request, final StreamObserver<Version> responseObserver )
    {
        final Version version = state.versions.get( FakeNodbState.versionKey( request.getRepoId(), request.getVersionId() ) );
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
        state.commits.put( FakeNodbState.commitKey( request.getRepoId(), request.getCommit().getCommitId() ), request.getCommit() );
        respondAck( responseObserver );
    }

    @Override
    public void getCommit( final GetCommitRequest request, final StreamObserver<Commit> responseObserver )
    {
        final Commit commit = state.commits.get( FakeNodbState.commitKey( request.getRepoId(), request.getCommitId() ) );
        if ( commit == null )
        {
            responseObserver.onError( Status.NOT_FOUND.withDescription( "No such commit" ).asRuntimeException() );
            return;
        }
        responseObserver.onNext( commit );
        responseObserver.onCompleted();
    }

    @Override
    public void findCommits( final FindCommitsRequest request, final StreamObserver<Commit> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        final String prefix = request.getRepoId() + "|";
        state.commits.entrySet()
            .stream()
            .filter( e -> e.getKey().startsWith( prefix ) )
            .map( Map.Entry::getValue )
            .sorted( Comparator.comparingLong( Commit::getTimestampMillis ).thenComparing( Commit::getCommitId ) )
            .forEach( responseObserver::onNext );
        responseObserver.onCompleted();
    }

    /** Mirrors VersionStore.findVersions' predicate/order/paging semantics in memory (see VersionQuery's javadoc for the conventions). */
    @Override
    public void findVersions( final FindVersionsRequest request, final StreamObserver<FindVersionsResponse> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        final String prefix = request.getRepoId() + "|";
        final List<Version> matches = new ArrayList<>( state.versions.entrySet()
                                                           .stream()
                                                           .filter( e -> e.getKey().startsWith( prefix ) )
                                                           .map( Map.Entry::getValue )
                                                           .filter( v -> request.getNodeId().isEmpty() ||
                                                               v.getNodeId().equals( request.getNodeId() ) )
                                                           .filter( v -> request.getTsFloorMillis() == 0 ||
                                                               v.getTimestampMillis() >= request.getTsFloorMillis() )
                                                           .filter( v -> request.getTsCeilingMillis() == 0 ||
                                                               v.getTimestampMillis() <= request.getTsCeilingMillis() )
                                                           .filter( v -> request.getVersionIdAfter().isEmpty() ||
                                                               v.getVersionId().compareTo( request.getVersionIdAfter() ) > 0 )
                                                           .filter( v -> matchesBlobKey( request, v ) )
                                                           .filter( v -> matchesCursor( request, v ) )
                                                           .toList() );
        switch ( request.getOrder() )
        {
            case VERSION_ORDER_TS_DESC_ID_ASC -> matches.sort(
                Comparator.comparingLong( Version::getTimestampMillis ).reversed().thenComparing( Version::getVersionId ) );
            case VERSION_ORDER_ID_ASC -> matches.sort( Comparator.comparing( Version::getVersionId ) );
            default ->
            {
            }
        }
        final FindVersionsResponse.Builder builder = FindVersionsResponse.newBuilder().setTotalHits( matches.size() );
        if ( request.getSize() != 0 )
        {
            Stream<Version> page = matches.stream().skip( request.getFrom() );
            if ( request.getSize() > 0 )
            {
                page = page.limit( request.getSize() );
            }
            page.forEach( builder::addVersions );
        }
        responseObserver.onNext( builder.build() );
        responseObserver.onCompleted();
    }

    private static boolean matchesBlobKey( final FindVersionsRequest request, final Version version )
    {
        if ( !request.hasBlobKey() )
        {
            return true;
        }
        return switch ( request.getBlobKey().getField() )
        {
            case BLOB_KEY_FIELD_BINARY_KEYS -> version.getBinaryKeysList().contains( request.getBlobKey().getBlobKey() );
            case BLOB_KEY_FIELD_NODE_DATA_HASH -> version.getNodeDataHash().equals( request.getBlobKey().getBlobKey() );
            case UNRECOGNIZED -> false;
        };
    }

    private static boolean matchesCursor( final FindVersionsRequest request, final Version version )
    {
        if ( !request.hasCursor() )
        {
            return true;
        }
        return version.getTimestampMillis() < request.getCursor().getTsMillis() ||
            ( version.getTimestampMillis() == request.getCursor().getTsMillis() &&
                version.getVersionId().compareTo( request.getCursor().getVersionId() ) > 0 );
    }

    /** Mirrors BranchStore.diffBranches' pinned semantics (per-side case-insensitive scope, exact-path excludes, UNION dedup). */
    @Override
    public void diffBranches( final DiffBranchesRequest request, final StreamObserver<DiffBranchesResponse> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        final Map<String, BranchEntry> source = entriesOfBranch( request.getRepoId(), request.getSourceBranch() );
        final Map<String, BranchEntry> target = entriesOfBranch( request.getRepoId(), request.getTargetBranch() );
        final Set<String> nodeIds = new LinkedHashSet<>();
        source.forEach( ( nodeId, entry ) -> {
            final BranchEntry other = target.get( nodeId );
            if ( ( other == null || !other.getVersionId().equals( entry.getVersionId() ) ) && sideMatches( request, entry ) )
            {
                nodeIds.add( nodeId );
            }
        } );
        target.forEach( ( nodeId, entry ) -> {
            final BranchEntry other = source.get( nodeId );
            if ( ( other == null || !other.getVersionId().equals( entry.getVersionId() ) ) && sideMatches( request, entry ) )
            {
                nodeIds.add( nodeId );
            }
        } );
        Stream<String> result = nodeIds.stream();
        if ( request.getLimit() > 0 )
        {
            result = result.limit( request.getLimit() );
        }
        final DiffBranchesResponse.Builder builder = DiffBranchesResponse.newBuilder();
        result.forEach( builder::addNodeIds );
        responseObserver.onNext( builder.build() );
        responseObserver.onCompleted();
    }

    private Map<String, BranchEntry> entriesOfBranch( final String repoId, final String branch )
    {
        final String prefix = repoId + "|" + branch + "|";
        final Map<String, BranchEntry> result = new LinkedHashMap<>();
        state.branchEntriesById.forEach( ( key, entry ) -> {
            if ( key.startsWith( prefix ) )
            {
                result.put( entry.getNodeId(), entry );
            }
        } );
        return result;
    }

    private static boolean sideMatches( final DiffBranchesRequest request, final BranchEntry entry )
    {
        final String path = entry.getNodePath().toLowerCase();
        if ( !request.getPathScope().isEmpty() )
        {
            final String scope = request.getPathScope().toLowerCase();
            if ( !path.equals( scope ) && !path.startsWith( scope + "/" ) )
            {
                return false;
            }
        }
        return request.getExcludePathsList().stream().noneMatch( exclude -> exclude.toLowerCase().equals( path ) );
    }

    @Override
    public void getActiveVersions( final GetActiveVersionsRequest request, final StreamObserver<GetActiveVersionsResponse> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        final GetActiveVersionsResponse.Builder builder = GetActiveVersionsResponse.newBuilder();
        for ( final String branch : request.getBranchesList() )
        {
            final BranchEntry entry =
                state.branchEntriesById.get( FakeNodbState.entryKey( request.getRepoId(), branch, request.getNodeId() ) );
            if ( entry != null )
            {
                final Version version = state.versions.get( FakeNodbState.versionKey( request.getRepoId(), entry.getVersionId() ) );
                if ( version != null )
                {
                    builder.addActiveVersions( ActiveVersion.newBuilder().setBranch( branch ).setVersion( version ) );
                }
            }
        }
        responseObserver.onNext( builder.build() );
        responseObserver.onCompleted();
    }

    /**
     * Reproduces just enough of {@code nodb/engine}'s {@code WriteService.write} (Phase 3
     * Gate B, nodb/BUILD-PHASE-3.md) to exercise {@link NodbNodeStore#storeVersion}/
     * {@link NodbNodeStore#storeNode}: validate every hash-only {@link PayloadRef} is
     * already in {@link FakeNodbState#payloads} BEFORE writing anything (an unknown hash
     * short-circuits with {@code needPayload} populated and nothing persisted, mirroring the
     * real engine's pre-check ordering); an inline ref's hash is always recomputed from its
     * bytes (never trusted from the client), same sha256 scheme {@code BlobKey.sha256} and
     * the real {@code PayloadStore.sha256Key} both use, so the two independently agree.
     */
    @Override
    public void writeBatch( final WriteBatchRequest request, final StreamObserver<WriteBatchResponse> responseObserver )
    {
        if ( !requireRepo( request.getRepoId(), responseObserver ) )
        {
            return;
        }
        final List<String> needPayload = new ArrayList<>();
        for ( final PayloadRef ref : request.getPayloadsList() )
        {
            if ( ref.getRefCase() == PayloadRef.RefCase.HASH && !state.payloads.containsKey( ref.getHash() ) )
            {
                needPayload.add( ref.getHash() );
            }
        }
        if ( !needPayload.isEmpty() )
        {
            responseObserver.onNext( WriteBatchResponse.newBuilder().addAllNeedPayload( needPayload ).build() );
            responseObserver.onCompleted();
            return;
        }

        for ( final PayloadRef ref : request.getPayloadsList() )
        {
            if ( ref.getRefCase() == PayloadRef.RefCase.INLINE )
            {
                state.payloads.put( sha256( ref.getInline() ), ref.getInline() );
            }
        }
        for ( final Version version : request.getVersionsList() )
        {
            state.versions.put( FakeNodbState.versionKey( request.getRepoId(), version.getVersionId() ), version );
        }
        for ( final BranchEntry entry : request.getBranchEntriesList() )
        {
            state.branchEntriesById.put( FakeNodbState.entryKey( request.getRepoId(), entry.getBranch(), entry.getNodeId() ), entry );
            state.nodeIdByPath.put( FakeNodbState.pathKey( request.getRepoId(), entry.getBranch(), entry.getNodePath() ),
                                     entry.getNodeId() );
        }
        responseObserver.onNext( WriteBatchResponse.newBuilder().build() );
        responseObserver.onCompleted();
    }

    @Override
    public void putPayload( final PutPayloadRequest request, final StreamObserver<PutPayloadResponse> responseObserver )
    {
        final String hash = sha256( request.getBytes() );
        state.payloads.put( hash, request.getBytes() );
        responseObserver.onNext( PutPayloadResponse.newBuilder().setHash( hash ).build() );
        responseObserver.onCompleted();
    }

    @Override
    public void getPayload( final GetPayloadRequest request, final StreamObserver<Payload> responseObserver )
    {
        final ByteString bytes = state.payloads.get( request.getHash() );
        if ( bytes == null )
        {
            responseObserver.onError( Status.NOT_FOUND.withDescription( "No such payload" ).asRuntimeException() );
            return;
        }
        responseObserver.onNext( Payload.newBuilder().setHash( request.getHash() ).setBytes( bytes ).build() );
        responseObserver.onCompleted();
    }

    @Override
    public void getPayloads( final GetPayloadsRequest request, final StreamObserver<Payload> responseObserver )
    {
        for ( final String hash : request.getHashesList() )
        {
            final ByteString bytes = state.payloads.get( hash );
            if ( bytes != null )
            {
                responseObserver.onNext( Payload.newBuilder().setHash( hash ).setBytes( bytes ).build() );
            }
        }
        responseObserver.onCompleted();
    }

    private static String sha256( final ByteString bytes )
    {
        return BlobKey.sha256( ByteSource.wrap( bytes.toByteArray() ) ).toString();
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
