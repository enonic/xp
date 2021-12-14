package com.enonic.xp.lib.task;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.impl.PropertyTreeMarshallerServiceFactory;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.ArgumentMatchers.any;

class SubmitTaskHandlerTest
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
        final TaskDescriptorService taskDescriptorService = Mockito.mock( TaskDescriptorService.class );
        addService( TaskDescriptorService.class, taskDescriptorService );
        final MixinService mixinService = Mockito.mock( MixinService.class );
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

        addService( PropertyTreeMarshallerService.class, PropertyTreeMarshallerServiceFactory.newInstance( mixinService ) );
    }

    @Test
    void testExample()
    {
        final TaskId taskId = TaskId.from( "7ca603c1-3b88-4009-8f30-46ddbcc4bb19" );
        Mockito.when( this.taskService.submitTask( any( SubmitTaskParams.class ) ) ).
            thenReturn( taskId );

        runScript( "/lib/xp/examples/task/submitTask.js" );
    }

    @Test
    void testSubmitTask()
    {
        Mockito.when( this.taskService.submitTask( any( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "123" ) );

        runFunction( "/test/submitTask-test.js", "submitTask" );
    }

    @Test
    void testSubmitNamed()
    {
        Mockito.when( this.taskService.submitTask( any( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "123" ) );

        runFunction( "/test/submitTask-test.js", "submitNamed" );
    }

    @Test
    void submitTaskFromApp()
    {
        final MockTaskService mockTaskMan = new MockTaskService();
        mockTaskMan.taskId = TaskId.from( "123" );
        this.taskService = mockTaskMan;
        addService( TaskService.class, taskService );

        runFunction( "/test/submitTask-test.js", "submitTaskFromApp" );
    }
}
