package com.enonic.wem.repo.internal.branch;

import com.enonic.wem.repo.internal.elasticsearch.branch.NodeBranchQuery;
import com.enonic.wem.repo.internal.elasticsearch.branch.NodeBranchQueryResult;
import com.enonic.wem.repo.internal.elasticsearch.branch.NodeBranchVersion;
import com.enonic.xp.node.NodeId;

public interface BranchService
{
    public String store( final StoreBranchDocument storeBranchDocument, final BranchContext context );

    public void delete( final NodeId nodeId, final BranchContext context );

    public NodeBranchVersion get( final NodeId nodeId, final BranchContext context );

    public NodeBranchQueryResult findAll( final NodeBranchQuery nodeBranchQuery, final BranchContext branchContext );
}
