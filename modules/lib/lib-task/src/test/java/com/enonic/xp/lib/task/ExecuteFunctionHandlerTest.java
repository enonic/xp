package com.enonic.xp.lib.task;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.impl.task.MockTaskService;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class ExecuteFunctionHandlerTest
    extends ScriptTestSupport
{
    private TaskService taskService;

    public volatile Object recorded;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        taskService = Mockito.mock( TaskService.class );
        addService( TaskService.class, taskService );
    }

    public void record( final Object value )
    {
        this.recorded = value;
    }

    @Test
    void testExample()
    {
        if ( isGraalJs() )
        {
            assertThrows( RuntimeException.class, () -> runScript( "/lib/xp/examples/task/executeFunction.js" ) );
            return;
        }
        final TaskId taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        Mockito.when( this.taskService.submitLocalTask( any() ) ).thenReturn( taskId );

        runScript( "/lib/xp/examples/task/executeFunction.js" );
    }

    @Test
    void testExecuteFunction()
    {
        if ( isGraalJs() )
        {
            // fails fast at submit: a function cannot leave the script context that created it
            final RuntimeException e =
                assertThrows( RuntimeException.class, () -> runFunction( "/test/executeFunction-test.js", "executeFunction" ) );
            assertTrue( e.getMessage().contains( "not supported on the GraalJS engine" ), e.getMessage() );
            return;
        }
        Mockito.when( this.taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "123" ) );

        runFunction( "/test/executeFunction-test.js", "executeFunction" );
    }

    @Test
    void testExecuteFunctionThrowingError()
    {
        if ( isGraalJs() )
        {
            assertThrows( RuntimeException.class,
                          () -> runFunction( "/test/executeFunction-test.js", "executeFunctionThrowingError" ) );
            return;
        }
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        addService( TaskService.class, mockTaskMan );

        assertThrows( ResourceProblemException.class,
                      () -> runFunction( "/test/executeFunction-test.js", "executeFunctionThrowingError" ) );
    }

    @Test
    void testClosureFunction()
    {
        if ( isGraalJs() )
        {
            assertThrows( RuntimeException.class, () -> runFunction( "/test/executeFunction-test.js", "executeClosureFunction" ) );
            return;
        }
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        addService( TaskService.class, mockTaskMan );

        runFunction( "/test/executeFunction-test.js", "executeClosureFunction" );

        // engines without pooling keep the historical closure behavior
        assertEquals( "closure", this.recorded );
    }

    private static boolean isGraalJs()
    {
        return "GraalJS".equalsIgnoreCase( System.getProperty( "xp.script-engine", "Nashorn" ) );
    }
}
