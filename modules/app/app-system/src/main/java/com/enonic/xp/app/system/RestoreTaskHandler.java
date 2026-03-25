package com.enonic.xp.app.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;

public class RestoreTaskHandler
    implements ScriptBean
{
    private static final Logger LOG = LoggerFactory.getLogger( RestoreTaskHandler.class );

    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private SnapshotService snapshotService;

    private TaskService taskService;

    private String snapshotName;

    private String repositoryId;

    private boolean latest;

    private boolean force;

    private TaskId taskId;

    public void setSnapshotName( final String snapshotName )
    {
        this.snapshotName = snapshotName;
    }

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId;
    }

    public void setLatest( final boolean latest )
    {
        this.latest = latest;
    }

    public void setForce( final boolean force )
    {
        this.force = force;
    }

    public void setTaskId( final String taskId )
    {
        this.taskId = TaskId.from( taskId );
    }

    public void execute()
    {
        TaskUtils.checkAlreadySubmitted( taskService.getTaskInfo( taskId ), taskService.getAllTasks() );

        final ProgressReporter progressReporter = TaskProgressReporterContext.current();

        final RestoreParams restoreParams = RestoreParams.create()
            .snapshotName( snapshotName )
            .repositoryId( repositoryId != null ? RepositoryId.from( repositoryId ) : null )
            .latest( latest )
            .force( force )
            .build();

        final String displayName = latest ? "latest snapshot" : snapshotName;
        LOG.info( "Restoring from snapshot: {}", displayName );

        final RestoreResult result = snapshotService.restore( restoreParams );

        if ( result.isFailed() )
        {
            LOG.warn( "Restore failed: {}", result.getMessage() );
        }
        else
        {
            LOG.info( "Restore completed successfully" );
        }

        try
        {
            progressReporter.progress( ProgressReportParams.create( MAPPER.writeValueAsString( result ) ).build() );
        }
        catch ( JsonProcessingException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.snapshotService = context.getService( SnapshotService.class ).get();
        this.taskService = context.getService( TaskService.class ).get();
    }
}
