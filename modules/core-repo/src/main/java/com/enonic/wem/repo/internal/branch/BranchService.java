package com.enonic.wem.repo.internal.branch;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQuery;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQueryResult;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchVersion;
import com.enonic.xp.node.NodeId;

public interface BranchService
{
    String store( final StoreBranchDocument storeBranchDocument, final InternalContext context );

    void delete( final NodeId nodeId, final InternalContext context );

    NodeBranchVersion get( final NodeId nodeId, final InternalContext context );

    NodeBranchQueryResult findAll( final NodeBranchQuery nodeBranchQuery, final InternalContext branchContext );
}
