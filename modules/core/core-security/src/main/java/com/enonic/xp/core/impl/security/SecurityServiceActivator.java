package com.enonic.xp.core.impl.security;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.SecurityService;

@Component(immediate = true)
public class SecurityServiceActivator
{
    private final IndexService indexService;

    private final NodeService nodeService;

    private ServiceRegistration<SecurityService> service;

    @Activate
    public SecurityServiceActivator( @Reference final NodeService nodeService, @Reference final IndexService indexService )
    {
        this.indexService = indexService;
        this.nodeService = nodeService;
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final SecurityServiceImpl securityService = new SecurityServiceImpl( nodeService, indexService );
        securityService.initialize();
        service = context.registerService( SecurityService.class, securityService, null );
    }

    @Deactivate
    public void deactivate()
    {
        service.unregister();
    }
}
