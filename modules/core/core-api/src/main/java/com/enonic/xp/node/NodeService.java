package com.enonic.xp.node;

import java.time.Instant;
import java.util.stream.Stream;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.util.BinaryReference;


@NullMarked
public interface NodeService
{
    Node create( CreateNodeParams params );

    Node update( UpdateNodeParams params );

    PatchNodeResult patch( PatchNodeParams params );

    MoveNodeResult move( MoveNodeParams params );

    PushNodesResult push( PushNodeParams params );

    DeleteNodeResult delete( DeleteNodeParams deleteNodeParams );

    Node getById( NodeId id );

    Node getByIdAndVersionId( NodeId id, NodeVersionId versionId );

    @Nullable NodeVersion getVersion( NodeId nodeId, NodeVersionId nodeVersionId );

    Nodes getByIds( NodeIds ids );

    @Nullable Node getByPath( NodePath path );

    Nodes getByPaths( NodePaths paths );

    DuplicateNodeResult duplicate( DuplicateNodeParams params );

    /**
     * Finds the ids of the children of a node.
     *
     * @deprecated Use {@link #findByQuery(NodeQuery)} with {@link NodeQuery.Builder#parent(NodePath)}. The query form answers the same
     * question and additionally accepts any constraint and return shape a query supports, whereas this method accepts filters only and
     * answers with ids only. It inherits the child order of the parent in the same way;
     * {@link NodeQuery.Builder#recursive(boolean)} replaces {@link FindNodesByParentParams.Builder#recursive(boolean)}, and a count is
     * expressed as a query of size 0. A parent given by id must be resolved to its path first. Scheduled for removal.
     */
    @Deprecated
    FindNodesByParentResult findByParent( FindNodesByParentParams params );

    /**
     * Lists the entire subtree of a node at once.
     * <p>
     * The stream holds one entry for every node below the given parent that the caller is permitted to read, ordered by path, each
     * naming its node by id, path and timestamp. The nodes themselves are not read. The stream may be consumed once and needs no
     * closing.
     * <p>
     * A node is listed once it has been written with {@link RefreshMode#STORAGE} or {@link RefreshMode#ALL} — earlier than
     * {@link #findByQuery(NodeQuery)} answers with it, which additionally waits for the search index. This method refreshes nothing
     * itself, so a node written without any refresh is listed by neither.
     * <p>
     * The whole listing is answered at once, so what it costs grows with the number of nodes in the subtree: a subtree that may hold
     * many should be {@link #enumerate(EnumerateNodesParams) enumerated} instead. Neither filtering nor ordering is offered — use
     * {@link #findByQuery(NodeQuery)} where either is required.
     *
     * @since 8.1.0
     */
    Stream<NodeListEntry> list( ListNodesParams params );

    /**
     * Enumerates the entire subtree of a node in batches. Requires the administrator role.
     * <p>
     * A batch holds at most {@link EnumerateNodesParams.Builder#batchSize(int) batchSize} entries, and the enumeration answers with
     * every node the subtree holds: nothing is left out for want of permission, and a caller without the administrator role is refused
     * rather than answered with less. Repeat the call with the {@link EnumerateNodesResult#getCursor() cursor} of each batch until a
     * batch answers with none — a batch may be empty before then, so the cursor and not the entries says whether the enumeration is
     * finished.
     * <p>
     * A node is observed at most once, whatever is written, deleted or moved while the enumeration is consumed; a node a concurrent
     * write places behind the cursor is not observed at all. The one exception belongs to a bounded enumeration: a node whose timestamp
     * changes to another moment before the bound is observed a second time.
     * <p>
     * As with {@link #list(ListNodesParams)}, a node is enumerated once it has been written with {@link RefreshMode#STORAGE} or
     * {@link RefreshMode#ALL}, and this method refreshes nothing itself.
     * <p>
     * An enumeration {@link EnumerateNodesParams.Builder#modifiedBefore(Instant) bounded by a timestamp} holds only the nodes whose
     * timestamp falls before the bound and arrives oldest first — so a consumer working through a backlog gets it in the order it
     * accumulated. An unbounded enumeration arrives in no specified order.
     *
     * @throws com.enonic.xp.exception.ForbiddenAccessException where the caller lacks the administrator role.
     * @since 8.1.0
     */
    EnumerateNodesResult enumerate( EnumerateNodesParams params );

    FindNodesByQueryResult findByQuery( NodeQuery nodeQuery );

    FindNodesByMultiRepoQueryResult findByQuery( MultiRepoNodeQuery nodeQuery );

    NodeComparison compare( NodeId id, Branch target );

    NodeComparisons compare( NodeIds ids, Branch target );

    GetNodeVersionsResult getVersions( GetNodeVersionsParams params );

    NodeVersionQueryResult findVersions( NodeVersionQuery nodeVersionQuery );

    NodeCommitQueryResult findCommits( NodeCommitQuery nodeCommitQuery );

    GetActiveNodeVersionsResult getActiveVersions( GetActiveNodeVersionsParams params );

    SortNodeResult sort( SortNodeParams params );

    ResolveSyncWorkResult resolveSyncWork( SyncWorkResolverParams params );

    void refresh( RefreshMode refreshMode );

    ApplyNodePermissionsResult applyPermissions( ApplyNodePermissionsParams params );

    ByteSource getBinary( NodeId nodeId, BinaryReference reference );

    ByteSource getBinary( NodeId nodeId, NodeVersionId nodeVersionId, BinaryReference reference );

    ImportNodeResult importNode( ImportNodeParams params );

    NodeCommitEntry commit( CommitNodeParams params );

    NodeCommitEntry commit( NodeCommitEntry nodeCommitEntry, NodeIds nodeIds );

    @Nullable NodeCommitEntry getCommit( NodeCommitId nodeCommitId );

    boolean nodeExists( NodeId nodeId );

    boolean nodeExists( NodePath nodePath );

    boolean hasUnpublishedChildren( NodeId parent, Branch target );

    /**
     * Applies the specified attribute changes to a particular node version.
     * Adds new attributes and removes existing attributes on the target version.
     * <p>
     * If an attribute with the same key already exists, its value is replaced.
     * If the same key is present in both the addAttributes set and the removeAttributes set, the attribute is added.
     *
     * @param params parameters describing the target {@code NodeVersionId}, the attributes to add,
     *               and the attribute keys to remove
     * @return resulting attributes
     */
    Attributes applyVersionAttributes( ApplyVersionAttributesParams params );
}
