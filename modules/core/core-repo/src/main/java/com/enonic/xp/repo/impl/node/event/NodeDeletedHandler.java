package com.enonic.xp.repo.impl.node.event;

import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StorageService;

public class NodeDeletedHandler
    implements NodeEventHandler
{

    @Override
    public void handleEvent( final StorageService storageService, final NodeEventData eventData, final InternalContext context )
    {
        storageService.handleNodeDeleted( eventData.getNodeId(), eventData.getNodePath(), context );
    }
}
