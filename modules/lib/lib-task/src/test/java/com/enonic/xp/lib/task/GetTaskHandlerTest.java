package com.enonic.xp.lib.task;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskManager;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskState;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetTaskHandlerTest
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
        final TaskInfo taskInfo = TaskInfo.create().
            state( TaskState.RUNNING ).
            id( TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" ) ).
            description( "Long running task" ).
            progress( TaskProgress.create().current( 33 ).total( 42 ).info( "Processing item 33" ).build() ).
            build();
        Mockito.when( this.taskManager.getTaskInfo( TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" ) ) ).thenReturn( taskInfo );

        runScript( "/site/lib/xp/examples/task/getTask.js" );
    }

    @Test
    public void testGetTaskExisting()
        throws Exception
    {
        final TaskInfo taskInfo = TaskInfo.create().
            state( TaskState.RUNNING ).
            id( TaskId.from( "123" ) ).
            description( "Long running task" ).
            progress( TaskProgress.create().current( 33 ).total( 42 ).info( "Processing item 33" ).build() ).
            build();
        Mockito.when( this.taskManager.getTaskInfo( TaskId.from( "123" ) ) ).thenReturn( taskInfo );

        runFunction( "/site/test/getTask-test.js", "getExistingTask" );
    }

    @Test
    public void testGetTaskNotFound()
        throws Exception
    {
        Mockito.when( this.taskManager.getTaskInfo( TaskId.from( "123" ) ) ).thenReturn( null );

        runFunction( "/site/test/getTask-test.js", "getTaskNotFound" );
    }

}