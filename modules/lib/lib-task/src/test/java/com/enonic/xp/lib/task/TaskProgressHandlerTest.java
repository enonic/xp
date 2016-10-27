package com.enonic.xp.lib.task;

import java.util.List;

import org.junit.Test;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.script.ScriptTestSupport;

import static org.junit.Assert.*;

public class TaskProgressHandlerTest
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
    public void testExample()
    {
        taskService.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );

        runScript( "/site/lib/xp/examples/task/progress.js" );

        final List<TaskProgress> progress = taskService.progressHistory;
        assertEquals( 22, progress.size() );
        assertEquals( "Initializing task", progress.get( 0 ).getInfo() );
        assertEquals( "Processing item 1", progress.get( 1 ).getInfo() );
        assertEquals( 1, progress.get( 4 ).getCurrent() );
        assertEquals( 10, progress.get( 4 ).getTotal() );
        assertEquals( "Task completed", progress.get( progress.size() - 1 ).getInfo() );
    }

    @Test
    public void testReportProgress()
        throws Exception
    {
        taskService.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        runFunction( "/site/test/progress-test.js", "reportProgress" );

        final List<TaskProgress> progress = taskService.progressHistory;
        assertEquals( 22, progress.size() );
        assertEquals( "Starting task", progress.get( 0 ).getInfo() );
        assertEquals( "Step 0", progress.get( 1 ).getInfo() );
        assertEquals( 1, progress.get( 4 ).getCurrent() );
        assertEquals( 10, progress.get( 4 ).getTotal() );
        assertEquals( "Work completed", progress.get( progress.size() - 1 ).getInfo() );
    }

    @Test
    public void testReportProgressOutsideTask()
        throws Exception
    {
        try
        {
            runFunction( "/site/test/progress-test.js", "reportProgressOutsideTask" );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertEquals( "The reportProgress function must be called from within a task.", e.getMessage() );
        }
    }

}