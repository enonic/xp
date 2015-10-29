package com.enonic.xp.repo.impl.branch;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.storage.MoveBranchParams;
import com.enonic.xp.repo.impl.branch.storage.NodeBranchMetadata;
import com.enonic.xp.repo.impl.branch.storage.NodesBranchMetadata;

public interface BranchService
{
    String store( final NodeBranchMetadata nodeBranchMetadata, final InternalContext context );

    String move( final MoveBranchParams moveBranchParams, final InternalContext context );

    void delete( final NodeId nodeId, final InternalContext context );

    NodeBranchMetadata get( final NodeId nodeId, final InternalContext context );

    NodesBranchMetadata get( final NodeIds nodeIds, final InternalContext context );

    NodeBranchMetadata get( final NodePath nodePath, final InternalContext context );

    NodesBranchMetadata get( final NodePaths nodePath, final InternalContext context );

    void handleNodeCreated( final NodeId nodeId, final NodePath nodePath, final InternalContext context );
}
