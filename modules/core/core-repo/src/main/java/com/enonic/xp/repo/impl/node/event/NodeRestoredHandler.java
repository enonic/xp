package com.enonic.xp.repo.impl.node.event;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StorageService;

public class NodeRestoredHandler
    extends AbstractNodeEventHandler
{

    @Override
    public void handleEvent( StorageService storageService, final Event event, final InternalContext context )
    {
        storageService.invalidate();
    }
}
