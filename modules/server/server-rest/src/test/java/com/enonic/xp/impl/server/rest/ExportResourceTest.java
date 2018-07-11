package com.enonic.xp.impl.server.rest;

import java.io.File;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.util.BinaryReference;

import static org.mockito.Matchers.isA;

public class ExportResourceTest
    extends ServerRestTestSupport
{
    private ExportService exportService;

    private RepositoryService repositoryService;

    private NodeRepositoryService nodeRepositoryService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setup()
        throws Exception
    {
        final File homeDir = this.temporaryFolder.newFolder( "home" );
        System.setProperty( "xp.home", homeDir.getAbsolutePath() );
    }

    @Override
    protected ExportResource getResourceInstance()
    {
        exportService = Mockito.mock( ExportService.class );
        repositoryService = Mockito.mock( RepositoryService.class );
        nodeRepositoryService = Mockito.mock( NodeRepositoryService.class );

        final ExportResource resource = new ExportResource();
        resource.setExportService( exportService );
        resource.setRepositoryService( repositoryService );
        resource.setNodeRepositoryService( nodeRepositoryService );

        return resource;
    }

    @Test
    public void exportNodes()
        throws Exception
    {

        final NodeExportResult nodeExportResult = NodeExportResult.create().
            addNodePath( NodePath.create().addElement( "node" ).addElement( "path" ).build() ).
            addBinary( NodePath.create().elements( "binary" ).build(), BinaryReference.from( "binaryRef" ) ).
            build();

        Mockito.when( this.exportService.exportNodes( isA( ExportNodesParams.class ) ) ).thenReturn( nodeExportResult );

        final String result = request().path( "repo/export" ).
            entity( readFromFile( "export_nodes_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "export_nodes_result.json", result );
    }

    @Test
    public void importNodes()
        throws Exception
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

        final String result = request().path( "repo/import" ).
            entity( readFromFile( "import_nodes_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( repositoryService, Mockito.times( 1 ) ).invalidateAll();

        Mockito.verify( nodeRepositoryService, Mockito.times( 1 ) ).isInitialized( RepositoryId.from( "system-repo" ) );
        Mockito.verify( nodeRepositoryService, Mockito.times( 1 ) ).create(
            CreateRepositoryParams.create().repositoryId( RepositoryId.from( "system-repo" ) ).repositorySettings(
                RepositorySettings.create().build() ).build() );

        assertJson( "import_nodes_result.json", result );
    }
}
