package com.enonic.xp.impl.task.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;

public final class NamedTaskScript
    implements RunnableTask
{
    private final static Logger LOG = LoggerFactory.getLogger( NamedTaskScript.class );

    public static final String SCRIPT_METHOD_NAME = "run";

    private final ScriptExports scriptExports;

    private final TaskDescriptor taskDescriptor;

    NamedTaskScript( final ScriptExports scriptExports, TaskDescriptor taskDescriptor )
    {
        this.scriptExports = scriptExports;
        this.taskDescriptor = taskDescriptor;
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        TaskProgressReporterContext.withContext( this::runTask ).run( id, progressReporter );
    }

    private void runTask( final TaskId id, final ProgressReporter progressReporter )
    {
        try
        {
            this.scriptExports.executeMethod( SCRIPT_METHOD_NAME );
        }
        catch ( Throwable t )
        {
            LOG.error( "Error executing named task [{}] '{}' with id {}: {}", taskDescriptor.getKey().toString(),
                       taskDescriptor.getDescription(), id.toString(), t.getMessage(), t );
            throw t;
        }
    }

}
