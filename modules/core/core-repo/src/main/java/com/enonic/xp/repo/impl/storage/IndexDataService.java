package com.enonic.xp.repo.impl.storage;

import java.util.Collection;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;

public interface IndexDataService
{
    ReturnValues get( NodeId nodeId, ReturnFields returnFields, InternalContext context );

    void delete( Collection<NodeId> nodeIds, InternalContext context );

    void store( IndexDocument indexDocument, InternalContext context );
}
