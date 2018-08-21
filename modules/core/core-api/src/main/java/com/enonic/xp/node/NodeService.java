package com.enonic.xp.node;

import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.util.BinaryReference;

@Beta
public interface NodeService
{
    Node create( CreateNodeParams params );

    Node update( UpdateNodeParams params );

    Node rename( RenameNodeParams params );

    PushNodesResult push( NodeIds ids, Branch target );

    PushNodesResult push( NodeIds ids, Branch target, PushNodesListener pushListener );

    NodeIds deleteById( NodeId id );

    NodeIds deleteByPath( NodePath path );

    Node getById( NodeId id );

    Nodes getByIds( NodeIds ids );

    Node getByPath( NodePath path );

    Nodes getByPaths( NodePaths paths );

    Node duplicate( DuplicateNodeParams params );

    Node move( NodeId nodeId, NodePath parentNodePath, MoveNodeListener moveListener );

    Nodes move( NodeIds nodeIds, NodePath parentNodePath, MoveNodeListener moveListener );

    FindNodesByParentResult findByParent( FindNodesByParentParams params );

    FindNodesByQueryResult findByQuery( NodeQuery nodeQuery );

    @Deprecated
    FindNodePathsByQueryResult findNodePathsByQuery( NodeQuery nodeQuery );

    FindNodesByMultiRepoQueryResult findByQuery( MultiRepoNodeQuery nodeQuery );

    NodeComparison compare( NodeId id, Branch target );

    NodeComparisons compare( NodeIds ids, Branch target );

    NodeVersionQueryResult findVersions( GetNodeVersionsParams params );

    NodeVersionQueryResult findVersions( NodeVersionQuery nodeVersionQuery );

    Nodes findInternalDependencies( Map<NodeId, NodePath> sourceNodeIds );

    boolean deleteVersion( NodeVersionId nodeVersionId);

    GetActiveNodeVersionsResult getActiveVersions( GetActiveNodeVersionsParams params );

    NodeVersionId setActiveVersion( NodeId nodeId, NodeVersionId nodeVersionId );

    Node setChildOrder( SetNodeChildOrderParams params );

    ReorderChildNodesResult reorderChildren( ReorderChildNodesParams params );

    NodeVersion getByNodeVersion( NodeVersionId nodeVersionId );

    ResolveSyncWorkResult resolveSyncWork( SyncWorkResolverParams params );

    void refresh( RefreshMode refreshMode );

    int applyPermissions( ApplyNodePermissionsParams params );

    ByteSource getBinary( NodeId nodeId, BinaryReference reference );

    ByteSource getBinary( NodeVersionId nodeVersionId, BinaryReference reference );

    String getBinaryKey( NodeId nodeId, BinaryReference reference );

    Node createRootNode( CreateRootNodeParams params );

    SetNodeStateResult setNodeState( SetNodeStateParams params );

    Node getRoot();

    Node setRootPermissions( AccessControlList accessControlList, boolean inheritPermissions );

    ImportNodeResult importNode( ImportNodeParams params );

    LoadNodeResult loadNode( final LoadNodeParams params );

    NodesHasChildrenResult hasChildren( Nodes nodes );

    boolean hasChildren( Node node );

    boolean nodeExists( NodeId nodeId );

    boolean nodeExists( NodePath nodePath );

    boolean hasUnpublishedChildren( NodeId parent, Branch target );

    void importNodeVersion( final ImportNodeVersionParams params );

}
