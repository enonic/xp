package com.enonic.xp.lib.task;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;

/**
 * Runs a detached task function: the function travels as source plus eagerly converted
 * parameters, and is re-materialized by the runner script in whatever script context serves this
 * thread — no context-bound state crosses the thread boundary, so detached tasks execute in
 * parallel on pooled script engines.
 */
final class DetachedFunctionTaskWrapper
    implements RunnableTask
{
    private static final Logger LOG = LoggerFactory.getLogger( DetachedFunctionTaskWrapper.class );

    static final String RUNNER_PATH = "/lib/xp/detached-task.js";

    private final Supplier<PortalScriptService> scriptService;

    private final ResourceKey runner;

    private final String source;

    private final Object params;

    private final String description;

    DetachedFunctionTaskWrapper( final Supplier<PortalScriptService> scriptService, final ResourceKey runner, final String source,
                                 final Object params, final String description )
    {
        this.scriptService = scriptService;
        this.runner = runner;
        this.source = source;
        this.params = params;
        this.description = description;
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        TaskProgressReporterContext.withContext( ( taskId, reporter ) -> runTask( taskId ) ).run( id, progressReporter );
    }

    private void runTask( final TaskId id )
    {
        try
        {
            final PortalScriptService service = scriptService.get();
            if ( service == null )
            {
                throw new IllegalStateException( "Cannot run detached task: PortalScriptService is not available" );
            }
            // a fresh context per run on pooled engines — detached tasks ride virtual threads and
            // may IO-wait for long; they must occupy neither a request-serving context for the run
            // nor one for resolving the runner (executeBackground touches no pooled slot)
            service.executeBackground( runner ).executeMethod( "run", source, params );
        }
        catch ( Throwable t )
        {
            LOG.error( "Error executing detached task [{}] '{}': {}", id, description, t.getMessage(), t );
            throw t;
        }
    }
}
