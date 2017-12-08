package com.enonic.xp.lib.task;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKey;
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
        final TaskInfo taskInfo = TaskInfo.create().
            state( TaskState.RUNNING ).
            id( TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" ) ).
            name( "com.enonic.myapp:clean-up" ).
            description( "Long running task" ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:user" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            progress( TaskProgress.create().current( 33 ).total( 42 ).info( "Processing item 33" ).build() ).
            build();

        Mockito.when( this.taskService.getAllTasks() ).thenReturn( taskList() ).thenReturn( Collections.singletonList( taskInfo ) );

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
            application( ApplicationKey.from( "com.enonic.app1" ) ).
            user( PrincipalKey.from( "user:store:user1" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            progress( TaskProgress.create().current( 33 ).total( 42 ).info( "Processing item 33" ).build() ).
            build();
        final TaskInfo taskInfo2 = TaskInfo.create().
            state( TaskState.FINISHED ).
            id( TaskId.from( "b6173bcb-bf54-409b-aa6b-96ae6fcec263" ) ).
            description( "Update statistics" ).
            application( ApplicationKey.from( "com.enonic.app2" ) ).
            user( PrincipalKey.from( "user:store:user2" ) ).
            startTime( Instant.parse( "2017-10-02T09:00:00Z" ) ).
            progress( TaskProgress.create().info( "Work completed" ).build() ).
            build();
        final TaskInfo taskInfo3 = TaskInfo.create().
            state( TaskState.FAILED ).
            id( TaskId.from( "e1f57280-d672-4cd8-b674-98e26e5b69ae" ) ).
            description( "Import remote data" ).
            application( ApplicationKey.from( "com.enonic.app3" ) ).
            user( PrincipalKey.from( "user:store:user3" ) ).
            startTime( Instant.parse( "2017-10-03T09:00:00Z" ) ).
            progress( TaskProgress.create().current( 33 ).total( 100 ).info( "Fetching data" ).build() ).
            build();

        return Arrays.asList( taskInfo1, taskInfo2, taskInfo3 );
    }
}