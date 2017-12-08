package com.enonic.xp.lib.task;

import java.time.Instant;

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

public class GetTaskHandlerTest
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
            description( "Long running task" ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            progress( TaskProgress.create().current( 33 ).total( 42 ).info( "Processing item 33" ).build() ).
            build();
        Mockito.when( this.taskService.getTaskInfo( TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" ) ) ).thenReturn( taskInfo );

        runScript( "/site/lib/xp/examples/task/get.js" );
    }

    @Test
    public void testGetTaskExisting()
        throws Exception
    {
        final TaskInfo taskInfo = TaskInfo.create().
            state( TaskState.RUNNING ).
            id( TaskId.from( "123" ) ).
            description( "Long running task" ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            progress( TaskProgress.create().current( 33 ).total( 42 ).info( "Processing item 33" ).build() ).
            build();
        Mockito.when( this.taskService.getTaskInfo( TaskId.from( "123" ) ) ).thenReturn( taskInfo );

        runFunction( "/site/test/get-test.js", "getExistingTask" );
    }

    @Test
    public void testGetTaskNotFound()
        throws Exception
    {
        Mockito.when( this.taskService.getTaskInfo( TaskId.from( "123" ) ) ).thenReturn( null );

        runFunction( "/site/test/get-test.js", "getTaskNotFound" );
    }

}