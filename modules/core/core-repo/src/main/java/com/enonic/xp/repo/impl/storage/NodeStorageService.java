package com.enonic.xp.repo.impl.storage;


import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.PushNodeEntries;
import com.enonic.xp.node.PushNodesListener;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.version.NodeVersionDocumentId;

public interface NodeStorageService
{
    Node move( final MoveNodeParams params, final InternalContext context );

    Node store( final Node node, final InternalContext context );

    Node load( final Node node, final InternalContext context );

    void storeVersion( final StoreNodeVersionParams params, final InternalContext context );

    void delete( final NodeIds nodeIds, final InternalContext context );

    Node updateMetadata( final Node node, final InternalContext context );

    void updateVersion( final Node node, final NodeVersionId nodeVersionId, final InternalContext context );

    void push( final Node node, final Branch target, final InternalContext context );

    void push( final PushNodeEntries entries, final PushNodesListener pushListener, final InternalContext context );

    Node get( final NodeId nodeId, final InternalContext context );

    Node get( final NodePath nodePath, final InternalContext context );

    Nodes get( final NodeIds nodeIds, final boolean keepOrder, final InternalContext context );

    Nodes get( final NodePaths nodePaths, final InternalContext context );

    Node get( final NodeVersionId nodeVersionId, final InternalContext context );

    NodeVersion get( final NodeVersionId nodeVersionId );

    NodeBranchEntry getBranchNodeVersion( final NodeId nodeId, final InternalContext context );

    NodeBranchEntries getBranchNodeVersions( final NodeIds nodeIds, final boolean keepOrder, final InternalContext context );

    NodeVersionMetadata getVersion( final NodeVersionDocumentId versionId, final InternalContext context );

    NodeId getIdForPath( final NodePath nodePath, final InternalContext context );

    void invalidate();

    void handleNodeCreated( final NodeId nodeId, final NodePath nodePath, final InternalContext context );

    void handleNodeDeleted( final NodeId nodeId, final NodePath nodePath, final InternalContext context );

    void handleNodeMoved( final NodeMovedParams params, final InternalContext context );

    void handleNodePushed( final NodeId nodeId, final NodePath nodePath, final NodePath currentTargetPath, InternalContext nodeContext );
}
