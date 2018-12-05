package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.SystemDumpUpgradeParams;
import com.enonic.xp.dump.SystemDumpUpgradeResult;
import com.enonic.xp.impl.server.rest.model.SystemDumpUpgradeRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemDumpUpgradeResultJson;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

public class UpgradeRunnableTask
    extends AbstractRunnableTask
{
    private final SystemDumpUpgradeRequestJson params;

    private final DumpService dumpService;

    private UpgradeRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.dumpService = builder.dumpService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final SystemDumpUpgradeParams upgradeParams = SystemDumpUpgradeParams.create().
            dumpName( params.getName() ).
            build();

        final SystemDumpUpgradeResult result = this.dumpService.upgrade( upgradeParams );
        progressReporter.info( SystemDumpUpgradeResultJson.from( result ).toString() );

    }

    public static class Builder
        extends AbstractRunnableTask.Builder<Builder>
    {
        private SystemDumpUpgradeRequestJson params;

        private DumpService dumpService;

        public Builder params( SystemDumpUpgradeRequestJson params )
        {
            this.params = params;
            return this;
        }

        public Builder dumpService( final DumpService dumpService )
        {
            this.dumpService = dumpService;
            return this;
        }

        public UpgradeRunnableTask build()
        {
            return new UpgradeRunnableTask( this );
        }
    }
}
