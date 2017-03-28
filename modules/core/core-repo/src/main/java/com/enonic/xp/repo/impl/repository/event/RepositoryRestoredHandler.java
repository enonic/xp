package com.enonic.xp.repo.impl.repository.event;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.RepositoryService;

public class RepositoryRestoredHandler
    implements RepositoryEventHandler
{

    private final NodeStorageService nodeStorageService;

    private final RepositoryService repositoryService;

    public RepositoryRestoredHandler( final RepositoryService repositoryService, final NodeStorageService nodeStorageService )
    {
        this.repositoryService = repositoryService;
        this.nodeStorageService = nodeStorageService;
    }

    @Override
    public void handleEvent( final Event event, final InternalContext context )
    {
        nodeStorageService.invalidate();
        repositoryService.invalidateAll();
    }
}
