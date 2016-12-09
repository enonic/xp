package com.enonic.xp.repo.impl.repository.event;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

public class RepositoryRestoredHandler
    implements RepositoryEventHandler
{

    @Override
    public void handleEvent( NodeStorageService storageService, final Event event, final InternalContext context )
    {
        storageService.invalidate();
    }
}
