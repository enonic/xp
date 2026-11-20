package com.enonic.xp.core.impl.app;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.internal.InternalRepositoryService;

@Component(immediate = true)
public class ApplicationRepoServiceActivator
{
    private final NodeService nodeService;

    private final IndexService indexService;

    private final InternalRepositoryService repositoryService;

    private final EventPublisher eventPublisher;

    private ServiceRegistration<ApplicationRepoService> service;

    @Activate
    public ApplicationRepoServiceActivator( @Reference final NodeService nodeService, @Reference final IndexService indexService,
                                            @Reference final InternalRepositoryService repositoryService,
                                            @Reference final EventPublisher eventPublisher )
    {
        this.nodeService = nodeService;
        this.indexService = indexService;
        this.repositoryService = repositoryService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * The application repository is created, the applications persisted in system-repo by an earlier version are moved into it,
     * and only then is the repository service published: the application service, which installs the stored applications, finds
     * them all in the repository. The migration runs on the master every time, so that it picks up what was loaded from an older dump
     * (the framework restarts after a load) and retries what failed before.
     */
    @Activate
    public void activate( final BundleContext context )
    {
        final ApplicationRepoServiceImpl applicationRepoService = new ApplicationRepoServiceImpl( nodeService );
        ApplicationRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();
        if ( indexService.isMaster() )
        {
            new ApplicationRepoMigrator( nodeService, applicationRepoService, eventPublisher ).migrate();
        }
        service = context.registerService( ApplicationRepoService.class, applicationRepoService, null );
    }

    @Deactivate
    public void deactivate()
    {
        service.unregister();
    }
}
