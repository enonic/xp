package com.enonic.xp.lib.task;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.Matchers.any;

public class SubmitNamedTaskHandlerTest
    extends ScriptTestSupport
{
    private TaskService taskService;

    private TaskDescriptorService taskDescriptorService;

    private MixinService mixinService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        taskService = Mockito.mock( TaskService.class );
        addService( TaskService.class, taskService );
        taskDescriptorService = Mockito.mock( TaskDescriptorService.class );
        addService( TaskDescriptorService.class, taskDescriptorService );
        mixinService = Mockito.mock( MixinService.class );
        addService( MixinService.class, mixinService );

        final Form cfg1 = Form.create().
            addFormItem( Input.create().
                name( "count" ).
                label( "Count" ).
                inputType( InputTypeName.LONG ).
                required( true ).
                build() ).
            build();
        final Form cfg2 = Form.create().
            addFormItem( Input.create().
                name( "values" ).
                label( "Values" ).
                inputType( InputTypeName.TEXT_LINE ).
                required( true ).
                multiple( true ).
                build() ).
            build();
        final TaskDescriptor desc1 = TaskDescriptor.create().key( DescriptorKey.from( "myapplication:job42" ) ).config( cfg1 ).build();
        final TaskDescriptor desc2 = TaskDescriptor.create().key( DescriptorKey.from( "myapplication:my-task" ) ).config( cfg2 ).build();
        final TaskDescriptor desc3 = TaskDescriptor.create().key( DescriptorKey.from( "other-app:some-task" ) ).build();
        Mockito.when( taskDescriptorService.getTasks() ).thenReturn( Descriptors.from( desc1, desc2, desc3 ) );

        Mockito.when( mixinService.inlineFormItems( any( Form.class ) ) ).thenAnswer( invocation -> invocation.getArguments()[0] );
    }

    @Test
    public void testExample()
    {
        final TaskId taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        Mockito.when( this.taskService.submitTask( any( DescriptorKey.class ), any( PropertyTree.class ) ) ).thenReturn( taskId );

        runScript( "/lib/xp/examples/task/submitNamed.js" );
    }

    @Test
    public void testSubmitNamedTask()
        throws Exception
    {
        Mockito.when( this.taskService.submitTask( any( DescriptorKey.class ), any( PropertyTree.class ) ) ).thenReturn(
            TaskId.from( "123" ) );

        runFunction( "/test/submitNamed-test.js", "submitTask" );
    }

    @Test
    public void submitTaskFromApp()
        throws Exception
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "123" );
        this.taskService = mockTaskMan;
        addService( TaskService.class, taskService );

        runFunction( "/test/submitNamed-test.js", "submitTaskFromApp" );
    }
}