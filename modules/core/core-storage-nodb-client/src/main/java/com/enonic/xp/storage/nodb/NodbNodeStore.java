package com.enonic.xp.storage.nodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.protobuf.ByteString;

import com.enonic.nodb.proto.v1.ActiveVersion;
import com.enonic.nodb.proto.v1.BranchEntryOrder;
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
import com.enonic.nodb.proto.v1.GetVersionRequest;
import com.enonic.nodb.proto.v1.ListBranchEntriesRequest;
import com.enonic.nodb.proto.v1.ListBranchEntriesResponse;
import com.enonic.nodb.proto.v1.PayloadRef;
import com.enonic.nodb.proto.v1.StoreBranchEntryRequest;
import com.enonic.nodb.proto.v1.StoreCommitRequest;
import com.enonic.nodb.proto.v1.Version;
import com.enonic.nodb.proto.v1.WriteBatchRequest;
import com.enonic.nodb.proto.v1.WriteBatchResponse;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.BranchEntryListing;
import com.enonic.xp.storage.spi.BranchEntryRecord;
import com.enonic.xp.storage.spi.CommitRecord;
import com.enonic.xp.storage.spi.NodeSegments;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.PayloadSegment;
import com.enonic.xp.storage.spi.SearchPreference;
import com.enonic.xp.storage.spi.VersionQuery;
import com.enonic.xp.storage.spi.VersionQueryResult;
import com.enonic.xp.storage.spi.VersionRecord;

/**
 * gRPC-backed {@link NodeStore}: most methods map onto the corresponding per-op RPC
 * (nodb/BUILD-PHASE-1.md Gate B scope constraint #2). {@link #storeVersion} and
 * {@link #storeNode} are the exception (Phase 3 Gate B, nodb/BUILD-PHASE-3.md's "symmetric
 * B" decision): a version write always carries its three payload segments now (the
 * re-added {@code node_version} payload FK requires it), so both ride {@code WriteBatch} --
 * ONE transaction, payloads inserted before the version row (server-side, Gate A) -- rather
 * than the old per-op {@code StoreVersion} RPC, which has no field for them. The per-op
 * {@code StoreBranchEntry}/{@code StoreVersion} RPCs remain defined in the proto/engine for
 * other callers; this client simply no longer uses {@code StoreVersion} for its own writes.
 * <p>
 * Registered with {@code storage.backend=nodb} and a positive {@code service.ranking} so it
 * outranks the elasticsearch-backed {@code NodeStore} (Phase 0, ranking 0/default) when
 * both are present -- see {@link NodbStorageClient}'s class javadoc for the full selection
 * mechanism and its unreachable-endpoint failure mode.
 * <p>
 * {@code searchPreference} is accepted on every method for interface conformance and never
 * forwarded to the wire: NoDB reads are direct, transactionally-consistent Postgres reads
 * with no replica lag in scope for Phase 1 (nodb.proto's "never sent over the wire" note).
 * <p>
 * Change-feed cache invalidation (multi-replica cache coherency) is out of scope for this
 * client in Phase 1 -- single-JVM itests don't exercise it; see nodb/DESIGN.md's ChangeFeed
 * section for the deferred design.
 */
