package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.DumpUpgradeResult;
import com.enonic.xp.dump.SystemDumpUpgradeParams;
import com.enonic.xp.impl.server.rest.model.SystemDumpUpgradeResultJson;
import com.enonic.xp.impl.server.rest.task.listener.UpgradeListenerImpl;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.upgrade.UpgradeListener;

public class UpgradeRunnableTask
    implements RunnableTask
{
    private final String name;

    private final DumpService dumpService;

    private UpgradeRunnableTask( Builder builder )
    {
        this.name = builder.name;

        this.dumpService = builder.dumpService;
    }

    public static Builder create()
    {
        return new Builder();
    }


    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final UpgradeListener upgradeListener = new UpgradeListenerImpl( progressReporter );

        final SystemDumpUpgradeParams upgradeParams =
            SystemDumpUpgradeParams.create().dumpName( name ).upgradeListener( upgradeListener ).build();

        final DumpUpgradeResult result = this.dumpService.upgrade( upgradeParams );
        upgradeListener.finished();

        progressReporter.progress( ProgressReportParams.create( SystemDumpUpgradeResultJson.from( result ).toString() ).build() );
    }

    public static class Builder
    {
        private String name;

        private DumpService dumpService;

        public Builder name( String name )
        {
            this.name = name;
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
