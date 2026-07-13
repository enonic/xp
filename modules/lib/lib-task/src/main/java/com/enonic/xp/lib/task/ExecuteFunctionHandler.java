package com.enonic.xp.lib.task;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
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

    private Function<Object, Object> taskFunction;

    private String source;

    private Object params;

    private ScriptValue paramsValue;

    private boolean detached;

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public void setFunc( final Function<Object, Object> taskFunction )
    {
        this.taskFunction = taskFunction;
    }

    public void setDetached( final boolean detached )
    {
        this.detached = detached;
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
        this.paramsValue = value;
        this.params = value == null ? null : requireDataOnly( toData( value ) );
    }

    public String executeFunction()
    {
        final TaskService taskService = taskServiceSupplier.get();
        final RunnableTask runnableTask = useDetached()
            ? new DetachedFunctionTaskWrapper( scriptServiceSupplier, detachedRunner, source, params, description )
            : new TaskWrapper( taskFunction, paramsValue == null ? null : paramsValue.getValue(), description );
        final TaskId taskId =
            taskService.submitLocalTask( SubmitLocalTaskParams.create().runnableTask( runnableTask ).description( description ).build() );

        return taskId.toString();
    }

    /**
     * Pooled script engines always run task functions detached (Web Worker semantics: only
     * {@code params} and globals in scope) — a routed closure would serialize the task with the
     * submitting context. Engines without pooling keep the historical closure behavior unless
     * detached is requested explicitly. Probed via {@link ScriptExports#isolated()}: pooled
     * engines return a distinct view.
     */
    private boolean useDetached()
    {
        if ( detached )
        {
            return true;
        }
        if ( source == null )
        {
            return false;
        }
        try
        {
            final PortalScriptService scriptService = scriptServiceSupplier.get();
            if ( scriptService == null )
            {
                return false;
            }
            final ScriptExports runnerExports = scriptService.execute( detachedRunner );
            return runnerExports.isolated() != runnerExports;
        }
        catch ( RuntimeException e )
        {
            // no script service (minimal runtimes, tests): keep the routed behavior;
            // explicitly detached tasks bypass this probe and surface real errors at run time
            return false;
        }
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
