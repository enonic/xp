package com.enonic.xp.app.system;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.DumpUpgradeResult;
import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.dump.SystemDumpUpgradeParams;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.util.Version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpgradeTaskHandlerTest
    extends ScriptTestSupport
{
    private final JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    @Mock
    private DumpService dumpService;

    @Mock
    private ProgressReporter progressReporter;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        addService( DumpService.class, this.dumpService );
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
        final ArgumentCaptor<SystemDumpUpgradeParams> paramsCaptor = ArgumentCaptor.forClass( SystemDumpUpgradeParams.class );
        when( dumpService.upgrade( paramsCaptor.capture() ) ).thenReturn( upgradeResult );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/UpgradeTaskHandlerTest.js", "upgrade" ) )
            .run( TaskId.from( "task" ), progressReporter );

        assertEquals( "dump-name", paramsCaptor.getValue().getDumpName() );

        final ArgumentCaptor<ProgressReportParams> progressCaptor = ArgumentCaptor.forClass( ProgressReportParams.class );
        verify( progressReporter, times( 2 ) ).progress( progressCaptor.capture() );
        final List<ProgressReportParams> result = progressCaptor.getAllValues();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "upgrade_result.json" ),
                                         jsonTestHelper.stringToJson( result.get( 1 ).getMessage() ) );
    }
}
