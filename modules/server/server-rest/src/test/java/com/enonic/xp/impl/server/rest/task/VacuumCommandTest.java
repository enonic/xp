package com.enonic.xp.impl.server.rest.task;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.impl.server.rest.model.VacuumRequestJson;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class VacuumCommandTest
{
    @Mock
    private TaskService taskService;

    @Captor
    private ArgumentCaptor<SubmitTaskParams> taskParamsArgumentCaptor;

    private VacuumCommand createCommand( final VacuumRequestJson vacuumRequestJson )
    {
        return VacuumCommand.create().
            taskService( taskService ).
            ageThreshold( vacuumRequestJson.getAgeThreshold() ).
            tasks( vacuumRequestJson.getTasks() ).
            build();
    }

    @Test
    void vacuum()
    {
        final List<String> tasks = List.of( "BinaryBlobVacuumTask", "NodeBlobVacuumTask" );
        VacuumRequestJson params = new VacuumRequestJson( "PT1s", tasks );

        final VacuumCommand command = createCommand( params );

        command.execute();

        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( taskParamsArgumentCaptor.capture() );

        assertEquals( "com.enonic.xp.app.system:vacuum", taskParamsArgumentCaptor.getValue().getDescriptorKey().toString() );
        assertEquals( 3, taskParamsArgumentCaptor.getValue().getData().getTotalSize() );
        assertEquals( "PT1s", taskParamsArgumentCaptor.getValue().getData().getString( "ageThreshold" ) );
        assertEquals( tasks, taskParamsArgumentCaptor.getValue().getData().getStrings( "tasks" ) );
    }

}
