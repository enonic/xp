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
        return joinBranchEntry( NodbStatusMapper.pointGet( () -> client.nodeStore().getBranchEntry( request ) ) );
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
        return joinBranchEntry( NodbStatusMapper.pointGet( () -> client.nodeStore().getBranchEntry( request ) ) );
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
        // No batched multi-get for the joined Version rows (FindVersions was not built in
        // Gate A -- see nodb.proto's Phase 1 method list): one GetVersion call per entry.
        // Known Phase 1 perf cost (N+1), documented rather than silently eaten.
        // Streaming RPCs surface StatusRuntimeException while iterating, not on the initial
        // call, so the whole consumption (call + iteration) is wrapped as one unit.
        NodbStatusMapper.repoScopedVoid( () -> {
            final Iterator<com.enonic.nodb.proto.v1.BranchEntry> entries = client.nodeStore().getBranchEntries( request );
            entries.forEachRemaining( entry -> result.add( joinBranchEntry( entry ) ) );
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
     * {@code proto.BranchEntry} doesn't carry {@code nodeDataHash}/{@code indexConfigHash}/
     * {@code aclHash} (see {@link RecordMapper}'s class javadoc) -- this joins in the
     * {@code node_version} row via a follow-up {@code GetVersion} call to recover them. A
     * documented extra round trip per branch-entry read, not an oversight.
     */
    @Nullable
    private BranchEntryRecord joinBranchEntry( final com.enonic.nodb.proto.v1.@Nullable BranchEntry entry )
    {
        if ( entry == null )
        {
            return null;
        }
        final GetVersionRequest versionRequest = GetVersionRequest.newBuilder().setVersionId( entry.getVersionId() ).build();
        final Version version = NodbStatusMapper.pointGet( () -> client.nodeStore().getVersion( versionRequest ) );
        if ( version == null )
        {
            throw new NodbClientException(
                "branch_entry for node [" + entry.getNodeId() + "] references version [" + entry.getVersionId() +
                    "] which no longer exists" );
        }
        return RecordMapper.toSpiBranchEntry( entry, version );
    }
}
