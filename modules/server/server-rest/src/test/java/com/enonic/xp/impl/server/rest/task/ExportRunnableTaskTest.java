package com.enonic.xp.impl.server.rest.task;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.home.HomeDirSupport;
import com.enonic.xp.impl.server.rest.model.ExportNodesRequestJson;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.util.BinaryReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExportRunnableTaskTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    @TempDir
    public Path temporaryFolder;

    private ExportService exportService;

    @BeforeEach
    void setUp()
    {
        HomeDirSupport.set( temporaryFolder );

        this.exportService = mock( ExportService.class );
    }

    private ExportRunnableTask createTask( final ExportNodesRequestJson params )
    {
        return ExportRunnableTask.create()
            .exportService( exportService )
            .repositoryId( params.getSourceRepoPath().getRepositoryId() )
            .branch( params.getSourceRepoPath().getBranch() )
            .nodePath( params.getSourceRepoPath().getNodePath() )
            .exportName( params.getExportName() )
            .includeVersions( params.isIncludeVersions() )
            .exportWithIds( params.isExportWithIds() )
            .dryRun( params.isDryRun() )
            .build();
    }

    @Test
    void exportNodes()
    {
        final NodeExportResult nodeExportResult = NodeExportResult.create()
            .addNodePath( NodePath.create().addElement( "node" ).addElement( "path" ).build() )
            .addBinary( NodePath.create().elements( "binary" ).build(), BinaryReference.from( "binaryRef" ) )
            .build();

        when( this.exportService.exportNodes( any( ExportNodesParams.class ) ) ).thenReturn( nodeExportResult );

        final ExportRunnableTask task = createTask( new ExportNodesRequestJson( "a:b:c", "export", true, true, true ) );

        ProgressReporter progressReporter = mock( ProgressReporter.class );

        task.run( TaskId.from( "taskId" ), progressReporter );

        final ArgumentCaptor<String> progressReporterCaptor = ArgumentCaptor.forClass( String.class );
        verify( progressReporter, times( 1 ) ).info( progressReporterCaptor.capture() );

        final String result = progressReporterCaptor.getValue();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "exportNodes_result.json" ), jsonTestHelper.stringToJson( result ) );
    }

}
