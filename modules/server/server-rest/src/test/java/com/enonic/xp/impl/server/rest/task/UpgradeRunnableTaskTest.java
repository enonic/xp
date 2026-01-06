package com.enonic.xp.impl.server.rest.task;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.DumpUpgradeResult;
import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.dump.SystemDumpUpgradeParams;
import com.enonic.xp.impl.server.rest.model.SystemDumpUpgradeRequestJson;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.util.Version;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpgradeRunnableTaskTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    private DumpService dumpService;

    @BeforeEach
    void setUp()
    {
        this.dumpService = mock( DumpService.class );
    }

    private UpgradeRunnableTask createTask( final SystemDumpUpgradeRequestJson params )
    {
        return UpgradeRunnableTask.create().dumpService( dumpService ).name( params.getName() ).build();
    }

    @Test
    void upgrade()
    {
        final DumpUpgradeResult upgradeResult = DumpUpgradeResult.create()
            .initialVersion( Version.emptyVersion )
            .upgradedVersion( new Version( 1 ) )
            .stepResult( DumpUpgradeStepResult.create()
                             .stepName( "Step1" )
                             .initialVersion( Version.emptyVersion )
                             .upgradedVersion( new Version( 1 ) )
                             .build() )
            .build();
        when( this.dumpService.upgrade( any( SystemDumpUpgradeParams.class ) ) ).thenReturn( upgradeResult );

        final UpgradeRunnableTask task = createTask( new SystemDumpUpgradeRequestJson( "dump-name" ) );

        ProgressReporter progressReporter = mock( ProgressReporter.class );

        task.run( TaskId.from( "taskId" ), progressReporter );

        final ArgumentCaptor<ProgressReportParams> progressReporterCaptor = ArgumentCaptor.forClass( ProgressReportParams.class );
        verify( progressReporter, times( 2 ) ).progress( progressReporterCaptor.capture() );

        final List<ProgressReportParams> result = progressReporterCaptor.getAllValues();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "upgrade_result.json" ),
                                         jsonTestHelper.stringToJson( result.get( 1 ).getMessage() ) );
    }

}
