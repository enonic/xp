package com.enonic.xp.repo.impl.repository.event;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.RepositoryService;

public class RepositoryRestoredHandler
    implements RepositoryEventHandler
{
    private final NodeStorageService nodeStorageService;

    private final RepositoryService repositoryService;

    private final ApplicationService applicationService;

    private RepositoryRestoredHandler( final Builder builder )
    {
        nodeStorageService = builder.nodeStorageService;
        repositoryService = builder.repositoryService;
        applicationService = builder.applicationService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void handleEvent( final Event event, final InternalContext context )
    {
        nodeStorageService.invalidate();
        repositoryService.invalidateAll();
        NodeHelper.runAsAdmin( applicationService::installAllStoredApplications );
    }

    public static final class Builder
    {
        private NodeStorageService nodeStorageService;

        private RepositoryService repositoryService;

        private ApplicationService applicationService;

        private Builder()
        {
        }

        public Builder nodeStorageService( final NodeStorageService val )
        {
            nodeStorageService = val;
            return this;
        }

        public Builder repositoryService( final RepositoryService val )
        {
            repositoryService = val;
            return this;
        }

        public Builder applicationService( final ApplicationService val )
        {
            applicationService = val;
            return this;
        }

        public RepositoryRestoredHandler build()
        {
            return new RepositoryRestoredHandler( this );
        }
    }
}
