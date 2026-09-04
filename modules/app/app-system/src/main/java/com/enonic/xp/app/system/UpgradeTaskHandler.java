package com.enonic.xp.app.system;

import com.enonic.xp.app.system.json.SystemDumpUpgradeResultJson;
import com.enonic.xp.app.system.listener.UpgradeListenerImpl;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.DumpUpgradeResult;
import com.enonic.xp.dump.SystemDumpUpgradeParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskProgressReporterContext;

/**
 * The {@code com.enonic.xp.app.system:upgrade} task: upgrades a dump on disk to the current model version.
 */
public class UpgradeTaskHandler
    implements ScriptBean
{
    private DumpService dumpService;

    private String name;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void execute()
    {
        final ProgressReporter progressReporter = TaskProgressReporterContext.current();
        final UpgradeListenerImpl listener = new UpgradeListenerImpl( progressReporter );

        final DumpUpgradeResult result =
            dumpService.upgrade( SystemDumpUpgradeParams.create().dumpName( name ).upgradeListener( listener ).build() );
        listener.finished();

        progressReporter.progress( ProgressReportParams.create( SystemDumpUpgradeResultJson.from( result ).toString() ).build() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.dumpService = context.getService( DumpService.class ).get();
    }
}
