package com.enonic.xp.lib.task;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskKey;
import com.enonic.xp.task.TaskService;

public final class SubmitNamedTaskHandler
    implements ScriptBean
{
    private Supplier<TaskService> taskServiceSupplier;

    private String name;

    public void setName( final String name )
    {
        this.name = name;
    }

    public String submit()
    {
        name = name == null ? "" : name;

        final TaskKey taskKey;
        if ( name.contains( ":" ) )
        {
            taskKey = TaskKey.from( name );
        }
        else
        {
            final ApplicationKey app = getApplication();
            if ( app == null )
            {
                throw new RuntimeException( "Could not resolve current application for named task: '" + name + "'" );
            }
            taskKey = TaskKey.from( app, name );
        }

        final TaskService taskService = taskServiceSupplier.get();
        final TaskId taskId = taskService.submitTask( taskKey );

        return taskId.toString();
    }

    private ApplicationKey getApplication()
    {
        PortalRequest portalRequest = PortalRequestAccessor.get();
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
        taskServiceSupplier = context.getService( TaskService.class );
    }
}
