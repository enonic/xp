package com.enonic.xp.repo.impl.branch;

import java.util.Collection;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;

public interface BranchService
{
    void store( NodeBranchEntry nodeBranchEntry, InternalContext context );

    void delete( Collection<NodeBranchEntry> nodeBranchEntries, InternalContext context );

    void push( NodeBranchEntry nodeBranchEntry, InternalContext context );

    NodeBranchEntry get( NodeId nodeId, InternalContext context );

    NodeBranchEntries get( Iterable<NodeId> nodeIds, InternalContext context );

    NodeBranchEntry get( NodePath nodePath, InternalContext context );

    void evictPath( NodePath nodePath, InternalContext context );

    void evictAllPaths();
}
