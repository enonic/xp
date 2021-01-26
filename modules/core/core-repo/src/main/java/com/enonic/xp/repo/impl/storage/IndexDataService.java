package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;

public interface IndexDataService
{
    ReturnValues get( NodeId nodeId, ReturnFields returnFields, InternalContext context );

    ReturnValues get( NodeIds nodeIds, ReturnFields returnFields, InternalContext context );

    void delete( NodeId nodeId, InternalContext context );

    void delete( NodeIds nodeIds, InternalContext context );

    void store( Node node, InternalContext context );

    void push( IndexPushNodeParams pushNodeParams, InternalContext context );
}
