package com.enonic.xp.impl.server.rest.task;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.impl.server.rest.model.ExportNodesRequestJson;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.task.AbstractRunnableTaskTest;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.util.BinaryReference;

import static org.mockito.ArgumentMatchers.isA;

public class ExportRunnableTaskTest
    extends AbstractRunnableTaskTest
{
    @TempDir
    public Path temporaryFolder;

    private ExportService exportService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        final Path homeDir = Files.createDirectory( this.temporaryFolder.resolve( "home" ) ).toAbsolutePath();
        System.setProperty( "xp.home", homeDir.toString() );

        this.exportService = Mockito.mock( ExportService.class );
    }

    @Override
    protected ExportRunnableTask createAndRunTask()
    {
        return null;
    }

    protected ExportRunnableTask createAndRunTask( final ExportNodesRequestJson params )
    {
        final ExportRunnableTask task = ExportRunnableTask.create().
            description( "export" ).
            taskService( taskService ).
            exportService( exportService ).
            params( params ).
            build();

        task.run( TaskId.from( "taskId" ), progressReporter );

        return task;
    }

    @Test
    public void exportNodes()
    {
        final NodeExportResult nodeExportResult = NodeExportResult.create().
            addNodePath( NodePath.create().addElement( "node" ).addElement( "path" ).build() ).
            addBinary( NodePath.create().elements( "binary" ).build(), BinaryReference.from( "binaryRef" ) ).
            build();

        Mockito.when( this.exportService.exportNodes( isA( ExportNodesParams.class ) ) ).thenReturn( nodeExportResult );

        final ExportRunnableTask task = createAndRunTask( new ExportNodesRequestJson( "a:b:c", "export", true, true, true ) );

        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "export" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "exportNodes_result.json" ), jsonTestHelper.stringToJson( result ) );
    }

}
