package com.enonic.xp.lib.task;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.impl.task.MockTaskService;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.runtime.BootstrapParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        addService( PortalScriptService.class, new PortalScriptService()
        {
            @Override
            public boolean hasScript( final ResourceKey script )
            {
                return true;
            }

            @Override
            public void bootstrap( final BootstrapParams params )
            {
            }

            @Override
            public ScriptExports execute( final ResourceKey script )
            {
                return runScript( script );
            }

            @Override
            public ScriptExports executeBackground( final ResourceKey script )
            {
                return runScriptBackground( script );
            }

            @Override
            public boolean isPooled( final ApplicationKey application )
            {
                return isScriptEnginePooled();
            }

            @Override
            public CompletableFuture<ScriptExports> executeAsync( final ResourceKey script )
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public ScriptValue toScriptValue( final ResourceKey script, final Object value )
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public Object toNativeObject( final ResourceKey script, final Object value )
            {
                throw new UnsupportedOperationException();
            }
        } );
    }

    public void record( final Object value )
    {
        this.recorded = value;
    }

    @Test
    void testExample()
    {
        final TaskId taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        Mockito.when( this.taskService.submitLocalTask( any() ) ).thenReturn( taskId );

        runScript( "/lib/xp/examples/task/executeFunction.js" );
    }

    @Test
    void testExecuteFunction()
    {
        Mockito.when( this.taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "123" ) );

        runFunction( "/test/executeFunction-test.js", "executeFunction" );
    }

    @Test
    void testExecuteFunctionThrowingError()
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        this.taskService = mockTaskMan;
        addService( TaskService.class, taskService );

        assertThrows( ResourceProblemException.class,
                      () -> runFunction( "/test/executeFunction-test.js", "executeFunctionThrowingError" ) );
    }

    @Test
    void testExecuteFunctionWithParams()
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        addService( TaskService.class, mockTaskMan );

        runFunction( "/test/executeFunction-test.js", "executeFunctionWithParams" );

        assertEquals( 42, ( (Number) this.recorded ).intValue() );
    }

    @Test
    void testClosureFunction_alwaysDetachedOnPooledEngines()
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        addService( TaskService.class, mockTaskMan );

        if ( isPooledEngine() )
        {
            // tasks are always detached: the captured variable is not available
            assertThrows( RuntimeException.class, () -> runFunction( "/test/executeFunction-test.js", "executeClosureFunction" ) );
        }
        else
        {
            // engines without pooling keep the historical closure behavior
            runFunction( "/test/executeFunction-test.js", "executeClosureFunction" );
            assertEquals( "closure", this.recorded );
        }
    }

    private static boolean isPooledEngine()
    {
        return "GraalJS".equalsIgnoreCase( System.getProperty( "xp.script-engine", "Nashorn" ) );
    }

    @Test
    void testExecuteFunction_usingLibs()
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        addService( TaskService.class, mockTaskMan );

        runFunction( "/test/executeFunction-test.js", "executeFunctionUsingLibs" );

        assertEquals( 42, ( (Number) this.recorded ).intValue() );
    }

    @Test
    void testExecuteFunction_functionParamsRejected()
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        addService( TaskService.class, mockTaskMan );

        assertThrows( RuntimeException.class,
                      () -> runFunction( "/test/executeFunction-test.js", "executeRejectsFunctionParams" ) );
    }

    @Test
    void testExecuteFunction_arrayParams()
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        addService( TaskService.class, mockTaskMan );

        runFunction( "/test/executeFunction-test.js", "executeArrayParams" );

        assertEquals( 42, ( (Number) this.recorded ).intValue() );
    }

    @Test
    void testExecuteFunction_scalarParams()
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        addService( TaskService.class, mockTaskMan );

        runFunction( "/test/executeFunction-test.js", "executeScalarParams" );

        assertEquals( 42, ( (Number) this.recorded ).intValue() );
    }

    @Test
    void testExecuteFunction_functionInArrayParamsRejected()
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        addService( TaskService.class, mockTaskMan );

        assertThrows( RuntimeException.class,
                      () -> runFunction( "/test/executeFunction-test.js", "executeRejectsFunctionInArrayParams" ) );
    }

    @Test
    void testExecuteFunction_functionAsParamsRejected()
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        addService( TaskService.class, mockTaskMan );

        assertThrows( RuntimeException.class,
                      () -> runFunction( "/test/executeFunction-test.js", "executeRejectsFunctionAsParams" ) );
    }
}
