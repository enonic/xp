package com.enonic.xp.lib.task;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public final class SubmitTaskHandler
    implements ScriptBean
{
    private Supplier<TaskService> taskServiceSupplier;

    private DescriptorKey descriptorKey;

    private String name;

    private ScriptValue config;

    private ApplicationKey applicationKey;

    @Override
    public void initialize( final BeanContext context )
    {
        this.applicationKey = context.getApplicationKey();
        this.taskServiceSupplier = context.getService( TaskService.class );
    }

    public void setDescriptor( final String value )
    {
        this.descriptorKey = value.indexOf( ':' ) == -1 ? DescriptorKey.from( applicationKey, value ) : DescriptorKey.from( value );
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setConfig( final ScriptValue config )
    {
        this.config = config;
    }

    public String submitTask()
    {
        final TaskService taskService = taskServiceSupplier.get();

        PropertyTree data = PropertyTree.fromMap( Optional.ofNullable( config ).map( ScriptValue::getMap ).orElse( Map.of() ) );

        final SubmitTaskParams params = SubmitTaskParams.create().descriptorKey( descriptorKey ).name( name ).data( data ).build();
        final TaskId taskId = taskService.submitTask( params );

        return taskId.toString();
    }
}
