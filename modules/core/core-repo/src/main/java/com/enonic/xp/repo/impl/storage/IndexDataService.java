package com.enonic.xp.repo.impl.storage;

import java.util.Collection;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;

public interface IndexDataService
{
    ReturnValues get( NodeId nodeId, ReturnFields returnFields, InternalContext context );

    void delete( Collection<NodeId> nodeIds, InternalContext context );

    void store( Node node, InternalContext context );

    void push( NodeId nodeId, Branch origin, InternalContext context );
}
