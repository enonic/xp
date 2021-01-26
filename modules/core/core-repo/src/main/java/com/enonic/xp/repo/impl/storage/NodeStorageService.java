package com.enonic.xp.repo.impl.storage;


import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
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
import com.enonic.xp.node.RoutableNodeVersionIds;
import com.enonic.xp.repo.impl.InternalContext;

public interface NodeStorageService
{
    Node move( StoreMovedNodeParams params, InternalContext context );

    Node store( Node node, InternalContext context );

    Node load( LoadNodeParams params, InternalContext context );

    void storeVersion( StoreNodeVersionParams params, InternalContext context );

    void storeCommit( StoreNodeCommitParams params, InternalContext context );

    void delete( NodeIds nodeIds, InternalContext context );

    Node updateMetadata( Node node, InternalContext context );

    void updateVersion( Node node, NodeVersionId nodeVersionId, InternalContext context );

    void push( Node node, Branch target, InternalContext context );

    void push( PushNodeEntries entries, PushNodesListener pushListener, InternalContext context );

    NodeCommitEntry commit( NodeCommitEntry entry, RoutableNodeVersionIds routableNodeVersionIds, InternalContext context );

    Node get( NodeId nodeId, InternalContext context );

    Node get( NodePath nodePath, InternalContext context );

    Nodes get( NodeIds nodeIds, boolean keepOrder, InternalContext context );

    Nodes get( NodePaths nodePaths, InternalContext context );

    Node get( NodeId nodeId, NodeVersionId nodeVersionId, InternalContext context );

    NodeVersion getNodeVersion( NodeVersionKey nodeVersionKey, InternalContext context );

    NodeBranchEntry getBranchNodeVersion( NodeId nodeId, InternalContext context );

    NodeBranchEntries getBranchNodeVersions( NodeIds nodeIds, boolean keepOrder, InternalContext context );

    NodeVersionMetadata getVersion( NodeId nodeId, NodeVersionId nodeVersionId, InternalContext context );

    NodeCommitEntry getCommit( NodeCommitId nodeCommitId, InternalContext context );

    NodeId getIdForPath( NodePath nodePath, InternalContext context );

    void invalidate();

    void handleNodeCreated( NodeId nodeId, NodePath nodePath, InternalContext context );

    void handleNodeDeleted( NodeId nodeId, NodePath nodePath, InternalContext context );

    void handleNodeMoved( NodeMovedParams params, InternalContext context );

    void handleNodePushed( NodeId nodeId, NodePath nodePath, NodePath currentTargetPath, InternalContext nodeContext );

    Node getNode( NodeId nodeId, NodeVersionId nodeVersionId, InternalContext context );

    Node getNode( NodePath nodePath, NodeVersionId nodeVersionId, InternalContext context );

}
