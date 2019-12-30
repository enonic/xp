package com.enonic.xp.impl.server.rest.task;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.impl.server.rest.model.ImportNodesRequestJson;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.task.AbstractRunnableTaskTest;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.util.BinaryReference;

import static org.mockito.ArgumentMatchers.isA;

public class ImportRunnableTaskTest
    extends AbstractRunnableTaskTest
{
    @TempDir
    public Path temporaryFolder;

    private ExportService exportService;

    private RepositoryService repositoryService;

    private NodeRepositoryService nodeRepositoryService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        final Path homeDir = Files.createDirectory( this.temporaryFolder.resolve( "home" ) ).toAbsolutePath();
        System.setProperty( "xp.home", homeDir.toString() );

        this.exportService = Mockito.mock( ExportService.class );
        this.repositoryService = Mockito.mock( RepositoryService.class );
        this.nodeRepositoryService = Mockito.mock( NodeRepositoryService.class );
    }

    @Override
    protected ImportRunnableTask createAndRunTask()
    {
        return null;
    }

    protected ImportRunnableTask createAndRunTask( final ImportNodesRequestJson params )
    {
        final ImportRunnableTask task = ImportRunnableTask.create().
            description( "import" ).
            taskService( taskService ).
            exportService( exportService ).
            repositoryService( repositoryService ).
            nodeRepositoryService( nodeRepositoryService ).
            params( params ).
            build();

        task.run( TaskId.from( "taskId" ), progressReporter );

        return task;
    }

    @Test
    public void importNodes()
    {
        NodeImportResult nodeImportResult =
            NodeImportResult.create().added( NodePath.create().addElement( "node" ).addElement( "path" ).build() ).
                addBinary( "binary", BinaryReference.from( "binaryRef" ) ).updated(
                NodePath.create().addElement( "node2" ).addElement( "path2" ).build() ).build();

        Mockito.when( this.exportService.importNodes( isA( ImportNodesParams.class ) ) ).thenReturn( nodeImportResult );

        Mockito.when( this.repositoryService.list() ).thenReturn( Repositories.from( Repository.create().
            branches( Branch.from( "master" ) ).
            id( RepositoryId.from( "system-repo" ) ).
            build() ) );

        final ImportRunnableTask task =
            createAndRunTask( new ImportNodesRequestJson( "export", "system-repo:master:a", true, true, true, "", null ) );

        task.createTaskResult();

        Mockito.verify( repositoryService, Mockito.times( 1 ) ).invalidateAll();

        Mockito.verify( nodeRepositoryService, Mockito.times( 1 ) ).isInitialized( RepositoryId.from( "system-repo" ) );
        Mockito.verify( nodeRepositoryService, Mockito.times( 1 ) ).create(
            CreateRepositoryParams.create().repositoryId( RepositoryId.from( "system-repo" ) ).repositorySettings(
                RepositorySettings.create().build() ).build() );

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "import" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "importNodes_result.json" ), jsonTestHelper.stringToJson( result ) );
    }

}
