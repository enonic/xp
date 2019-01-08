package com.enonic.xp.impl.server.rest.task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.SystemDumpUpgradeParams;
import com.enonic.xp.dump.SystemDumpUpgradeResult;
import com.enonic.xp.impl.server.rest.model.SystemDumpUpgradeRequestJson;
import com.enonic.xp.task.AbstractRunnableTaskTest;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.util.Version;

public class UpgradeRunnableTaskTest
    extends AbstractRunnableTaskTest
{
    private DumpService dumpService;

    @Before
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.dumpService = Mockito.mock( DumpService.class );
    }

    protected UpgradeRunnableTask createAndRunTask()
    {
        return null;
    }

    protected UpgradeRunnableTask createAndRunTask( final SystemDumpUpgradeRequestJson params )
    {
        final UpgradeRunnableTask task = UpgradeRunnableTask.create().
            description( "upgrade" ).
            taskService( taskService ).
            dumpService( dumpService ).
            params( params ).
            build();

        task.run( TaskId.from( "taskId" ), progressReporter );

        return task;
    }

    @Test
    public void upgrade()
    {
        final SystemDumpUpgradeResult upgradeResult = SystemDumpUpgradeResult.create().
            initialVersion( Version.emptyVersion ).
            upgradedVersion( new Version( 1, 0, 0 ) ).
            build();
        Mockito.when( this.dumpService.upgrade( Mockito.isA( SystemDumpUpgradeParams.class ) ) ).thenReturn( upgradeResult );

        final UpgradeRunnableTask task = createAndRunTask( new SystemDumpUpgradeRequestJson( "dump-name" ) );

        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "upgrade" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "upgrade_result.json" ), jsonTestHelper.stringToJson( result ) );
    }

}
