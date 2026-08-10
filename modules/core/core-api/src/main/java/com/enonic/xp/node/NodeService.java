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
     * @deprecated Use {@link #findByQuery(NodeQuery)} with {@link NodeQuery.Builder#parent(NodePath)}. The query form answers the same
     * question and additionally accepts any constraint and return shape a query supports, whereas this method accepts filters only and
     * answers with ids only. It inherits the child order of the parent in the same way;
     * {@link NodeQuery.Builder#recursive(boolean)} replaces {@link FindNodesByParentParams.Builder#recursive(boolean)}, and a count is
     * expressed as a query of size 0. A parent given by id must be resolved to its path first. Scheduled for removal.
     */
    @Deprecated
    FindNodesByParentResult findByParent( FindNodesByParentParams params );

    /**
     * Lists the children of a node, or its entire subtree when {@link ListNodesParams.Builder#recursive(boolean)} is set.
     * <p>
     * Every node the caller is permitted to read is listed, ordered by path, and is represented by its id, path and timestamp. The nodes
     * themselves are not read.
     * <p>
     * The listing is served from storage rather than from the search index. A node stored with {@link RefreshMode#STORAGE} or
     * {@link RefreshMode#ALL} is therefore listed immediately, whereas {@link #findByQuery(NodeQuery)} returns it only once the search
     * index has been refreshed. This method performs no refresh of its own; a node stored without a refresh is visible to neither.
     * <p>
     * Paging, filtering and ordering are not supported. Use a query where any of them is required.
     *
     * @since 8.1.0
     */
    ListNodesResult list( ListNodesParams params );

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
