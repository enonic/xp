package com.enonic.xp.app.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.CleanUpAuditLogListener;
import com.enonic.xp.audit.CleanUpAuditLogParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;

public class AuditLogCleanupTaskHandler
    implements ScriptBean
{
    private static final Logger LOG = LoggerFactory.getLogger( AuditLogCleanupTaskHandler.class );

    private AuditLogService auditLogService;

    private TaskService taskService;

    private String ageThreshold;

    private TaskId taskId;

    public void setAgeThreshold( final String ageThreshold )
    {
        this.ageThreshold = ageThreshold;
    }

    public void setTaskId( final String taskId )
    {
        this.taskId = TaskId.from( taskId );
    }

    public void execute()
    {
        TaskUtils.checkAlreadySubmitted( taskService.getTaskInfo( taskId ), taskService.getAllTasks() );

        LOG.info( "Audit log clean up started" );

        final Listener listener = new Listener( TaskProgressReporterContext.current() );

        auditLogService.cleanUp( CleanUpAuditLogParams.create().listener( listener ).ageThreshold( ageThreshold ).build() );

        listener.reportProgress();

        LOG.info( "Audit log clean up finished" );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.auditLogService = context.getService( AuditLogService.class ).get();
        this.taskService = context.getService( TaskService.class ).get();
    }

    private static class Listener
        implements CleanUpAuditLogListener
    {
        private static final int REPORT_INTERVAL = 1_000;

        private final ProgressReporter progressReporter;

        private int deleted;

        private int reportedAt;

        private int resolved;

        Listener( final ProgressReporter progressReporter )
        {
            this.progressReporter = progressReporter;
        }

        @Override
        public void resolved( final int count )
        {
            this.resolved = count;
            reportProgress();
        }

        @Override
        public void recordsDeleted( final int count )
        {
            deleted += count;

            if ( deleted - reportedAt >= REPORT_INTERVAL )
            {
                LOG.debug( "{} audit log records have been deleted", deleted );
                reportProgress();
            }
        }

        private void reportProgress()
        {
            reportedAt = deleted;

            if ( progressReporter != null )
            {
                progressReporter.progress( ProgressReportParams.create().current( deleted ).total( resolved ).build() );
            }
        }
    }
}
