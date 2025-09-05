package com.enonic.xp.lib.task;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.impl.task.MockTaskService;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class ExecuteFunctionHandlerTest
    extends ScriptTestSupport
{
    private TaskService taskService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        taskService = Mockito.mock( TaskService.class );
        addService( TaskService.class, taskService );
    }

    @Test
    public void testExample()
    {
        final TaskId taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        Mockito.when( this.taskService.submitLocalTask( any() ) ).thenReturn( taskId );

        runScript( "/lib/xp/examples/task/executeFunction.js" );
    }

    @Test
    public void testExecuteFunction()
        throws Exception
    {
        Mockito.when( this.taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "123" ) );

        runFunction( "/test/executeFunction-test.js", "executeFunction" );
    }

    @Test
    public void testExecuteFunctionThrowingError()
        throws Exception
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        this.taskService = mockTaskMan;
        addService( TaskService.class, taskService );

        assertThrows( ResourceProblemException.class,
                      () -> runFunction( "/test/executeFunction-test.js", "executeFunctionThrowingError" ) );
    }
}
