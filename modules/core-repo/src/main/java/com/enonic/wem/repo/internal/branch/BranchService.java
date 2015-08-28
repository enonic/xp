package com.enonic.wem.repo.internal.branch;

import com.enonic.wem.repo.internal.elasticsearch.branch.NodeBranchQuery;
import com.enonic.wem.repo.internal.elasticsearch.branch.NodeBranchQueryResult;
import com.enonic.wem.repo.internal.elasticsearch.branch.NodeBranchVersion;
import com.enonic.xp.node.NodeId;

public interface BranchService
{
    String store( final StoreBranchDocument storeBranchDocument, final BranchContext context );

    void delete( final NodeId nodeId, final BranchContext context );

    NodeBranchVersion get( final NodeId nodeId, final BranchContext context );

    NodeBranchQueryResult findAll( final NodeBranchQuery nodeBranchQuery, final BranchContext branchContext );
}
