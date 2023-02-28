package com.enonic.xp.core.impl.audit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.core.impl.audit.config.AuditLogConfig;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;

@Component(immediate = true)
public class AuditLogServiceActivator
{
    private final AuditLogConfig config;

    private final IndexService indexService;

    private final RepositoryService repositoryService;

    private final NodeService nodeService;

    private ServiceRegistration<AuditLogService> service;

    @Activate
    public AuditLogServiceActivator( @Reference final AuditLogConfig config, @Reference final IndexService indexService,
                                     @Reference final RepositoryService repositoryService, @Reference final NodeService nodeService )
    {
        this.config = config;
        this.indexService = indexService;
        this.repositoryService = repositoryService;
        this.nodeService = nodeService;
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final AuditLogServiceImpl auditLogService = new AuditLogServiceImpl( config, nodeService );
        AuditLogRepoInitializer.create().
            setIndexService( indexService ).
            setRepositoryService( repositoryService ).
            build().
            initialize();
        service = context.registerService( AuditLogService.class, auditLogService, null );
    }

    @Deactivate
    public void deactivate()
    {
        service.unregister();
    }
}
