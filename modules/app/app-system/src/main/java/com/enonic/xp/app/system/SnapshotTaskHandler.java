package com.enonic.xp.app.system;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;

public class SnapshotTaskHandler
    implements ScriptBean
{
    private static final Logger LOG = LoggerFactory.getLogger( SnapshotTaskHandler.class );

    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH-mm-ss.SSS'z'" ).withZone( ZoneOffset.UTC );

    private SnapshotService snapshotService;

    private TaskService taskService;

    private String snapshotName;

    private String repositoryId;

    private TaskId taskId;

    public void setSnapshotName( final String snapshotName )
    {
        this.snapshotName = snapshotName;
    }

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId;
    }

    public void setTaskId( final String taskId )
    {
        this.taskId = TaskId.from( taskId );
    }

    public void execute()
    {
        TaskUtils.checkAlreadySubmitted( taskService.getTaskInfo( taskId ), taskService.getAllTasks() );

        final ProgressReporter progressReporter = TaskProgressReporterContext.current();

        final RepositoryId repoId = repositoryId != null ? RepositoryId.from( repositoryId ) : null;
        final String resolvedName = snapshotName != null ? snapshotName : createSnapshotName( repoId );

        final SnapshotParams snapshotParams = SnapshotParams.create().snapshotName( resolvedName ).repositoryId( repoId ).build();

        LOG.info( "Creating snapshot: {}", resolvedName );

        final SnapshotResult result = snapshotService.snapshot( snapshotParams );

        LOG.info( "Snapshot completed with state: {}", result.getState() );

        try
        {
            progressReporter.progress( ProgressReportParams.create( MAPPER.writeValueAsString( result ) ).build() );
        }
        catch ( JsonProcessingException e )
        {
            throw new RuntimeException( e );
        }
    }

    private static String createSnapshotName( final RepositoryId repositoryId )
    {
        return ( ( repositoryId == null ? "" : repositoryId ) + DATE_TIME_FORMATTER.format( Instant.now() ) ).toLowerCase();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.snapshotService = context.getService( SnapshotService.class ).get();
        this.taskService = context.getService( TaskService.class ).get();
    }
}
