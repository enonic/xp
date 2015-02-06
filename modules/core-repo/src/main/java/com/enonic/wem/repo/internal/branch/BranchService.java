package com.enonic.wem.repo.internal.branch;

import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.repo.internal.index.query.NodeBranchVersion;

public interface BranchService
{
    public void store( final StoreBranchDocument storeBranchDocument, final BranchContext context );

    public void delete( final NodeId nodeId, final BranchContext context );

    public NodeBranchVersion get( final NodeId nodeId, final BranchContext context );

    public NodeBranchVersion get( final NodePath nodePath, final BranchContext context );
}
