package com.enonic.xp.lib.task;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.task.TaskState;
import com.enonic.xp.testing.ScriptTestSupport;

public class IsRunningHandlerTest
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
        final TaskInfo taskInfo = TaskInfo.create().
            state( TaskState.RUNNING ).
            id( TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" ) ).
            name( "com.enonic.myapp:clean-up-task" ).
            description( "Long running task" ).
            progress( TaskProgress.create().current( 33 ).total( 42 ).info( "Processing item 33" ).build() ).
            build();
        Mockito.when( this.taskService.getRunningTasks() ).thenReturn( Collections.singletonList( taskInfo ) );

        runScript( "/lib/xp/examples/task/isRunning.js" );
    }

    @Test
    public void testIsRunningFalse()
        throws Exception
    {
        Mockito.when( this.taskService.getRunningTasks() ).thenReturn( new ArrayList<>() );

        runFunction( "/test/isRunning-test.js", "isRunning" );
    }

}