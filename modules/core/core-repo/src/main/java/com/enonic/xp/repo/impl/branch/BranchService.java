package com.enonic.xp.repo.impl.branch;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.elasticsearch.branch.NodeBranchQuery;
import com.enonic.xp.repo.impl.elasticsearch.branch.NodeBranchQueryResult;
import com.enonic.xp.repo.impl.elasticsearch.branch.NodeBranchVersion;

public interface BranchService
{
    public void store( final StoreBranchDocument storeBranchDocument, final BranchContext context );

    public void delete( final NodeId nodeId, final BranchContext context );

    public NodeBranchVersion get( final NodeId nodeId, final BranchContext context );

    public NodeBranchQueryResult findAll( final NodeBranchQuery nodeBranchQuery, final BranchContext branchContext );
}
