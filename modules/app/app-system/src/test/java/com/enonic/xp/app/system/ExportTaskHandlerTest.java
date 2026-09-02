package com.enonic.xp.app.system;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.home.HomeDirSupport;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportTaskHandlerTest
    extends ScriptTestSupport
{
    private final JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    @TempDir
    Path temporaryFolder;

    @Captor
    private ArgumentCaptor<ExportNodesParams> paramsCaptor;

    @Mock
    private ExportService exportService;

    @Mock
    private ProgressReporter progressReporter;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        HomeDirSupport.set( temporaryFolder );
        addService( ExportService.class, this.exportService );
    }

    @Test
    void exportNodes()
    {
        final NodeExportResult nodeExportResult = NodeExportResult.create()
            .addNodePath( new NodePath( "/node/path" ) )
            .addBinary( new NodePath( "/binary" ), BinaryReference.from( "binaryRef" ) )
            .build();
        when( exportService.exportNodes( paramsCaptor.capture() ) ).thenReturn( nodeExportResult );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/ExportTaskHandlerTest.js", "exportNodes" ) )
            .run( TaskId.from( "task" ), progressReporter );

        assertEquals( "export", paramsCaptor.getValue().getExportName() );
        assertEquals( new NodePath( "/c" ), paramsCaptor.getValue().getSourceNodePath() );
        assertEquals( 1000, paramsCaptor.getValue().getBatchSize() );

        final ArgumentCaptor<ProgressReportParams> progressCaptor = ArgumentCaptor.forClass( ProgressReportParams.class );
        verify( progressReporter ).progress( progressCaptor.capture() );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "exportNodes_result.json" ),
                                         jsonTestHelper.stringToJson( progressCaptor.getValue().getMessage() ) );
    }

    @Test
    void exportNodesBatch()
    {
        when( exportService.exportNodes( paramsCaptor.capture() ) ).thenReturn(
            NodeExportResult.create().addNodePath( new NodePath( "/node/path" ) ).build() );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/ExportTaskHandlerTest.js", "exportNodesBatch" ) )
            .run( TaskId.from( "task" ), progressReporter );

        assertEquals( 50, paramsCaptor.getValue().getBatchSize() );
    }
}
