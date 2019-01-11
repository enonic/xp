package com.enonic.xp.impl.server.rest.task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.task.AbstractRunnableTaskTest;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumService;
import com.enonic.xp.vacuum.VacuumTaskResult;

public class VacuumRunnableTaskTest
    extends AbstractRunnableTaskTest
{
    private VacuumService vacuumService;

    @Before
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.vacuumService = Mockito.mock( VacuumService.class );
    }

    protected VacuumRunnableTask createAndRunTask()
    {
        final VacuumRunnableTask task = VacuumRunnableTask.create().
            description( "upgrade" ).
            taskService( taskService ).
            vacuumService( vacuumService ).
            build();

        task.run( TaskId.from( "taskId" ), progressReporter );

        return task;
    }

    @Test
    public void vacuum()
    {
        VacuumResult vacuumResult = VacuumResult.create().add( VacuumTaskResult.create().
            deleted().deleted().failed().inUse().processed().
            taskName( "vacuum-task-name" ).build() ).build();

        Mockito.when( this.vacuumService.vacuum( Mockito.isA( VacuumParameters.class ) ) ).thenReturn( vacuumResult );

        final VacuumRunnableTask task = createAndRunTask();

        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "upgrade" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "vacuum_result.json" ), jsonTestHelper.stringToJson( result ) );
    }

}
