package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;

public interface IndexedDataService
{
    ReturnValues get( final NodeId nodeId, final ReturnFields returnFields, final InternalContext context );

    ReturnValues get( final NodeIds nodeIds, final ReturnFields returnFields, final InternalContext context );

}
