package com.enonic.xp.repo.impl.branch;

import com.enonic.xp.context.InternalContext;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;

public interface BranchService
{
    String store( final NodeBranchEntry nodeBranchEntry, final InternalContext context );

    String store( final NodeBranchEntry nodeBranchEntry, final NodePath previousPath, final InternalContext context );

    void delete( final NodeId nodeId, final InternalContext context );

    void delete( final NodeIds nodeIds, final InternalContext context );

    NodeBranchEntry get( final NodeId nodeId, final InternalContext context );

    NodeBranchEntries get( final NodeIds nodeIds, final boolean keepOrder, final InternalContext context );

    NodeBranchEntry get( final NodePath nodePath, final InternalContext context );

    NodeBranchEntries get( final NodePaths nodePath, final InternalContext context );

    void cachePath( final NodeId nodeId, final NodePath nodePath, final InternalContext context );

    void evictPath( final NodePath nodePath, final InternalContext context );

    void evictAllPaths();
}
