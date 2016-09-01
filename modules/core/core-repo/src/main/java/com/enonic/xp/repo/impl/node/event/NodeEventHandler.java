package com.enonic.xp.repo.impl.node.event;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

interface NodeEventHandler
{
    void handleEvent( NodeStorageService nodeStorageService, final Event event, final InternalContext context );
}
