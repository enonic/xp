package com.enonic.xp.lib.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.task.TaskState;
import com.enonic.xp.testing.ScriptTestSupport;

public class ListTasksHandlerTest
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
        Mockito.when( this.taskService.getAllTasks() ).thenReturn( taskList() );

        runScript( "/site/lib/xp/examples/task/list.js" );
    }

    @Test
    public void testListExisting()
        throws Exception
    {
        Mockito.when( this.taskService.getAllTasks() ).thenReturn( taskList() );

        runFunction( "/site/test/list-test.js", "getExistingTasks" );
    }

    @Test
    public void testListNone()
        throws Exception
    {
        Mockito.when( this.taskService.getAllTasks() ).thenReturn( new ArrayList<>() );

        runFunction( "/site/test/list-test.js", "listNone" );
    }

    private List<TaskInfo> taskList()
    {
        final TaskInfo taskInfo1 = TaskInfo.create().
            state( TaskState.RUNNING ).
            id( TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" ) ).
            description( "Long running task" ).
            progress( TaskProgress.create().current( 33 ).total( 42 ).info( "Processing item 33" ).build() ).
            build();
        final TaskInfo taskInfo2 = TaskInfo.create().
            state( TaskState.FINISHED ).
            id( TaskId.from( "b6173bcb-bf54-409b-aa6b-96ae6fcec263" ) ).
            description( "Update statistics" ).
            progress( TaskProgress.create().info( "Work completed" ).build() ).
            build();
        final TaskInfo taskInfo3 = TaskInfo.create().
            state( TaskState.FAILED ).
            id( TaskId.from( "e1f57280-d672-4cd8-b674-98e26e5b69ae" ) ).
            description( "Import remote data" ).
            progress( TaskProgress.create().current( 33 ).total( 100 ).info( "Fetching data" ).build() ).
            build();

        return Arrays.asList( taskInfo1, taskInfo2, taskInfo3 );
    }
}