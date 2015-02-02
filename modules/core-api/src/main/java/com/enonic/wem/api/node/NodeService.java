package com.enonic.wem.api.node;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.api.workspace.Workspaces;

public interface NodeService
{
    Node create( CreateNodeParams params );

    Node update( UpdateNodeParams params );

    Node rename( RenameNodeParams params );

    PushNodesResult push( NodeIds ids, Workspace target );

    Node deleteById( NodeId id );

    Node deleteByPath( NodePath path );

    Node getById( NodeId id );

    Nodes getByIds( NodeIds ids );

    Node getByPath( NodePath path );

    Nodes getByPaths( NodePaths paths );

    Node duplicate( NodeId nodeId );

    Node move( NodeId nodeId, NodePath parentNodePath );

    FindNodesByParentResult findByParent( FindNodesByParentParams params );

    FindNodesByQueryResult findByQuery( NodeQuery nodeQuery );

    NodeComparison compare( NodeId id, Workspace target );

    NodeComparisons compare( NodeIds ids, Workspace target );

    FindNodeVersionsResult findVersions( GetNodeVersionsParams params );

    GetActiveNodeVersionsResult getActiveVersions( GetActiveNodeVersionsParams params );

    NodeVersionDiffResult diff( NodeVersionDiffQuery query );

    Node setChildOrder( SetNodeChildOrderParams params );

    ReorderChildNodesResult reorderChildren( ReorderChildNodesParams params );

    Node getByVersionId( NodeVersionId nodeVersionid );

    ResolveSyncWorkResult resolveSyncWork( SyncWorkResolverParams params );

    void snapshot();

    int applyPermissions( ApplyNodePermissionsParams params );

    ByteSource getBinary( NodeId nodeId, BinaryReference reference );

    RootNode createRootNode( CreateRootNodeParams params );

    Node setNodeState( final NodeId nodeId, final NodeState nodeState );

    RootNode getRoot();
}
