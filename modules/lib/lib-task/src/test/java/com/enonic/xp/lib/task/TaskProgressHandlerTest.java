package com.enonic.xp.lib.task;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.impl.task.MockTaskService;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class TaskProgressHandlerTest
    extends ScriptTestSupport
{
    private MockTaskService taskService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        taskService = new MockTaskService();
        addService( TaskService.class, taskService );
    }

    @Test
    void testExample()
    {
        taskService.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );

        runScript( "/lib/xp/examples/task/progress.js" );

        final List<TaskProgress> progress = taskService.progressHistory;
        assertEquals( 12, progress.size() );
        assertEquals( "Initializing task", progress.get( 0 ).getInfo() );
        assertEquals( "Processing item 1", progress.get( 1 ).getInfo() );
        assertEquals( 0, progress.get( 1 ).getCurrent() );
        assertEquals( 10, progress.get( 1 ).getTotal() );
        assertEquals( "Task completed", progress.get( progress.size() - 1 ).getInfo() );
    }

    @Test
    void testReportProgress()
    {
        taskService.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        runFunction( "/test/progress-test.js", "reportProgress" );

        final List<TaskProgress> progress = taskService.progressHistory;
        assertEquals( 12, progress.size() );
        assertEquals( "Starting task", progress.get( 0 ).getInfo() );
        assertEquals( "Step 0", progress.get( 1 ).getInfo() );
        assertEquals( 0, progress.get( 1 ).getCurrent() );
        assertEquals( 10, progress.get( 1 ).getTotal() );
        assertEquals( "Work completed", progress.get( progress.size() - 1 ).getInfo() );
    }

    @Test
    void testReportProgressOutsideTask()
    {
        try
        {
            runFunction( "/test/progress-test.js", "reportProgressOutsideTask" );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertEquals( "The reportProgress function must be called from within a task.", e.getMessage() );
        }
    }

    @Test
    void testReportProgressWithoutInfo()
    {
        taskService.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        runFunction( "/test/progress-test.js", "reportProgressWithoutInfo" );

        final List<TaskProgress> progress = taskService.progressHistory;
        assertEquals( 10, progress.size() );
        assertEquals( 0, progress.get( 0 ).getCurrent() );
        assertEquals( 10, progress.get( 0 ).getTotal() );
        assertEquals( "", progress.get( 0 ).getInfo() );
        assertEquals( 9, progress.get( 9 ).getCurrent() );
        assertEquals( 10, progress.get( 9 ).getTotal() );
    }

    @Test
    void testReportProgressInfoOnly()
    {
        taskService.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        runFunction( "/test/progress-test.js", "reportProgressInfoOnly" );

        final List<TaskProgress> progress = taskService.progressHistory;
        assertEquals( 3, progress.size() );
        assertEquals( "Step 1", progress.get( 0 ).getInfo() );
        assertEquals( "Step 2", progress.get( 1 ).getInfo() );
        assertEquals( "Step 3", progress.get( 2 ).getInfo() );
    }

}
