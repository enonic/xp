package com.enonic.xp.repo.impl.repository.event;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.internal.InternalRepositoryService;

public class RepositoryRestoreInitializedHandler
    implements RepositoryEventHandler
{

    private final NodeStorageService nodeStorageService;

    private final InternalRepositoryService repositoryService;

    public RepositoryRestoreInitializedHandler( final InternalRepositoryService repositoryService, final NodeStorageService nodeStorageService )
    {
        this.repositoryService = repositoryService;
        this.nodeStorageService = nodeStorageService;
    }

    @Override
    public void handleEvent( final Event event )
    {
        nodeStorageService.invalidate();
        repositoryService.invalidateAll();
    }
}
