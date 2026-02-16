package com.enonic.xp.app.system;

import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacuumTaskHandlerTest
    extends ScriptTestSupport
{
    @Captor
    private ArgumentCaptor<VacuumParameters> paramsCaptor;

    @Mock
    private VacuumService vacuumService;

    @Mock
    private TaskService taskService;

    @Mock
    private ProgressReporter progressReporter;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        addService( VacuumService.class, this.vacuumService );
        addService( TaskService.class, this.taskService );
    }

    @Test
    void vacuum()
    {
        final TaskId taskId = TaskId.from( "task" );

        when( taskService.getTaskInfo( taskId ) ).thenReturn( TaskInfo.create()
                                                                  .id( taskId )
                                                                  .name( "com.enonic.xp.app.system:vacuum" )
                                                                  .application( ApplicationKey.SYSTEM )
                                                                  .startTime( Instant.now() )
                                                                  .build() );
        when( vacuumService.vacuum( any( VacuumParameters.class ) ) ).thenReturn( VacuumResult.create().build() );

        TaskProgressReporterContext.withContext( ( id, progressReporter ) -> runFunction( "/test/VacuumTaskHandlerTest.js", "vacuum" ) )
            .run( taskId, progressReporter );

        verify( vacuumService, times( 1 ) ).vacuum( paramsCaptor.capture() );

        assertEquals( "PT2S", paramsCaptor.getValue().getAgeThreshold().toString() );
        assertEquals( 2, paramsCaptor.getValue().getTaskNames().size() );
        assertTrue( paramsCaptor.getValue().getTaskNames().containsAll( Set.of( "a", "b" ) ) );
        assertNotNull( paramsCaptor.getValue().getVacuumListener() );
    }

    @Test
    void vacuumDefaultParams()
    {
        final TaskId taskId = TaskId.from( "task" );

        when( taskService.getTaskInfo( taskId ) ).thenReturn( TaskInfo.create()
                                                                  .id( taskId )
                                                                  .name( "com.enonic.xp.app.system:vacuum" )
                                                                  .application( ApplicationKey.SYSTEM )
                                                                  .startTime( Instant.now() )
                                                                  .build() );
        when( vacuumService.vacuum( any( VacuumParameters.class ) ) ).thenReturn( VacuumResult.create().build() );

        TaskProgressReporterContext.withContext(
            ( id, progressReporter ) -> runFunction( "/test/VacuumTaskHandlerTest.js", "vacuumDefault" ) ).run( taskId, progressReporter );

        verify( vacuumService, times( 1 ) ).vacuum( paramsCaptor.capture() );

        assertNull( paramsCaptor.getValue().getAgeThreshold() );
        assertThat( paramsCaptor.getValue().getTaskNames() ).isEmpty();
        assertNotNull( paramsCaptor.getValue().getVacuumListener() );
    }
}
