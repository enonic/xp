package com.enonic.xp.app.system;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
public class VacuumTaskHandlerTest
    extends ScriptTestSupport
{
    @Captor
    private ArgumentCaptor<VacuumParameters> paramsCaptor;

    @Mock
    private VacuumService vacuumService;

    @Mock
    private ProgressReporter progressReporter;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        addService( VacuumService.class, this.vacuumService );
    }

    @Test
    public void vacuum()
        throws Exception
    {
        final TaskId taskId = TaskId.from( "task" );

        Mockito.when( vacuumService.vacuum( isA( VacuumParameters.class ) ) ).thenReturn( VacuumResult.create().build() );

        TaskProgressReporterContext.withContext( ( id, progressReporter ) -> runFunction( "/test/VacuumTaskHandlerTest.js", "vacuum" ) ).
            run( taskId, progressReporter );

        Mockito.verify( vacuumService, Mockito.times( 1 ) ).vacuum( paramsCaptor.capture() );

        assertEquals( "PT2S", paramsCaptor.getValue().getAgeThreshold().toString() );
        assertEquals( 2, paramsCaptor.getValue().getTaskNames().size() );
        assertTrue( paramsCaptor.getValue().getTaskNames().containsAll( Set.of( "a", "b" ) ) );
        assertNotNull( paramsCaptor.getValue().getVacuumListener() );
    }

    @Test
    public void vacuumDefaultParams()
        throws Exception
    {
        final TaskId taskId = TaskId.from( "task" );

        Mockito.when( vacuumService.vacuum( isA( VacuumParameters.class ) ) ).thenReturn( VacuumResult.create().build() );

        TaskProgressReporterContext.withContext(
            ( id, progressReporter ) -> runFunction( "/test/VacuumTaskHandlerTest.js", "vacuumDefault" ) ).
            run( taskId, progressReporter );

        Mockito.verify( vacuumService, Mockito.times( 1 ) ).vacuum( paramsCaptor.capture() );

        assertNull( paramsCaptor.getValue().getAgeThreshold() );
        assertEquals( 2, paramsCaptor.getValue().getTaskNames().size() );
        assertTrue( paramsCaptor.getValue().getTaskNames().containsAll( Set.of( "SegmentVacuumTask", "VersionTableVacuumTask" ) ) );
        assertNotNull( paramsCaptor.getValue().getVacuumListener() );
    }
}
