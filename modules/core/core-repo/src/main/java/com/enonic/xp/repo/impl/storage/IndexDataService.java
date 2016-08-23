package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repository.RepositoryId;

public interface IndexDataService
{
    ReturnValues get( final NodeId nodeId, final ReturnFields returnFields, final InternalContext context );

    ReturnValues get( final NodeIds nodeIds, final ReturnFields returnFields, final InternalContext context );

    void delete( final NodeId nodeId, final InternalContext context );

    void delete( final NodeIds nodeIds, final InternalContext context );

    void store( final Node node, final InternalContext context );

    void push( final NodeIds nodeIds, final BranchId targetBranchId, final RepositoryId targetRepo, final InternalContext context );
}
