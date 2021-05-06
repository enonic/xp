package com.enonic.xp.lib.task;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
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

    private Supplier<PortalRequest> requestSupplier;

    private Supplier<PropertyTreeMarshallerService> propertyTreeMarshallerServiceSupplier;

    private String descriptor;

    private ScriptValue config;

    public void setDescriptor( final String descriptor )
    {
        this.descriptor = descriptor;
    }

    public void setConfig( final ScriptValue config )
    {
        this.config = config;
    }

    public String submitTask()
    {
        descriptor = descriptor == null ? "" : descriptor;

        final DescriptorKey taskKey;
        if ( descriptor.contains( ":" ) )
        {
            taskKey = DescriptorKey.from( descriptor );
        }
        else
        {
            final ApplicationKey app = getApplication();
            if ( app == null )
            {
                throw new RuntimeException( "Could not resolve current application for descriptord task: '" + descriptor + "'" );
            }
            taskKey = DescriptorKey.from( app, descriptor );
        }

        final TaskService taskService = taskServiceSupplier.get();

        PropertyTree data = propertyTreeMarshallerServiceSupplier.get().
            marshal( Optional.ofNullable( config ).map( ScriptValue::getMap ).orElse( Map.of() ) );

        final SubmitTaskParams params = SubmitTaskParams.create().
            descriptorKey( taskKey ).
            data( data ).
            build();
        final TaskId taskId = taskService.submitTask( params );

        return taskId.toString();
    }

    private ApplicationKey getApplication()
    {
        PortalRequest portalRequest = requestSupplier.get();
        if ( portalRequest != null )
        {
            return portalRequest.getApplicationKey();
        }
        else
        {
            return ApplicationKey.from( SubmitTaskHandler.class );
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        requestSupplier = context.getBinding( PortalRequest.class );
        taskServiceSupplier = context.getService( TaskService.class );
        propertyTreeMarshallerServiceSupplier = context.getService( PropertyTreeMarshallerService.class );
    }
}
