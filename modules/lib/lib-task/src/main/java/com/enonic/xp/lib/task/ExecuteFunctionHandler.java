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

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public void setFunc( final Function<Object, Object> taskFunction )
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

    /**
     * Data for a detached function, delivered as its single argument. The routed path binds
     * params to the function on the JS side instead — closures are legal there.
     */
    public void setParams( final ScriptValue value )
    {
        this.params = value == null ? null : requireDataOnly( toData( value ) );
    }

    public String executeFunction()
    {
        final TaskService taskService = taskServiceSupplier.get();
        final RunnableTask runnableTask;
        if ( useDetached() )
        {
            requireTransferableSource();
            runnableTask = new DetachedFunctionTaskWrapper( scriptServiceSupplier, detachedRunner, source, params, description );
        }
        else
        {
            runnableTask = new TaskWrapper( taskFunction, description );
        }
        final TaskId taskId =
            taskService.submitLocalTask( SubmitLocalTaskParams.create().runnableTask( runnableTask ).description( description ).build() );

        return taskId.toString();
    }

    /**
     * The engine decides how the task function runs. Pooled script engines (GraalJS) always run
     * it detached — Web Worker semantics: re-materialized from source in a fresh context with
     * {@code params}, {@code log} and {@code require} in scope, because a routed closure would
     * serialize the task with the submitting context. Engines without pooling (Nashorn) always
     * keep the historical attached-closure behavior. Probed via {@link ScriptExports#background()}:
     * pooled engines return a distinct view.
     */
    private boolean useDetached()
    {
        if ( source == null )
        {
            return false;
        }
        final PortalScriptService scriptService;
        try
        {
            scriptService = scriptServiceSupplier.get();
        }
        catch ( RuntimeException e )
        {
            // no script service registered (minimal runtimes, tests): keep the attached behavior.
            // Only the lookup is shielded — a probe failure on a real service must fail the submit
            // loudly, not silently flip this submission's execution semantics.
            return false;
        }
        if ( scriptService == null )
        {
            return false;
        }
        final ScriptExports runnerExports = scriptService.execute( detachedRunner );
        return runnerExports.background() != runnerExports;
    }

    /**
     * {@code Function.prototype.toString} of a bound or native(-wrapped) function is the
     * unparseable {@code function () { [native code] }} form — fail at submit with a clear
     * message, not on the task thread with a cryptic eval error.
     */
    private void requireTransferableSource()
    {
        if ( source.trim().endsWith( "{ [native code] }" ) )
        {
            throw new IllegalArgumentException(
                "Detached task func must be a plain JavaScript function - a bound or native function has no transferable source" );
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
