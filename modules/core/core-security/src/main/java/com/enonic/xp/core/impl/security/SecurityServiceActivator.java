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

    private final SecurityAuditLogSupport securityAuditLogSupport;

    private final PasswordEncoderFactory passwordEncoderFactory;

    private ServiceRegistration<SecurityService> service;

    @Activate
    public SecurityServiceActivator( @Reference final NodeService nodeService, @Reference final IndexService indexService,
                                     @Reference final SecurityAuditLogSupport securityAuditLogSupport,
                                     @Reference final PasswordEncoderFactory passwordEncoderFactory )
    {
        this.indexService = indexService;
        this.nodeService = nodeService;
        this.securityAuditLogSupport = securityAuditLogSupport;
        this.passwordEncoderFactory = passwordEncoderFactory;
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final SecurityServiceImpl securityService = new SecurityServiceImpl( nodeService, securityAuditLogSupport, passwordEncoderFactory );
        SecurityInitializer.create()
            .setIndexService( indexService )
            .setSecurityService( securityService )
            .setNodeService( nodeService )
            .build()
            .initialize();
        service = context.registerService( SecurityService.class, securityService, null );
    }

    @Deactivate
    public void deactivate()
    {
        service.unregister();
    }
}
