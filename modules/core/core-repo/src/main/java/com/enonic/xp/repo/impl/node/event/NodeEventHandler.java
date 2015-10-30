package com.enonic.xp.repo.impl.node.event;

import com.enonic.xp.event.Event2;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StorageService;

interface NodeEventHandler
{
    void handleEvent( StorageService storageService, final Event2 event, final InternalContext context );
}
