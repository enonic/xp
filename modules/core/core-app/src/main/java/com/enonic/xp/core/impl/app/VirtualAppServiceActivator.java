package com.enonic.xp.core.impl.app;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.internal.InternalRepositoryService;

@Component(immediate = true)
public class VirtualAppServiceActivator
{
    private final IndexService indexService;

    private final InternalRepositoryService repositoryService;

    private final NodeService nodeService;

    private ServiceRegistration<VirtualAppService> service;

    @Activate
    public VirtualAppServiceActivator( @Reference final IndexService indexService,
                                       @Reference final InternalRepositoryService repositoryService,
                                       @Reference final NodeService nodeService )
    {
        this.indexService = indexService;
        this.repositoryService = repositoryService;
        this.nodeService = nodeService;
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final VirtualAppService virtualAppService = new VirtualAppService( nodeService );
        VirtualAppInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();
        service = context.registerService( VirtualAppService.class, virtualAppService, null );
    }

    @Deactivate
    public void deactivate()
    {
        service.unregister();
    }
}

