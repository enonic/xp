package com.enonic.xp.lib.task;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.Matchers.any;

public class SubmitNamedTaskHandlerTest
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
        Mockito.when( this.taskService.submitTask( any( DescriptorKey.class ) ) ).thenReturn( taskId );

        runScript( "/site/lib/xp/examples/task/submitNamed.js" );
    }

    @Test
    public void testSubmitNamedTask()
        throws Exception
    {
        Mockito.when( this.taskService.submitTask( any( DescriptorKey.class ) ) ).thenReturn( TaskId.from( "123" ) );

        runFunction( "/site/test/submitNamed-test.js", "submitTask" );
    }

    @Test
    public void submitTaskFromApp()
        throws Exception
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "123" );
        this.taskService = mockTaskMan;
        addService( TaskService.class, taskService );

        runFunction( "/site/test/submitNamed-test.js", "submitTaskFromApp" );
    }
}