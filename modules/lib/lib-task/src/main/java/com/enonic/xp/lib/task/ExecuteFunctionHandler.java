package com.enonic.xp.lib.task;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public final class ExecuteFunctionHandler
    implements ScriptBean
{
    private Supplier<TaskService> taskServiceSupplier;

    private Supplier<PortalScriptService> scriptServiceSupplier;

    private ResourceKey detachedRunner;

    private String description;

    private Function<Void, Void> taskFunction;

    private String source;

    private Object params;

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public void setFunc( final Function<Void, Void> taskFunction )
    {
        this.taskFunction = taskFunction;
    }

    /**
     * Source of a detached function: re-materialized in the executing script context instead of
     * being routed back to the submitting one, see {@link DetachedFunctionTaskWrapper}.
     */
    public void setSource( final String source )
    {
        this.source = source;
    }

    public void setParams( final ScriptValue value )
    {
        this.params = value == null ? null : requireDataOnly( toData( value ) );
    }

    public String executeFunction()
    {
        final TaskService taskService = taskServiceSupplier.get();
        final RunnableTask runnableTask = this.source != null
            ? new DetachedFunctionTaskWrapper( scriptServiceSupplier, detachedRunner, source, params, description )
            : new TaskWrapper( taskFunction, description );
        final TaskId taskId =
            taskService.submitLocalTask( SubmitLocalTaskParams.create().runnableTask( runnableTask ).description( description ).build() );

        return taskId.toString();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        taskServiceSupplier = context.getService( TaskService.class );
        scriptServiceSupplier = context.getService( PortalScriptService.class );
        detachedRunner = ResourceKey.from( context.getApplicationKey(), DetachedFunctionTaskWrapper.RUNNER_PATH );
    }

    private static Object toData( final ScriptValue value )
    {
        if ( value.isObject() )
        {
            return value.getMap();
        }
        else if ( value.isArray() )
        {
            return value.getList();
        }
        else if ( value.isFunction() )
        {
            throw functionsNotAllowed();
        }
        else
        {
            return value.getValue();
        }
    }

    private static Object requireDataOnly( final Object value )
    {
        if ( value instanceof Function )
        {
            throw functionsNotAllowed();
        }
        else if ( value instanceof Map )
        {
            ( (Map<?, ?>) value ).values().forEach( ExecuteFunctionHandler::requireDataOnly );
        }
        else if ( value instanceof List )
        {
            ( (List<?>) value ).forEach( ExecuteFunctionHandler::requireDataOnly );
        }
        return value;
    }

    private static IllegalArgumentException functionsNotAllowed()
    {
        return new IllegalArgumentException(
            "Detached task params must contain data only - functions cannot cross into a detached task" );
    }
}
