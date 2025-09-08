package com.enonic.xp.repo.impl.branch;

import java.util.Collection;

import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;

public interface BranchService
{
    void store( NodeBranchEntry nodeBranchEntry, NodePath previousPath, InternalContext context );

    void delete( Collection<NodeBranchEntry> nodeBranchEntries, InternalContext context );

    NodeBranchEntry get( NodeId nodeId, InternalContext context );

    NodeBranchEntries get( Iterable<NodeId> nodeIds, InternalContext context );

    NodeBranchEntry get( NodePath nodePath, InternalContext context );

    void evictPath( NodePath nodePath, InternalContext context );

    void evictAllPaths();
}
