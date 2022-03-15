package com.enonic.xp.core.impl.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogId;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.CleanUpAuditLogParams;
import com.enonic.xp.audit.CleanUpAuditLogResult;
import com.enonic.xp.audit.FindAuditLogParams;
import com.enonic.xp.audit.FindAuditLogResult;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.core.impl.audit.config.AuditLogConfig;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;

public class AuditLogServiceImpl
    implements AuditLogService
{
    private static final Logger LOG = LoggerFactory.getLogger( AuditLogService.class );

    private final AuditLogConfig config;

    private final IndexService indexService;

    private final RepositoryService repositoryService;

    private final NodeService nodeService;

    public AuditLogServiceImpl( final AuditLogConfig config, final IndexService indexService, final RepositoryService repositoryService,
                                final NodeService nodeService )
    {
        this.config = config;
        this.indexService = indexService;
        this.repositoryService = repositoryService;
        this.nodeService = nodeService;
    }

    public void initialize()
    {
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
        if ( !config.isEnabled() )
        {
            return null;
        }

        try
        {
            AuditLog result = CreateAuditLogCommand.create().
                nodeService( nodeService ).
                params( params ).
                build().
                execute();

            if ( this.config.isOutputLogs() )
            {
                LOG.info( "{} {}", result.getType(), result.getSource() );
            }

            return result;
        }
        catch ( Throwable th )
        {
            LOG.error( "Could not log into auditLog", th );
            return null;
        }
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

    @Override
    public CleanUpAuditLogResult cleanUp( final CleanUpAuditLogParams params )
    {
        return CleanUpAuditLogCommand.create().
            nodeService( nodeService ).
            ageThreshold( params.getAgeThreshold() != null ? params.getAgeThreshold() : config.ageThreshold() ).
            listener( params.getListener() ).
            build().
            execute();
    }
}
