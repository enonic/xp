package com.enonic.xp.app.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.CleanUpAuditLogListener;
import com.enonic.xp.audit.CleanUpAuditLogParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class AuditLogCleanupTaskHandler
    implements ScriptBean
{
    private static final Logger LOG = LoggerFactory.getLogger( AuditLogCleanupTaskHandler.class );

    private AuditLogService auditLogService;

    public String ageThreshold;

    public void execute()
    {
        auditLogService.cleanUp( CleanUpAuditLogParams.create().
            listener( new Listener() ).
            ageThreshold( ageThreshold ).
            build() );
    }


    @Override
    public void initialize( final BeanContext context )
    {
        this.auditLogService = context.getService( AuditLogService.class ).get();
    }

    private static class Listener
        implements CleanUpAuditLogListener
    {
        private long count;

        private int batchSize;

        @Override
        public void start( final int batchSize )
        {
            LOG.info( "Audit log clean up started" );
            this.batchSize = batchSize;
        }

        @Override
        public void processed()
        {
            count++;

            if ( batchSize > 0 && count % batchSize == 0 )
            {
                LOG.debug( String.format( "[%s] audit log nodes has been processed", batchSize ) );
            }
        }

        @Override
        public void finished()
        {
            LOG.info( "Audit log clean up finished" );
        }
    }
}
