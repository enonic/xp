package com.enonic.xp.storage.nodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
import com.enonic.nodb.proto.v1.StoreBranchEntryRequest;
import com.enonic.nodb.proto.v1.StoreCommitRequest;
import com.enonic.nodb.proto.v1.StoreVersionRequest;
import com.enonic.nodb.proto.v1.Version;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.BranchEntryRecord;
import com.enonic.xp.storage.spi.CommitRecord;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.SearchPreference;
import com.enonic.xp.storage.spi.VersionRecord;

/**
 * gRPC-backed {@link NodeStore}: every method maps onto the corresponding per-op RPC
 * (nodb/BUILD-PHASE-1.md Gate B scope constraint #2 -- per-op RPCs, not {@code WriteBatch};
 * {@code WriteBatch} stays the native/bench-optimized batched path with no XP caller).
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

    // --- versions ---

    @Override
    public void storeVersion( final RepositoryId repositoryId, final VersionRecord version )
    {
        final StoreVersionRequest request = StoreVersionRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setVersion( RecordMapper.toProtoVersion( version ) )
            .build();
        NodbStatusMapper.repoScopedVoid( () -> client.nodeStore().storeVersion( request ) );
    }

    @Override
    public void deleteVersion( final RepositoryId repositoryId, final String versionId )
    {
        final DeleteVersionRequest request = DeleteVersionRequest.newBuilder().setVersionId( versionId ).build();
        NodbStatusMapper.repoScopedVoid( () -> client.nodeStore().deleteVersion( request ) );
    }

    @Override
    @Nullable
    public VersionRecord getVersion( final RepositoryId repositoryId, final String versionId,
                                      final @Nullable SearchPreference searchPreference )
    {
        final GetVersionRequest request = GetVersionRequest.newBuilder().setVersionId( versionId ).build();
        final Version version = NodbStatusMapper.pointGet( () -> client.nodeStore().getVersion( request ) );
        return version == null ? null : RecordMapper.toSpiVersion( version );
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
        final GetCommitRequest request = GetCommitRequest.newBuilder().setCommitId( commitId ).build();
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
