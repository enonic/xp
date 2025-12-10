package com.enonic.xp.repo.impl.storage;


import java.util.Collection;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.security.acl.AccessControlList;

public interface NodeStorageService
{
    NodeVersionData store( StoreNodeParams params, InternalContext context );

    void storeVersion( StoreNodeVersionParams params, InternalContext context );

    void storeCommit( StoreNodeCommitParams params, InternalContext context );

    void delete( Collection<NodeBranchEntry> nodeBranchEntries, InternalContext context );

    void deleteFromIndex( NodeId nodeId, InternalContext internalContext );

    void push( NodeBranchEntry entry, Branch origin, InternalContext context );

    NodeCommitEntry commit( NodeCommitEntry entry, NodeVersionIds versionIds, InternalContext context );

    void addAttributes( NodeVersionId versionId, Attributes attributes, InternalContext context );

    Node get( NodeId nodeId, InternalContext context );

    Node get( NodePath nodePath, InternalContext context );

    Nodes get( NodeIds nodeIds, InternalContext context );

    Nodes get( NodePaths nodePaths, InternalContext context );

    Node get( NodeVersionId nodeVersionId, InternalContext context );

    NodeVersion getNodeVersion( NodeVersionKey nodeVersionKey, InternalContext context );

    AccessControlList getNodePermissions( NodeVersionKey nodeVersionKey, InternalContext context );

    NodeBranchEntry getBranchNodeVersion( NodeId nodeId, InternalContext context );

    NodeBranchEntries getBranchNodeVersions( NodeIds nodeIds, InternalContext context );

    NodeVersionMetadata getVersion( NodeVersionId nodeVersionId, InternalContext context );

    NodeCommitEntry getCommit( NodeCommitId nodeCommitId, InternalContext context );

    NodeBranchEntry getBranchNodeVersion( NodePath nodePath, InternalContext context );

    void invalidate();

    void invalidatePath( NodePath nodePath, InternalContext context );
}
