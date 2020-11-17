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

public final class SubmitNamedTaskHandler
    implements ScriptBean
{
    private Supplier<TaskService> taskServiceSupplier;

    private Supplier<PortalRequest> requestSupplier;

    private Supplier<PropertyTreeMarshallerService> propertyTreeMarshallerServiceSupplier;

    private String name;

    private ScriptValue config;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setConfig( final ScriptValue config )
    {
        this.config = config;
    }

    public String submit()
    {
        name = name == null ? "" : name;

        final DescriptorKey taskKey;
        if ( name.contains( ":" ) )
        {
            taskKey = DescriptorKey.from( name );
        }
        else
        {
            final ApplicationKey app = getApplication();
            if ( app == null )
            {
                throw new RuntimeException( "Could not resolve current application for named task: '" + name + "'" );
            }
            taskKey = DescriptorKey.from( app, name );
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
            return ApplicationKey.from( SubmitNamedTaskHandler.class );
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
