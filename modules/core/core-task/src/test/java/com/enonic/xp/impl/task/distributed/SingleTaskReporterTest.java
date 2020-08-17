package com.enonic.xp.impl.task.distributed;

import org.junit.jupiter.api.Test;

import com.enonic.xp.impl.task.TaskManager;
import com.enonic.xp.task.TaskId;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SingleTaskReporterTest
{
    @Test
    void apply()
    {
        final TaskId someTaskId = TaskId.from( "someTask" );
        final TaskManager taskManager = mock( TaskManager.class );
        new SingleTaskReporter( someTaskId ).apply( taskManager );
        verify( taskManager ).getTaskInfo( eq( someTaskId ) );
    }
}