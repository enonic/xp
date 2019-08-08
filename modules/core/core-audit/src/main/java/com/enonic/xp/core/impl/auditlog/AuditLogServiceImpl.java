package com.enonic.xp.core.impl.auditlog;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogId;
import com.enonic.xp.auditlog.AuditLogParams;
import com.enonic.xp.auditlog.AuditLogService;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;

@Component(immediate = true)
public class AuditLogServiceImpl
    implements AuditLogService
{
    private IndexService indexService;

    private RepositoryService repositoryService;

    private NodeService nodeService;

    @Activate
    public void initialize( final BundleContext context )
    {
        AuditLogRepoInitializer.create().
            setIndexService( indexService ).
            setRepositoryService( repositoryService ).
            build().
            initialize();
    }

    @Override
    public AuditLog log( final AuditLogParams params )
    {
        return CreateAuditLogCommand.create().
            nodeService( nodeService ).
            params( params ).
            build().
            execute();
    }

    @Override
    public AuditLog get( final AuditLogId id )
    {
        return GetAuditLogCommand.create().
            nodeService( nodeService ).
            auditLogId( id ).
            build().
            execute();
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
