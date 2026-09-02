package com.enonic.xp.app.system;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportResult;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportTaskHandlerTest
    extends ScriptTestSupport
{
    private final JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    @TempDir
    Path temporaryFolder;

    @Captor
    private ArgumentCaptor<ImportNodesParams> paramsCaptor;

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
    void importNodes()
    {
        final NodeImportResult nodeImportResult = NodeImportResult.create()
            .added( new NodePath( "/node/path" ) )
            .addBinary( "/binary", BinaryReference.from( "binaryRef" ) )
            .updated( new NodePath( "/node2/path2" ) )
            .build();
        when( exportService.importNodes( paramsCaptor.capture() ) ).thenReturn( nodeImportResult );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/ImportTaskHandlerTest.js", "importNodes" ) )
            .run( TaskId.from( "task" ), progressReporter );

        assertEquals( "export", paramsCaptor.getValue().getExportName() );
        assertEquals( new NodePath( "/a" ), paramsCaptor.getValue().getTargetNodePath() );
        assertTrue( paramsCaptor.getValue().isImportNodeIds() );
        assertTrue( paramsCaptor.getValue().isImportPermissions() );
        assertNull( paramsCaptor.getValue().getXslt() );

        final ArgumentCaptor<ProgressReportParams> progressCaptor = ArgumentCaptor.forClass( ProgressReportParams.class );
        verify( progressReporter ).progress( progressCaptor.capture() );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "importNodes_result.json" ),
                                         jsonTestHelper.stringToJson( progressCaptor.getValue().getMessage() ) );
    }

    @Test
    void importNodesXsl()
    {
        when( exportService.importNodes( paramsCaptor.capture() ) ).thenReturn( NodeImportResult.create().build() );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/ImportTaskHandlerTest.js", "importNodesXsl" ) )
            .run( TaskId.from( "task" ), progressReporter );

        assertFalse( paramsCaptor.getValue().isImportNodeIds() );
        assertEquals( Map.of( "k", "v" ), paramsCaptor.getValue().getXsltParams() );
    }
}
