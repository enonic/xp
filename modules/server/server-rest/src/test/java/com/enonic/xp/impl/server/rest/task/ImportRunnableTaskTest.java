package com.enonic.xp.impl.server.rest.task;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.home.HomeDirSupport;
import com.enonic.xp.impl.server.rest.model.ImportNodesRequestJson;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.util.BinaryReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImportRunnableTaskTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    @TempDir
    public Path temporaryFolder;

    private ExportService exportService;

    private RepositoryService repositoryService;

    private NodeRepositoryService nodeRepositoryService;

    @BeforeEach
    void setUp()
    {
        HomeDirSupport.set( temporaryFolder );
        this.exportService = mock( ExportService.class );
        this.repositoryService = mock( RepositoryService.class );
        this.nodeRepositoryService = mock( NodeRepositoryService.class );
    }

    private ImportRunnableTask createTask( final ImportNodesRequestJson params )
    {
        return ImportRunnableTask.create()
            .repositoryId( params.getTargetRepoPath().getRepositoryId() )
            .branch( params.getTargetRepoPath().getBranch() )
            .nodePath( params.getTargetRepoPath().getNodePath() )
            .exportName( params.getExportName() )
            .importWithIds( params.isImportWithIds() )
            .importWithPermissions( params.isImportWithPermissions() )
            .xslSource( params.getXslSource() )
            .xslParams( params.getXslParams() )
            .nodeRepositoryService( nodeRepositoryService )
            .repositoryService( repositoryService )
            .exportService( exportService )
            .build();
    }

    @Test
    void importNodes()
    {
        NodeImportResult nodeImportResult = NodeImportResult.create()
            .added( new NodePath( "/node/path" ) )
            .addBinary( "/binary", BinaryReference.from( "binaryRef" ) )
            .updated( new NodePath( "/node2/path2" ) )
            .build();

        when( this.exportService.importNodes( any( ImportNodesParams.class ) ) ).thenReturn( nodeImportResult );

        final PropertyTree repoData = new PropertyTree();
        repoData.addString( "key", "value" );

        when( this.repositoryService.list() ).thenReturn( Repositories.from(
            Repository.create().branches( Branch.from( "master" ) ).id( RepositoryId.from( "system-repo" ) ).data( repoData ).build() ) );

        final ImportRunnableTask task = createTask( new ImportNodesRequestJson( "export", "system-repo:master:a", true, true, "", null ) );

        ProgressReporter progressReporter = mock( ProgressReporter.class );
        task.run( TaskId.from( "taskId" ), progressReporter );

        verify( repositoryService, times( 1 ) ).invalidateAll();

        verify( nodeRepositoryService, times( 1 ) ).isInitialized( RepositoryId.from( "system-repo" ) );

        verify( nodeRepositoryService, times( 1 ) ).create( CreateRepositoryParams.create()
                                                                .repositoryId( RepositoryId.from( "system-repo" ) )
                                                                .data( repoData )
                                                                .repositorySettings( RepositorySettings.create().build() )
                                                                .build() );

        final ArgumentCaptor<String> progressReporterCaptor = ArgumentCaptor.forClass( String.class );
        verify( progressReporter, times( 1 ) ).info( progressReporterCaptor.capture() );

        final String result = progressReporterCaptor.getValue();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "importNodes_result.json" ), jsonTestHelper.stringToJson( result ) );
    }

}
