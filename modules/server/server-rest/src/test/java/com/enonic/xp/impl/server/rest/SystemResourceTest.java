package com.enonic.xp.impl.server.rest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.enonic.xp.impl.server.rest.model.SystemDumpRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemDumpUpgradeRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemLoadRequestJson;
import com.enonic.xp.impl.server.rest.task.DumpRunnableTask;
import com.enonic.xp.impl.server.rest.task.LoadRunnableTask;
import com.enonic.xp.impl.server.rest.task.UpgradeRunnableTask;
import com.enonic.xp.impl.server.rest.task.VacuumRunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskResultJson;
import com.enonic.xp.task.TaskService;

import static org.mockito.Matchers.eq;

public class SystemResourceTest
    extends ServerRestTestSupport
{
    private TaskService taskService;

    private SystemResource resource;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup()
        throws Exception
    {
    }

    @Test
    public void dumpTask()
        throws Exception
    {
        Mockito.when( taskService.submitTask( Mockito.isA( DumpRunnableTask.class ), eq( "dump" ) ) ).thenReturn(
            TaskId.from( "task-id" ) );

        final SystemDumpRequestJson json = Mockito.mock( SystemDumpRequestJson.class );

        final TaskResultJson result = resource.systemDump( json );
        assertEquals( "task-id", result.getTaskId() );
    }

    @Test
    public void load()
        throws Exception
    {
        Mockito.when( taskService.submitTask( Mockito.isA( LoadRunnableTask.class ), eq( "load" ) ) ).thenReturn(
            TaskId.from( "task-id" ) );

        final SystemLoadRequestJson json = Mockito.mock( SystemLoadRequestJson.class );

        final TaskResultJson result = resource.load( json );
        assertEquals( "task-id", result.getTaskId() );
    }

    @Test
    public void vacuum()
        throws Exception
    {
        Mockito.when( taskService.submitTask( Mockito.isA( VacuumRunnableTask.class ), eq( "vacuum" ) ) ).thenReturn(
            TaskId.from( "task-id" ) );

        final TaskResultJson result = resource.vacuum();
        assertEquals( "task-id", result.getTaskId() );
    }

    @Test
    public void upgrade()
        throws Exception
    {
        Mockito.when( taskService.submitTask( Mockito.isA( UpgradeRunnableTask.class ), eq( "upgrade" ) ) ).thenReturn(
            TaskId.from( "task-id" ) );

        final SystemDumpUpgradeRequestJson json = Mockito.mock( SystemDumpUpgradeRequestJson.class );

        final TaskResultJson result = resource.upgrade( json );
        assertEquals( "task-id", result.getTaskId() );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.taskService = Mockito.mock( TaskService.class );

        resource = new SystemResource();
        resource.setTaskService( taskService );
        return resource;
    }
}
