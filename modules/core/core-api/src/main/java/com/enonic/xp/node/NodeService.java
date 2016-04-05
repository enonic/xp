package com.enonic.xp.node;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.util.BinaryReference;

@Beta
public interface NodeService
{
    Node create( CreateNodeParams params );

    Node update( UpdateNodeParams params );

    Node rename( RenameNodeParams params );

    PushNodesResult push( NodeIds ids, Branch target );

    Node deleteById( NodeId id );

    Node deleteByPath( NodePath path );

    Node getById( NodeId id );

    Nodes getByIds( NodeIds ids );

    Node getByPath( NodePath path );

    Nodes getByPaths( NodePaths paths );

    Node duplicate( NodeId nodeId );

    Node move( NodeId nodeId, NodePath parentNodePath );

    Nodes move( NodeIds nodeIds, NodePath parentNodePath );

    FindNodesByParentResult findByParent( FindNodesByParentParams params );

    FindNodesByQueryResult findByQuery( NodeQuery nodeQuery );

    NodeComparison compare( NodeId id, Branch target );

    NodeComparisons compare( NodeIds ids, Branch target );

    NodeVersionQueryResult findVersions( GetNodeVersionsParams params );

    GetActiveNodeVersionsResult getActiveVersions( GetActiveNodeVersionsParams params );

    NodeVersionId setActiveVersion( final NodeId nodeId, final NodeVersionId nodeVersionId );

    Node setChildOrder( SetNodeChildOrderParams params );

    ReorderChildNodesResult reorderChildren( ReorderChildNodesParams params );

    NodeVersion getByNodeVersion( NodeVersionMetadata nodeVersionMetadata );

    NodeIds resolveSyncWork( SyncWorkResolverParams params );

    SnapshotResult snapshot( SnapshotParams params );

    RestoreResult restore( RestoreParams params );

    DeleteSnapshotsResult deleteSnapshot( final DeleteSnapshotParams param );

    SnapshotResults listSnapshots();

    void refresh( RefreshMode refreshMode );

    void deleteSnapshotRespository();

    int applyPermissions( ApplyNodePermissionsParams params );

    ByteSource getBinary( NodeId nodeId, BinaryReference reference );

    String getBinaryKey( final NodeId nodeId, final BinaryReference reference );

    Node createRootNode( CreateRootNodeParams params );

    SetNodeStateResult setNodeState( final SetNodeStateParams params );

    Node getRoot();

    ImportNodeResult importNode( final ImportNodeParams params );

    NodesHasChildrenResult hasChildren( final Nodes nodes );

    boolean hasChildren( final Node node );

    boolean nodeExists( final NodeId nodeId );

    boolean nodeExists( final NodePath nodePath );
}
