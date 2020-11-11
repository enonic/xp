package com.enonic.xp.impl.task.distributed;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RunningTasksReporterTest
{
    @Test
    void apply()
    {
        final TaskManager taskManager = mock( TaskManager.class );
        new RunningTasksReporter().apply( taskManager );
        verify( taskManager ).getRunningTasks();
    }
}
