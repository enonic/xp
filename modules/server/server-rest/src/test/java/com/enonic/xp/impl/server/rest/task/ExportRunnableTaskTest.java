package com.enonic.xp.impl.server.rest.task;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
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

import static org.mockito.Matchers.isA;

public class ExportRunnableTaskTest
    extends AbstractRunnableTaskTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ExportService exportService;

    @Before
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        final File homeDir = this.temporaryFolder.newFolder( "home" );
        System.setProperty( "xp.home", homeDir.getAbsolutePath() );

        this.exportService = Mockito.mock( ExportService.class );
    }

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
