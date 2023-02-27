package com.enonic.xp.core.impl.app;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;

@Component(immediate = true)
public class ApplicationRepoServiceActivator
{
    private final NodeService nodeService;

    private final IndexService indexService;

    private ServiceRegistration<ApplicationRepoService> service;

    @Activate
    public ApplicationRepoServiceActivator( @Reference final NodeService nodeService, @Reference final IndexService indexService )
    {
        this.nodeService = nodeService;
        this.indexService = indexService;
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final ApplicationRepoServiceImpl applicationRepoService = new ApplicationRepoServiceImpl( nodeService );
        ApplicationRepoInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            build().
            initialize();
        service = context.registerService( ApplicationRepoService.class, applicationRepoService, null );
    }

    @Deactivate
    public void deactivate()
    {
        service.unregister();
    }
}
