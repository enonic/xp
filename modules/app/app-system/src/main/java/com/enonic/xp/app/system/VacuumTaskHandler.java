package com.enonic.xp.app.system;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.app.system.listener.VacuumListenerImpl;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumService;

public class VacuumTaskHandler
    implements ScriptBean
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final List<String> DEFAULT_VACUUM_TASKS = List.of( "SegmentVacuumTask", "VersionTableVacuumTask" );

    private VacuumService vacuumService;

    public String ageThreshold;

    public List<String> tasks;

    public void execute()
    {
        final ProgressReporter progressReporter = TaskProgressReporterContext.current();

        final VacuumResult result = vacuumService.vacuum( VacuumParameters.create().
            ageThreshold( ageThreshold != null ? Duration.parse( ageThreshold ) : null ).
            taskNames( tasks == null || tasks.isEmpty() ? DEFAULT_VACUUM_TASKS : tasks ).
            vacuumListener( new VacuumListenerImpl( progressReporter ) ).
            build() );

        try
        {
            progressReporter.info( MAPPER.writeValueAsString( MAPPER.createObjectNode().putPOJO( "taskResults", result.getResults() ) ) );
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
    }
}
