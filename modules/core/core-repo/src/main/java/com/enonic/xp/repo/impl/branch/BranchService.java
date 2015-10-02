package com.enonic.xp.repo.impl.branch;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.storage.BranchNodeVersion;
import com.enonic.xp.repo.impl.branch.storage.BranchNodeVersions;

public interface BranchService
{
    String store( final StoreBranchDocument storeBranchDocument, final InternalContext context );

    String move( final MoveBranchDocument moveBranchDocument, final InternalContext context );

    void delete( final NodeId nodeId, final InternalContext context );

    BranchNodeVersion get( final NodeId nodeId, final InternalContext context );

    BranchNodeVersions get( final NodeIds nodeIds, final InternalContext context );

    BranchNodeVersion get( final NodePath nodePath, final InternalContext context );

    BranchNodeVersions get( final NodePaths nodePath, final InternalContext context );
}
