package com.enonic.wem.api.node;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.api.workspace.Workspace;

public interface NodeService
{
    Node create( CreateNodeParams params );

    Node update( UpdateNodeParams params );

    Node rename( RenameNodeParams params );

    Node push( NodeId id, Workspace target );

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

    NodeComparisons compare( final NodeIds ids, final Workspace target );

    FindNodeVersionsResult findVersions( final GetNodeVersionsParams params );

    GetActiveNodeVersionsResult getActiveVersions( final GetActiveNodeVersionsParams params );

    Node setChildOrder( final SetNodeChildOrderParams params );

    ReorderChildNodesResult reorderChildren( final ReorderChildNodesParams params );

    Node getByVersionId( NodeVersionId nodeVersionid );

    void snapshot();

    int applyPermissions( ApplyNodePermissionsParams params );

    ByteSource getBinary( final NodeId nodeId, final BinaryReference reference );
}
