package com.enonic.xp.core.impl.audit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogId;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.FindAuditLogParams;
import com.enonic.xp.audit.FindAuditLogResult;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;

@Component(immediate = true, configurationPid = "com.enonic.xp.audit")
public class AuditLogServiceImpl
    implements AuditLogService
{
    private final static Logger LOG = LoggerFactory.getLogger( AuditLogService.class );

    private AuditLogConfig config;

    private IndexService indexService;

    private RepositoryService repositoryService;

    private NodeService nodeService;

    @Activate
    public void initialize( final AuditLogConfig config )
    {
        this.config = config;

        AuditLogRepoInitializer.create().
            setIndexService( indexService ).
            setRepositoryService( repositoryService ).
            build().
            initialize();
    }

    public AuditLogConfig getConfig()
    {
        return config;
    }

    @Override
    public AuditLog log( final LogAuditLogParams params )
    {
        if ( !config.enabled() )
        {
            return null;
        }

        AuditLog result = CreateAuditLogCommand.create().
            nodeService( nodeService ).
            params( params ).
            build().
            execute();

        if ( this.config.outputLogs() )
        {
            logAuditLog( result );
        }

        return result;
    }

    private void logAuditLog( final AuditLog auditLog )
    {
        final String message = String.format( "%s %s %s", auditLog.getType(), auditLog.getSource(), auditLog.getMessage() );
        LOG.info( message );
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

    @Override
    public FindAuditLogResult find( final FindAuditLogParams params )
    {
        return FindAuditLogCommand.create().
            nodeService( nodeService ).
            params( params ).
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
