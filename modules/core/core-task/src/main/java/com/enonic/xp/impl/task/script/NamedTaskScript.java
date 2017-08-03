package com.enonic.xp.impl.task.script;

import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public final class NamedTaskScript
    implements RunnableTask
{
    public static final String SCRIPT_METHOD_NAME = "run";

    private final ScriptExports scriptExports;

    NamedTaskScript( final ScriptExports scriptExports )
    {
        this.scriptExports = scriptExports;
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        this.scriptExports.executeMethod( SCRIPT_METHOD_NAME );
    }

}
