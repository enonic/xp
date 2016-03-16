package com.enonic.xp.repo.impl.storage;


import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repo.impl.branch.storage.NodeBranchMetadata;
import com.enonic.xp.repo.impl.branch.storage.NodesBranchMetadata;
import com.enonic.xp.repo.impl.version.NodeVersionDocumentId;

public interface StorageService
{
    Node move( final MoveNodeParams params, final InternalContext context );

    Node store( final Node node, final InternalContext context );

    void delete( final NodeId nodeId, final InternalContext context );

    Node updateMetadata( final Node node, final InternalContext context );

    void updateVersion( final Node node, final NodeVersionId nodeVersionId, final InternalContext context );

    Node get( final NodeId nodeId, final InternalContext context );

    Node get( final NodePath nodePath, final InternalContext context );

    Nodes get( final NodeIds nodeIds, final InternalContext context );

    Nodes get( final NodePaths nodePaths, final InternalContext context );

    Node get( final NodeVersionId nodeVersionId, final InternalContext context );

    NodeVersion get( final NodeVersionMetadata nodeVersionMetadata );

    NodeBranchMetadata getBranchNodeVersion( final NodeId nodeId, final InternalContext context );

    NodesBranchMetadata getBranchNodeVersions( final NodeIds nodeIds, final InternalContext context );

    NodeVersionMetadata getVersion( final NodeVersionDocumentId versionId, final InternalContext context );

    ReturnValues getIndexedData( final NodeId nodeId, final ReturnFields returnFields, final InternalContext context );

    NodeId getIdForPath( final NodePath nodePath, final InternalContext context );

    NodePath getParentPath( final NodeId nodeId, final InternalContext context );

    void handleNodeCreated( final NodeId nodeId, final NodePath nodePath, final InternalContext context );

    void handleNodeDeleted( final NodeId nodeId, final NodePath nodePath, final InternalContext context );

    void handleNodeMoved( final NodeMovedParams params, final InternalContext context );
}