@Component(service = NodeStore.class, property = { "storage.backend=nodb", "service.ranking:Integer=100" })
public class NodbNodeStore
    implements NodeStore
{
    /**
     * Page size of a branch-entry walk ({@link #listChildEntries}/{@link #listBranchEntries}).
     * Large enough that an ordinary subtree or small branch finishes in one round trip, small
     * enough that a million-node branch never materializes on either side.
     */
    private static final int BRANCH_ENTRY_PAGE_SIZE = 1000;

    private final NodbStorageClient client;

    @Activate
    public NodbNodeStore( @Reference final NodbStorageClient client )
    {
        this.client = client;
    }

    // --- branch entries ---

    @Override
    public void storeBranchEntry( final RepositoryId repositoryId, final Branch branch, final BranchEntryRecord entry )
    {
        final StoreBranchEntryRequest request = StoreBranchEntryRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setEntry( RecordMapper.toProtoBranchEntry( branch, entry ) )
            .build();
        NodbStatusMapper.repoScopedVoid( () -> client.nodeStore().storeBranchEntry( request ) );
    }

    @Override
    public void deleteBranchEntries( final RepositoryId repositoryId, final Branch branch, final Collection<String> nodeIds )
    {
        final DeleteBranchEntriesRequest request = DeleteBranchEntriesRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setBranch( branch.getValue() )
            .addAllNodeIds( nodeIds )
            .build();
        NodbStatusMapper.repoScopedVoid( () -> client.nodeStore().deleteBranchEntries( request ) );
    }

    @Override
    public boolean existsBranchEntry( final RepositoryId repositoryId, final Branch branch, final String nodeId,
                                       final @Nullable SearchPreference searchPreference )
    {
        final ExistsBranchEntryRequest request = ExistsBranchEntryRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setBranch( branch.getValue() )
            .setNodeId( nodeId )
            .build();
        return NodbStatusMapper.existsCheck( () -> client.nodeStore().existsBranchEntry( request ).getExists() );
    }

    @Override
    @Nullable
    public BranchEntryRecord getBranchEntry( final RepositoryId repositoryId, final Branch branch, final String nodeId,
                                              final @Nullable SearchPreference searchPreference )
    {
        final GetBranchEntryRequest request = GetBranchEntryRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setBranch( branch.getValue() )
            .setNodeId( nodeId )
            .build();
        return toSpiBranchEntryOrNull( NodbStatusMapper.pointGet( () -> client.nodeStore().getBranchEntry( request ) ) );
    }

    @Override
    @Nullable
    public BranchEntryRecord getBranchEntryByPath( final RepositoryId repositoryId, final Branch branch, final String nodePath,
                                                    final @Nullable SearchPreference searchPreference )
    {
        // No client-side refresh call: branch_entry is the row of record for NoDB, so the
        // SPI's "force a refresh before path lookup" requirement is already trivially
        // satisfied -- see spi.NodeStore#getBranchEntryByPath's javadoc and nodb.proto's
        // "never sent over the wire" note.
        final GetBranchEntryRequest request = GetBranchEntryRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setBranch( branch.getValue() )
            .setNodePath( nodePath )
            .build();
        return toSpiBranchEntryOrNull( NodbStatusMapper.pointGet( () -> client.nodeStore().getBranchEntry( request ) ) );
    }

    @Override
    public List<BranchEntryRecord> getBranchEntries( final RepositoryId repositoryId, final Branch branch,
                                                       final Collection<String> nodeIds,
                                                       final @Nullable SearchPreference searchPreference )
    {
        final GetBranchEntriesRequest request = GetBranchEntriesRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setBranch( branch.getValue() )
            .addAllNodeIds( nodeIds )
            .build();
        final List<BranchEntryRecord> result = new ArrayList<>();
        // Server-side JOIN (BranchStore.JOINED_SELECT) already fetches node_version's hash
        // columns in the same query -- no follow-up GetVersion per entry (Phase 1 Gate C
        // N+1 fix, BUILD-PHASE-1.md). Streaming RPCs surface StatusRuntimeException while
        // iterating, not on the initial call, so the whole consumption (call + iteration)
        // is wrapped as one unit.
        NodbStatusMapper.repoScopedVoid( () -> {
            final Iterator<com.enonic.nodb.proto.v1.BranchEntry> entries = client.nodeStore().getBranchEntries( request );
            entries.forEachRemaining( entry -> result.add( RecordMapper.toSpiBranchEntry( entry ) ) );
        } );
        return result;
    }

    @Override
    public List<Branch> getBranchesWithNode( final RepositoryId repositoryId, final String nodeId )
    {
        final GetBranchesWithNodeRequest request =
            GetBranchesWithNodeRequest.newBuilder().setRepoId( repositoryId.toString() ).setNodeId( nodeId ).build();
        final List<Branch> result = new ArrayList<>();
        // See getBranchEntries above: streaming RPC, wrap call + iteration together.
        NodbStatusMapper.repoScopedVoid( () -> {
            final Iterator<BranchRef> refs = client.nodeStore().getBranchesWithNode( request );
            refs.forEachRemaining( ref -> result.add( Branch.from( ref.getBranch() ) ) );
        } );
        return result;
    }

    /**
     * Overrides the SPI default (see {@link NodeStore#getChildren}'s javadoc): NoDB's
     * {@code branch_entry.parent_path} generated column serves this directly (server-side
     * {@code BranchStore.getChildren}), the storage-side capability the ES backend does not
     * have -- Phase 1 Gate C's SPI addition.
     */
    @Override
    public List<BranchEntryRecord> getChildren( final RepositoryId repositoryId, final Branch branch, final String parentPath,
                                                 final int from, final int size, final @Nullable SearchPreference searchPreference )
    {
        final GetChildrenRequest request = GetChildrenRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setBranch( branch.getValue() )
            .setParentPath( parentPath )
            .setFrom( from )
            .setSize( size )
            .build();
        final List<BranchEntryRecord> result = new ArrayList<>();
        // See getBranchEntries above: streaming RPC, wrap call + iteration together.
        NodbStatusMapper.repoScopedVoid( () -> {
            final Iterator<com.enonic.nodb.proto.v1.BranchEntry> entries = client.nodeStore().getChildren( request );
            entries.forEachRemaining( entry -> result.add( RecordMapper.toSpiBranchEntry( entry ) ) );
        } );
        return result;
    }

    // --- branch-entry listing (Phase 4 decision D2, nodb/BUILD-PHASE-4.md) ---

    /**
     * Capability probe (see {@link NodeStore#supportsBranchEntryQueries}): NoDB answers the
     * delete cascade's and reindex's branch-entry walks from {@code branch_entry} in Postgres,
     * so those commands route here instead of querying the ES storage index nodb mode never
     * creates.
     */
    @Override
    public boolean supportsBranchEntryQueries()
    {
        return true;
    }

    @Override
    public BranchEntryListing listChildEntries( final RepositoryId repositoryId, final Branch branch, final String pathPrefix )
    {
        return listing( repositoryId, branch, pathPrefix, BranchEntryOrder.BRANCH_ENTRY_ORDER_PATH_DESC );
    }

    @Override
    public BranchEntryListing listBranchEntries( final RepositoryId repositoryId, final Branch branch )
    {
        return listing( repositoryId, branch, null, BranchEntryOrder.BRANCH_ENTRY_ORDER_PATH_ASC );
    }

    /**
     * Fetches the FIRST page eagerly and the rest lazily.
     * <p>
     * The first round trip is unavoidable: it is the one that carries {@code total_hits}, and
     * {@link BranchEntryListing#totalHits} is needed before iteration starts (the reindex listener
     * reports it up front). Every later page is fetched only when the consumer's iterator walks
     * off the end of the current one, which is what makes a whole-branch walk O(page) in memory on
     * both sides -- a reindex loop does substantial work per entry, so a design that held one gRPC
     * stream and one server transaction open for the entire walk would be the wrong shape.
     */
    private BranchEntryListing listing( final RepositoryId repositoryId, final Branch branch, final @Nullable String pathPrefix,
                                         final BranchEntryOrder order )
    {
        final ListBranchEntriesResponse first = page( repositoryId, branch, pathPrefix, order, "", "" );
        return new BranchEntryListing( first.getTotalHits(),
                                        () -> new PagingIterator( repositoryId, branch, pathPrefix, order, first ) );
    }

    private ListBranchEntriesResponse page( final RepositoryId repositoryId, final Branch branch, final @Nullable String pathPrefix,
                                             final BranchEntryOrder order, final String afterPath, final String afterNodeId )
    {
        final ListBranchEntriesRequest.Builder builder = ListBranchEntriesRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setBranch( branch.getValue() )
            .setOrder( order )
            .setAfterPath( afterPath )
            .setAfterNodeId( afterNodeId )
            .setPageSize( BRANCH_ENTRY_PAGE_SIZE );
        if ( pathPrefix != null )
        {
            builder.setPathPrefix( pathPrefix );
        }
        return NodbStatusMapper.repoScoped( () -> client.nodeStore().listBranchEntries( builder.build() ) );
    }

    /**
     * Keyset walk over {@code (lower(node_path), node_id)}. The cursor is the server's own
     * {@code next_after_*} pair, never a value this side derives from a returned path: the
     * predicate is evaluated by PostgreSQL's {@code lower()} under the database collation, and a
     * second lowercasing here is how a walk would silently skip or repeat rows.
     */
    private final class PagingIterator
        implements Iterator<BranchEntryRecord>
    {
        private final RepositoryId repositoryId;

        private final Branch branch;

        private final @Nullable String pathPrefix;

        private final BranchEntryOrder order;

        private ListBranchEntriesResponse current;

        private int index;

        private PagingIterator( final RepositoryId repositoryId, final Branch branch, final @Nullable String pathPrefix,
                                 final BranchEntryOrder order, final ListBranchEntriesResponse first )
        {
            this.repositoryId = repositoryId;
            this.branch = branch;
            this.pathPrefix = pathPrefix;
            this.order = order;
            this.current = first;
        }

        @Override
        public boolean hasNext()
        {
            while ( index >= current.getEntriesCount() )
            {
                if ( !current.getHasMore() )
                {
                    return false;
                }
                current = page( repositoryId, branch, pathPrefix, order, current.getNextAfterPath(), current.getNextAfterNodeId() );
                index = 0;
            }
            return true;
        }

        @Override
        public BranchEntryRecord next()
        {
            if ( !hasNext() )
            {
                throw new java.util.NoSuchElementException();
            }
            return RecordMapper.toSpiBranchEntry( current.getEntries( index++ ) );
        }
    }

    // --- versions ---

    @Override
    public void storeVersion( final RepositoryId repositoryId, final VersionRecord version, final NodeSegments segments )
    {
        final WriteBatchRequest request = WriteBatchRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .addAllPayloads( toPayloadRefs( segments ) )
            .addVersions( RecordMapper.toProtoVersion( version ) )
            .build();
        writeBatch( request );
    }

    /**
     * ONE {@code WriteBatch} RPC per save (Phase 3 Gate B, nodb/BUILD-PHASE-3.md): version +
     * branch entry + payload segments as a single transaction, instead of the default
     * {@code storeVersion} then {@code storeBranchEntry} sequence (two RPCs) that
     * {@code NodeStore#storeNode}'s default method would otherwise produce.
     */
    @Override
    public void storeNode( final RepositoryId repositoryId, final Branch branch, final NodeSegments segments, final VersionRecord version,
                            final BranchEntryRecord branchEntry )
    {
        final WriteBatchRequest request = WriteBatchRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .addAllPayloads( toPayloadRefs( segments ) )
            .addVersions( RecordMapper.toProtoVersion( version ) )
            .addBranchEntries( RecordMapper.toProtoBranchEntry( branch, branchEntry ) )
            .build();
        writeBatch( request );
    }

    private void writeBatch( final WriteBatchRequest request )
    {
        final WriteBatchResponse response = NodbStatusMapper.repoScoped( () -> client.nodeStore().writeBatch( request ) );
        if ( !response.getNeedPayloadList().isEmpty() )
        {
            // v1 always sends inline bytes for a segment whose content is new (see
            // #toPayloadRef) and hash-only ONLY for a segment the caller itself already
            // knows is stored (VersionServiceImpl's commit/change-attributes convenience
            // overload, reusing an existing NodeVersionKey verbatim) -- NEED_PAYLOAD can
            // only be returned for a hash-only ref the server does NOT have, which should
            // never happen under that discipline. Surfaced loudly rather than silently
            // dropping the write (WriteService.write does not persist anything when
            // needPayload is non-empty -- see nodb/engine's WriteService javadoc).
            throw new NodbClientException(
                "NoDB WriteBatch reported missing payload(s) the caller believed were already stored: " +
                    response.getNeedPayloadList() );
        }
    }

    private static Iterable<PayloadRef> toPayloadRefs( final NodeSegments segments )
    {
        return List.of( toPayloadRef( segments.nodeData() ), toPayloadRef( segments.indexConfig() ),
                         toPayloadRef( segments.accessControl() ) );
    }

    private static PayloadRef toPayloadRef( final PayloadSegment segment )
    {
        return segment.bytes() == null
            ? PayloadRef.newBuilder().setHash( segment.hash() ).build()
            : PayloadRef.newBuilder().setInline( ByteString.copyFrom( segment.bytes() ) ).build();
    }

    @Override
    public void deleteVersion( final RepositoryId repositoryId, final String versionId )
    {
        final DeleteVersionRequest request =
            DeleteVersionRequest.newBuilder().setRepoId( repositoryId.toString() ).setVersionId( versionId ).build();
        NodbStatusMapper.repoScopedVoid( () -> client.nodeStore().deleteVersion( request ) );
    }

    @Override
    @Nullable
    public VersionRecord getVersion( final RepositoryId repositoryId, final String versionId,
                                      final @Nullable SearchPreference searchPreference )
    {
        final GetVersionRequest request =
            GetVersionRequest.newBuilder().setRepoId( repositoryId.toString() ).setVersionId( versionId ).build();
        final Version version = NodbStatusMapper.pointGet( () -> client.nodeStore().getVersion( request ) );
        return version == null ? null : RecordMapper.toSpiVersion( version );
    }

    // --- storage-index query family (Phase 3.5 Gate A, nodb/BUILD-PHASE-3.5.md) ---

    /**
     * Capability probe (see {@link NodeStore#supportsVersionQueries}): NoDB serves the
     * whole family from Postgres, so commands may route {@link #findVersions}/
     * {@link #diffBranches}/{@link #getActiveVersions}/{@link #findCommits} here instead
     * of the ES storage-index flow.
     */
    @Override
    public boolean supportsVersionQueries()
    {
        return true;
    }

    @Override
    public VersionQueryResult findVersions( final RepositoryId repositoryId, final VersionQuery query )
    {
        final FindVersionsRequest request =
            RecordMapper.toProtoVersionQuery( query ).setRepoId( repositoryId.toString() ).build();
        final FindVersionsResponse response = NodbStatusMapper.repoScoped( () -> client.nodeStore().findVersions( request ) );
        final List<VersionRecord> versions = new ArrayList<>();
        for ( final Version version : response.getVersionsList() )
        {
            versions.add( RecordMapper.toSpiVersion( version ) );
        }
        return new VersionQueryResult( response.getTotalHits(), versions );
    }

    @Override
    public List<String> diffBranches( final RepositoryId repositoryId, final Branch source, final Branch target,
                                       final @Nullable String pathScope, final Collection<String> excludes, final int limit )
    {
        final DiffBranchesRequest.Builder builder = DiffBranchesRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setSourceBranch( source.getValue() )
            .setTargetBranch( target.getValue() )
            .addAllExcludePaths( excludes )
            .setLimit( limit );
        if ( pathScope != null )
        {
            builder.setPathScope( pathScope );
        }
        final DiffBranchesResponse response = NodbStatusMapper.repoScoped( () -> client.nodeStore().diffBranches( builder.build() ) );
        return List.copyOf( response.getNodeIdsList() );
    }

    @Override
    public Map<Branch, VersionRecord> getActiveVersions( final RepositoryId repositoryId, final String nodeId,
                                                          final Collection<Branch> branches )
    {
        final GetActiveVersionsRequest.Builder builder =
            GetActiveVersionsRequest.newBuilder().setRepoId( repositoryId.toString() ).setNodeId( nodeId );
        for ( final Branch branch : branches )
        {
            builder.addBranches( branch.getValue() );
        }
        final GetActiveVersionsResponse response =
            NodbStatusMapper.repoScoped( () -> client.nodeStore().getActiveVersions( builder.build() ) );
        final Map<Branch, VersionRecord> result = new LinkedHashMap<>();
        for ( final ActiveVersion activeVersion : response.getActiveVersionsList() )
        {
            result.put( Branch.from( activeVersion.getBranch() ), RecordMapper.toSpiVersion( activeVersion.getVersion() ) );
        }
        return result;
    }

    @Override
    public List<CommitRecord> findCommits( final RepositoryId repositoryId )
    {
        final FindCommitsRequest request = FindCommitsRequest.newBuilder().setRepoId( repositoryId.toString() ).build();
        final List<CommitRecord> result = new ArrayList<>();
        // See getBranchEntries above: streaming RPC, wrap call + iteration together.
        NodbStatusMapper.repoScopedVoid( () -> {
            final Iterator<Commit> commits = client.nodeStore().findCommits( request );
            commits.forEachRemaining( commit -> result.add( RecordMapper.toSpiCommit( commit ) ) );
        } );
        return result;
    }

    // --- commits ---

    @Override
    public void storeCommit( final RepositoryId repositoryId, final CommitRecord commit )
    {
        final StoreCommitRequest request = StoreCommitRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setCommit( RecordMapper.toProtoCommit( commit ) )
            .build();
        NodbStatusMapper.repoScopedVoid( () -> client.nodeStore().storeCommit( request ) );
    }

    @Override
    @Nullable
    public CommitRecord getCommit( final RepositoryId repositoryId, final String commitId,
                                    final @Nullable SearchPreference searchPreference )
    {
        final GetCommitRequest request =
            GetCommitRequest.newBuilder().setRepoId( repositoryId.toString() ).setCommitId( commitId ).build();
        final Commit commit = NodbStatusMapper.pointGet( () -> client.nodeStore().getCommit( request ) );
        return commit == null ? null : RecordMapper.toSpiCommit( commit );
    }

    /**
     * {@code proto.BranchEntry} now carries the joined {@code node_data_hash}/
     * {@code index_config_hash}/{@code acl_hash} fields directly (server-side JOIN against
     * {@code node_version} -- Phase 1 Gate C N+1 fix, BUILD-PHASE-1.md), so this is a plain
     * null-safe mapping, not a follow-up round trip.
     */
    @Nullable
    private static BranchEntryRecord toSpiBranchEntryOrNull( final com.enonic.nodb.proto.v1.@Nullable BranchEntry entry )
    {
        return entry == null ? null : RecordMapper.toSpiBranchEntry( entry );
    }
}
