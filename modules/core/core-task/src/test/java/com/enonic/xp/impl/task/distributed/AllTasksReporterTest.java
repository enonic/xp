package com.enonic.xp.impl.task.distributed;

import org.junit.jupiter.api.Test;

import com.enonic.xp.impl.task.TaskManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AllTasksReporterTest
{
    @Test
    void apply()
    {
        final TaskManager taskManager = mock( TaskManager.class );
        new AllTasksReporter().apply( taskManager );
        verify( taskManager ).getAllTasks();
    }
}