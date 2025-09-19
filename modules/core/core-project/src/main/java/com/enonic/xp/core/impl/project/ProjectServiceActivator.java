package com.enonic.xp.core.impl.project;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SecurityService;

@Component(immediate = true, configurationPid = "com.enonic.xp.project")
public class ProjectServiceActivator
{
    private final RepositoryService repositoryService;

    private final IndexService indexService;

    private final NodeService nodeService;

    private final SecurityService securityService;

    private final EventPublisher eventPublisher;

    private ServiceRegistration<ProjectService> service;

    private final ProjectConfig config;

    @Activate
    public ProjectServiceActivator( @Reference final RepositoryService repositoryService, @Reference final IndexService indexService,
                                    @Reference final NodeService nodeService, @Reference final SecurityService securityService,
                                    @Reference final ProjectPermissionsContextManager projectPermissionsContextManager,
                                    @Reference final EventPublisher eventPublisher, final ProjectConfig config )
    {
        this.repositoryService = repositoryService;
        this.indexService = indexService;
        this.nodeService = nodeService;
        this.securityService = securityService;
        this.eventPublisher = eventPublisher;
        this.config = config;
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final ProjectServiceImpl projectService =
            new ProjectServiceImpl( repositoryService, indexService, nodeService, securityService,
                                    eventPublisher, config );
        projectService.initialize();
        service = context.registerService( ProjectService.class, projectService, null );
    }

    @Deactivate
    public void deactivate()
    {
        service.unregister();
    }
}
