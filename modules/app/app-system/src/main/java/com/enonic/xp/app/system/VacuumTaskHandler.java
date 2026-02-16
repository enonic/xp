package com.enonic.xp.app.system;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.app.system.listener.VacuumListenerImpl;
import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumService;

public class VacuumTaskHandler
    implements ScriptBean
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private VacuumService vacuumService;

    private TaskService taskService;

    private String ageThreshold;

    private List<String> tasks;

    private TaskId taskId;

    public void setAgeThreshold( final String ageThreshold )
    {
        this.ageThreshold = ageThreshold;
    }

    public void setTasks( final List<String> tasks )
    {
        this.tasks = tasks;
    }

    public void setTaskId( final String taskId )
    {
        this.taskId = TaskId.from( taskId );
    }

    public void execute()
    {
        TaskUtils.checkAlreadySubmitted( taskService.getTaskInfo( taskId ), taskService.getAllTasks() );

        final ProgressReporter progressReporter = TaskProgressReporterContext.current();

        final VacuumResult result = vacuumService.vacuum( VacuumParameters.create().
            ageThreshold( ageThreshold != null ? Duration.parse( ageThreshold ) : null ).
            taskNames( tasks ).
            vacuumListener( new VacuumListenerImpl( progressReporter ) ).
            build() );

        try
        {
            progressReporter.progress( ProgressReportParams.create(
                MAPPER.writeValueAsString( MAPPER.createObjectNode().putPOJO( "taskResults", result.getResults() ) ) ).build() );
        }
        catch ( JsonProcessingException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.vacuumService = context.getService( VacuumService.class ).get();
        this.taskService = context.getService( TaskService.class ).get();
    }
}
