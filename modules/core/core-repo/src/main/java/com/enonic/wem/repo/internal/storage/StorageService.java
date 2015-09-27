package com.enonic.wem.repo.internal.storage;


import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.storage.BranchNodeVersion;
import com.enonic.wem.repo.internal.version.NodeVersionDocumentId;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.Nodes;

public interface StorageService
{
    Node store( final Node node, final InternalContext context );

    void delete( final NodeId nodeId, final InternalContext context );

    Node updateMetadata( final Node node, final InternalContext context );

    void updateVersion( final Node node, final NodeVersionId nodeVersionId, final InternalContext context );

    Node get( final NodeId nodeId, final InternalContext context );

    Node get( final NodePath nodePath, final InternalContext context );

    Nodes get( final NodeIds nodeIds, final InternalContext context );

    Nodes get( final NodePaths nodePaths, final InternalContext context );

    Node get( final NodeVersionId nodeVersionId );

    BranchNodeVersion getBranchNodeVersion( final NodeId nodeId, final InternalContext context );

    NodeVersion getVersion( final NodeVersionDocumentId versionId, final InternalContext context );

    boolean exists( final NodePath nodePath, final InternalContext context );
}
