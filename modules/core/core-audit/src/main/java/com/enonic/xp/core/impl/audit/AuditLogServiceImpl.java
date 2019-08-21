package com.enonic.xp.core.impl.audit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogId;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.FindAuditLogParams;
import com.enonic.xp.audit.FindAuditLogResult;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;

@Component(immediate = true, configurationPid = "com.enonic.xp.audit")
public class AuditLogServiceImpl
    implements AuditLogService
{
    private final static Logger LOG = LoggerFactory.getLogger( AuditLogService.class );

    private final Marker AUDIT = MarkerFactory.getMarker( "AUDIT" );

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
            // Audit logging is disabled, just return
            // log object without actually saving it
            // TODO: Is it ok to do this?
            return AuditLog.create().
                id( AuditLogId.from( "00000000-0000-0000-0000-000000000000" ) ).
                type( params.getType() ).
                time( params.getTime() ).
                source( params.getSource() ).
                user( params.getUser() ).
                message( params.getMessage() ).
                objectUris( params.getObjectUris() ).
                data( params.getData() ).
                build();
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
        LOG.info( AUDIT, // TODO: What do we want to log here
                  String.format( "%s %s %s", auditLog.getId(), auditLog.getSource(), auditLog.getMessage() ) );
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
