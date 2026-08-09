package com.enonic.xp.node;

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
     * @deprecated Use {@link #findByQuery(NodeQuery)} with {@link NodeQuery.Builder#parent(NodePath)}, which answers the same question
     * and accepts every constraint and return shape a query can carry, where this takes filters alone and answers with ids alone. It
     * inherits the child order of the parent the same way, {@link NodeQuery.Builder#recursive(boolean)} replaces
     * {@link FindNodesByParentParams.Builder#recursive(boolean)}, and a count is a query of size 0. Resolve a parent given by id to its
     * path first. Scheduled for removal.
     */
    @Deprecated
    FindNodesByParentResult findByParent( FindNodesByParentParams params );

    /**
     * Lists the children of a node, or with {@link ListNodesByParentParams.Builder#recursive(boolean)} its whole subtree.
     * <p>
     * Every node the caller may read is listed, ordered by path, as an id, a path and a timestamp - never the node itself. A node written
     * a moment ago is listed, where {@link #findByQuery(NodeQuery)} may not find it until the search index catches up.
     * <p>
     * There is no paging, no filtering and no choice of order: ask a query when any of those matter.
     *
     * @since 8.1.0
     */
    ListNodesByParentResult list( ListNodesByParentParams params );

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
