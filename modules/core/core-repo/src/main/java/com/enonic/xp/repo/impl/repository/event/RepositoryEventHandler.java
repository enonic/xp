package com.enonic.xp.repo.impl.repository.event;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

interface RepositoryEventHandler
{
    void handleEvent( NodeStorageService storageService, final Event event, final InternalContext context );
}
