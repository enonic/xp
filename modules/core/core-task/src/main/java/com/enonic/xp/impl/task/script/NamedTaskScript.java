package com.enonic.xp.impl.task.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;

final class NamedTaskScript
    implements NamedTask
{
    private static final Logger LOG = LoggerFactory.getLogger( NamedTaskScript.class );

    public static final String SCRIPT_METHOD_NAME = "run";

    private final ScriptExports scriptExports;

    private final TaskDescriptor taskDescriptor;

    private final PropertyTree config;

    NamedTaskScript( final ScriptExports scriptExports, TaskDescriptor taskDescriptor, final PropertyTree config )
    {
        this.scriptExports = scriptExports;
        this.taskDescriptor = taskDescriptor;
        this.config = config;
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
            final PropertyTreeMapper configMapper = new PropertyTreeMapper( config );
            this.scriptExports.executeMethod( SCRIPT_METHOD_NAME, configMapper, id.toString() );
        }
        catch ( Throwable t )
        {
            LOG.error( "Error executing named task [{}] '{}' with id {}: {}", taskDescriptor.getKey(), taskDescriptor.getName(), id,
                       t.getMessage(), t );
            throw t;
        }
    }

    @Override
    public TaskDescriptor getTaskDescriptor()
    {
        return this.taskDescriptor;
    }
}
