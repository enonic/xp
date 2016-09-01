package com.enonic.xp.lib.task;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskManager;
import com.enonic.xp.testing.script.ScriptTestSupport;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

public class SubmitTaskHandlerTest
    extends ScriptTestSupport
{
    private TaskManager taskManager;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        taskManager = Mockito.mock( TaskManager.class );
        addService( TaskManager.class, taskManager );
    }

    @Test
    public void testExample()
    {
        final TaskId taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        Mockito.when( this.taskManager.submitTask( any( RunnableTask.class ), anyString() ) ).thenReturn( taskId );

        runScript( "/site/lib/xp/examples/task/submit.js" );
    }

    @Test
    public void testSubmitTask()
        throws Exception
    {
        Mockito.when( this.taskManager.submitTask( any( RunnableTask.class ), anyString() ) ).thenReturn( TaskId.from( "123" ) );

        runFunction( "/site/test/submit-test.js", "submitTask" );
    }

    @Test(expected = ResourceProblemException.class)
    public void testSubmitTaskThrowingError()
        throws Exception
    {
        final MockTaskManager mockTaskMan = new MockTaskManager();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        this.taskManager = mockTaskMan;
        addService( TaskManager.class, taskManager );

        runFunction( "/site/test/submit-test.js", "submitTaskThrowingError" );
    }
}