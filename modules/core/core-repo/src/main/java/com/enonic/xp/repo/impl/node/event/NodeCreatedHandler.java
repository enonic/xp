package com.enonic.xp.repo.impl.node.event;

import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StorageService;

public class NodeCreatedHandler
    implements NodeEventHandler
{

    @Override
    public void handleEvent( StorageService storageService, final NodeEventData eventData, final InternalContext context )
    {
        storageService.handleNodeCreated( eventData.getNodeId(), eventData.getNodePath(), context );
    }


}
